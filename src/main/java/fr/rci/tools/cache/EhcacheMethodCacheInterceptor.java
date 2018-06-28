/**
 * Application : ServicesTechniques<br />
 * <br />
 * Classe/Interface : MethodCacheInterceptor<br />
 * <br />
 * Créée le : 29 mars 2011 par: zntriche<br />
 * Modifiée le : xx/xx/xxxx par: xxxxxxxxxxx<br />
 * xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx<br />
 * Modifiée le : xx/xx/xxxx par: xxxxxxxxxxx<br />
 * xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx<br />
 *
 */

package fr.rci.tools.cache;

import static fr.rci.tools.cache.CacheInterceptorConfiguration.BY_METHOD_CACHE_STRATEGY;
import static fr.rci.tools.cache.CacheInterceptorConfiguration.DISK_CACHE_SUFFIX;
import static fr.rci.tools.cache.CacheInterceptorConfiguration.DOUBLE_ASYNC_CACHE_TYPE;
import static fr.rci.tools.cache.CacheInterceptorConfiguration.DOUBLE_CACHE_TYPE;
import static fr.rci.tools.cache.CacheInterceptorConfiguration.PERMANENT_CACHE_SUFFIX;
import static fr.rci.tools.cache.CacheInterceptorConfiguration.SHARED_CACHE_STRATEGY;

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.management.MBeanServer;

import org.aopalliance.aop.Advice;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.management.ManagementService;

/**
 * Interceptor de cache.
 */
@Component
@Aspect
@Slf4j
public class EhcacheMethodCacheInterceptor{

    @Autowired
    private CacheInterceptorConfiguration config ;

    @Autowired(required=false)
	private CacheManager cacheManager;
    
    @Autowired(required=false)
    private MBeanServer server;	
    
    @Autowired(required=false)
	private ExecutorService executor;
    
    private CachedValueStrategy cachedValueStrategy ;
	
	private final ThreadLocal<StringBuilder> buffer = new ThreadLocal<StringBuilder>() {
    	protected StringBuilder initialValue() {
    		return new StringBuilder() ;
    	}
    } ;

	private CacheKeyProvider cacheKeyProvider;
	
    /**
     *
     * Constructeur par defaut.
     */
    public EhcacheMethodCacheInterceptor() {
        super();

    }

    /**
     * {@inheritDoc}
     */
    @PostConstruct
    public void init() {
    	try {
			cacheKeyProvider = config.getCacheKeyProvider().newInstance() ;
			cachedValueStrategy = config.getCacheValueStrategy().getConstructor(CacheInterceptorConfiguration.class).newInstance(config) ;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			log.error("Unable to configure cache interceptor", e);
		}
    	
        if (server == null) {
        	log.warn("No MBean Server provided, using Platform one") ;
        	server = ManagementFactory.getPlatformMBeanServer() ;
        }

        if (cacheManager == null) {
        	log.warn("No CacheManager provided, using shared default one") ;
        	cacheManager = CacheManager.getInstance() ;
        }

        try {
            ManagementService.registerMBeans(cacheManager, server, true, true, true, true);
        } catch (CacheException e) {
            log.warn("Failed registering JMX.", e);
        }
        
        if (executor == null) {
        	executor = Executors.newFixedThreadPool(config.getAsynchThreadCount()) ;
        }
        
    }
    
    @Around("@annotation(fr.rci.tools.cache.Cached) || @within(fr.rci.tools.cache.Cached)")
    public Object cache(final ProceedingJoinPoint invocation) throws Throwable {
    	Object result;

        String methodName = invocation.getSignature().getName();
        String targetName = getTargetName(invocation);
        String cacheName = getCacheName(targetName, methodName);
        CacheKey cacheKey = cacheKeyProvider.getCacheKey(targetName, methodName, invocation.getArgs());
        
        Ehcache cache = cacheManager.addCacheIfAbsent(cacheName);
		String diskCacheName = cacheName+DISK_CACHE_SUFFIX;
		Ehcache cacheDisk = cacheManager.getEhcache(diskCacheName) ;

        log.debug("looking for method result in cache {}", cache.getName());
        Element element = cache.get(cacheKey);
    	logCacheUsage("mem", cacheName, cacheKey, element);
        
        if (element == null && cacheDisk != null) {
        	log.debug("element not found in principal cache, looking in disk cache");
        	element = cacheDisk.get(cacheKey);
        	logCacheUsage("disk", diskCacheName, cacheKey, element);
        }
        
        if (element == null) {
            try {
            	if (DOUBLE_ASYNC_CACHE_TYPE.equals(config.getCacheType())) {
        			log.debug("Trying permanent cache {}", getPermanentCacheName(cacheName));
            		element = tryFromPermanentCache(cacheKey, cacheName, null);
                	if (element != null) {
            			log.debug("Using permanent cache value and submitting refresh task");
            			executor.submit(new CacheRefreshTask(cacheName, cacheKey, invocation)) ;
            			return cachedValueStrategy.fromCachedValue(invocation, (Cacheable) element.getObjectValue());
            		}
            	}
            	log.debug("cache miss, calling intercepted method");
                result = invocation.proceed();
                cacheResult(invocation, result, cacheName, cacheKey);
                return result ;
            } catch (Exception e) {
        		if (DOUBLE_CACHE_TYPE.equals(config.getCacheType()) || DOUBLE_ASYNC_CACHE_TYPE.equals(config.getCacheType())) {
	                element = tryFromPermanentCache(cacheKey, cacheName, e);
        		}
        		else {
        			throw e ;
        		}
            }
        } 
        else {
            log.debug("using result from cache");
        }

        return cachedValueStrategy.fromCachedValue(invocation, (Cacheable) element.getObjectValue());
    }

	protected void logCacheUsage(String type, String cacheName, CacheKey cacheKey, Element element) {
        log.debug("{} {} for cache '{}', CacheKey.hashCode={}", type, element == null ? "miss" : "hit", cacheName, cacheKey.hashCode());
	}

	protected void cacheResult(ProceedingJoinPoint invocation, Object result, String cacheName, CacheKey cacheKey) {
		Cacheable cacheableValue = cachedValueStrategy.toCachedValue(invocation, result);
		if (cacheableValue != null) {
			// Choix du cache selon la taille a mettre en cache
			Ehcache cache ;
			if (cacheableValue.getSize() > config.getMaxInmemoryCacheableSize()) {
				String cacheNameDisk = cacheName+DISK_CACHE_SUFFIX;
				log.debug("element size {} too large for memory, storing in cache {}", cacheableValue.getSize(), cacheNameDisk);
				cache = cacheManager.addCacheIfAbsent(cacheNameDisk) ;
			}	
			else {
				cache = cacheManager.addCacheIfAbsent(cacheName) ;
			}

			Element element = new Element(cacheKey, cacheableValue);
			cache.put(element);

			if (DOUBLE_CACHE_TYPE.equals(config.getCacheType()) || DOUBLE_ASYNC_CACHE_TYPE.equals(config.getCacheType())) {
				log.debug("storing element with key {} in permanent cache", cacheKey.hashCode());
				String pcName = getPermanentCacheName(cacheName);
				Ehcache permanentCache = cacheManager.addCacheIfAbsent(pcName);
				CacheConfiguration cacheConfiguration = permanentCache.getCacheConfiguration();
				if (!cacheConfiguration.isEternal() || !cacheConfiguration.isDiskPersistent()) {
					log.warn("Invalid configuration for cache {} : not ethernal or diskPersistent", pcName);
				}
				permanentCache.put(element);
			}
		}
	}

	protected Element tryFromPermanentCache(CacheKey cacheKey, String cacheName, Exception e) throws Exception {
		String name = getPermanentCacheName(cacheName) ;
	    Ehcache permanentCache = cacheManager.addCacheIfAbsent(name);
	    Element element = permanentCache.get(cacheKey);
    	logCacheUsage("permanent", name, cacheKey, element);

	    if (element == null && e != null) {
    		throw e;
	    }

	    return element;
	}

	public String getPermanentCacheName(String name) {
		return StringUtils.isEmpty(config.getPermanentCacheName()) ? name+PERMANENT_CACHE_SUFFIX : config.getPermanentCacheName() ;
	}

	protected String getTargetName(final ProceedingJoinPoint invocation) {
		String targetName;
		if (config.isUseFirstInterfaceAsName()) {
            targetName = invocation.getThis().getClass().getInterfaces()[0].getName();
        } else {
            targetName = invocation.getThis().getClass().getName();
        }
		return targetName;
	}

	protected String getCacheName(String targetName, String methodName) {
		String cacheName;
		if (SHARED_CACHE_STRATEGY.equals(config.getCacheStrategy())) {
            cacheName = config.getSharedCacheName();
        } else if (BY_METHOD_CACHE_STRATEGY.equals(config.getCacheStrategy())) {
        	StringBuilder builder = buffer.get() ;
        	builder.setLength(0);
        	cacheName = builder.append(targetName).append(".").append(methodName).toString();
        } else { // BY_SERVICE_CACHE_STRATEGY
        	cacheName = targetName ;
        }
		return cacheName;
	}

	private final class CacheRefreshTask implements Runnable {
		private final String cacheName;
		private final CacheKey cacheKey;
		private final ProceedingJoinPoint invocation;

		private CacheRefreshTask(String cacheName, CacheKey cacheKey,
				ProceedingJoinPoint invocation) {
			this.cacheName = cacheName;
			this.cacheKey = cacheKey;
			this.invocation = invocation;
		}

		@Override
		public void run() {
		    Object result;
			try {
				result = invocation.proceed();
				cacheResult(invocation, result, cacheName, cacheKey);				
			} catch (Throwable e) {
				log.debug("Unable to get result in CacheRefreshTask");
			}
		}
	}
}
