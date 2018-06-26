package fr.rci.tools.cache;

public interface CacheKeyProvider {

	CacheKey getCacheKey(String targetName, String methodName, Object[] arguments);

}
