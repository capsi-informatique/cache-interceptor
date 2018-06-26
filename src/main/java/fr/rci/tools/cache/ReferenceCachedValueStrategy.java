package fr.rci.tools.cache;

import java.io.Serializable;

public class ReferenceCachedValueStrategy extends AbstractCachedValueStrategy {

	public ReferenceCachedValueStrategy(CacheInterceptorConfiguration config) {
		super(config);
	}

	@Override
	public Object fromCachedValue(Cacheable cachedElement) {
		return cachedElement.getValue();
	}

	@Override
	public Cacheable toCachedValue(Object element) {
		return new Cacheable(-1, (Serializable) element);
	}

}
