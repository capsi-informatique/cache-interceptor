package fr.rci.tools.cache;

public class DefaultCacheKeyProvider implements CacheKeyProvider {

	@Override
	public CacheKey getCacheKey(String targetName, String methodName, Object[] arguments) {
		return new CacheKey(targetName, methodName, arguments) ;
	}

}
