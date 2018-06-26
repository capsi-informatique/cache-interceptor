package fr.rci.tools.cache;

import org.aspectj.lang.ProceedingJoinPoint;

public interface CachedValueStrategy {
	public Cacheable toCachedValue(ProceedingJoinPoint invocation, final Object element) ;
    public Object fromCachedValue(ProceedingJoinPoint invocation, Cacheable cachedElement) ;
}
