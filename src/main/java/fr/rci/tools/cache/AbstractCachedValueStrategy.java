package fr.rci.tools.cache;

import java.io.Serializable;

import org.aspectj.lang.ProceedingJoinPoint;

public abstract class AbstractCachedValueStrategy implements CachedValueStrategy {
    public static final Serializable NULL_PLACEHOLDER = "####NULL#####";
    
	protected CacheInterceptorConfiguration config;

	public AbstractCachedValueStrategy(CacheInterceptorConfiguration config) {
		super();
		this.config = config;
	}
	
	@Override
	public Object fromCachedValue(ProceedingJoinPoint invocation, Cacheable cachedElement) {
    	if (NULL_PLACEHOLDER.equals(cachedElement.getValue()) && config.isCacheNullValues()) {
    		return null ;
    	}
    	else {
    		return fromCachedValue(cachedElement) ;
    	}
	}
	
	public abstract Object fromCachedValue(Cacheable cachedElement) ;	
	
	@Override
	public Cacheable toCachedValue(ProceedingJoinPoint invocation, Object element) {
    	if (element == null) {
    		if (config.isCacheNullValues()) {
    			return new Cacheable(-1, NULL_PLACEHOLDER) ;
    		}
    	}
    	return toCachedValue(element) ;
	}
	
	public abstract Cacheable toCachedValue(Object element) ;
	
}
