package fr.rci.tools.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.export.annotation.ManagedResource;

import lombok.Data;

@Data
@ManagedResource
@ConfigurationProperties("rci.cache")
public class CacheInterceptorConfiguration {

    public static final String BY_SERVICE_CACHE_STRATEGY = "by-service";
    public static final String BY_METHOD_CACHE_STRATEGY = "by-method";
    public static final String SHARED_CACHE_STRATEGY = "shared";
    public static final Class<? extends CachedValueStrategy> REFERENCE_CACHED_VALUE_STRATEGY = ReferenceCachedValueStrategy.class;
    public static final Class<? extends CachedValueStrategy> SERIALISATION_CACHED_VALUE_STRATEGY = SerialisationCachedValueStrategy.class;
    public static final String SINGLE_CACHE_TYPE = "single";
    public static final String DOUBLE_CACHE_TYPE = "double";
    public static final String DOUBLE_ASYNC_CACHE_TYPE = "double-async";
    public static final String DISK_CACHE_SUFFIX = "-disk";
    public static final String PERMANENT_CACHE_SUFFIX = "-perm";
    
    private static final String DEFAULT_PERMENANT_CACHE_NAME = "shared-perm";
    private static final Class<? extends CachedValueStrategy> DEFAULT_CACHED_VALUE_STRATEGIE = REFERENCE_CACHED_VALUE_STRATEGY;
    private static final String DEFAULT_CACHE_STRATEGY = BY_SERVICE_CACHE_STRATEGY;
    private static final String DEFAULT_CACHE_TYPE = SINGLE_CACHE_TYPE;
    private static final String DEFAULT_SHARED_CACHE_NAME = "defaut-shared-cache-method-interceptor";
    private static final int DEFAULT_MAX_CACHEABLE_SIZE = 50 * 1024 * 1024; // 500 Mo
    private static final int DEFAULT_IN_MEMORY_MAX_CACHEABLE_SIZE = 5 * 1024 * 1024; // 5 Mo
	private static final boolean DEFAULT_CACHE_NULL_VALUES = false;
	private static final boolean DEFAULT_USE_FIRST_INTERFACE_AS_NAME = true;
	private static final int DEFAULT_ASYNC_THREAD_COUNT = 8 ;
	
	private Class<? extends CacheKeyProvider> cacheKeyProvider = fr.rci.tools.cache.DefaultCacheKeyProvider.class ;
	private String cacheStrategy = DEFAULT_CACHE_STRATEGY;
	private boolean useFirstInterfaceAsName = DEFAULT_USE_FIRST_INTERFACE_AS_NAME ;
	private Class<? extends CachedValueStrategy> cacheValueStrategy = DEFAULT_CACHED_VALUE_STRATEGIE;
	private String sharedCacheName = DEFAULT_SHARED_CACHE_NAME;
	private String cacheType = DEFAULT_CACHE_TYPE ;
	private String permanentCacheName = DEFAULT_PERMENANT_CACHE_NAME;
	private boolean cacheNullValues = DEFAULT_CACHE_NULL_VALUES;
	private int maxCacheableSize= DEFAULT_MAX_CACHEABLE_SIZE;
	private int maxInmemoryCacheableSize = DEFAULT_IN_MEMORY_MAX_CACHEABLE_SIZE;
	private int asynchThreadCount = DEFAULT_ASYNC_THREAD_COUNT;

}