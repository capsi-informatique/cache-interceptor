package fr.rci.tools.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SerialisationCachedValueStrategy extends AbstractCachedValueStrategy {

    private final ThreadLocal<ByteArrayOutputStream> byteBuffers = new ThreadLocal<ByteArrayOutputStream>() {
    	@Override
    	protected ByteArrayOutputStream initialValue() {
    		return new ByteArrayOutputStream();
    	}
    } ;
	
	public SerialisationCachedValueStrategy(CacheInterceptorConfiguration config) {
		super(config);
	}

	@Override
	public Object fromCachedValue(Cacheable cachedElement) {
		Serializable value = cachedElement.getValue();
		if (value instanceof byte[]) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream((byte[]) value));
				return ois.readObject();
			} catch (Exception e) {
				log.error("Error reading serialised element, using REFERENCE_CACHED_VALUE_STRATEGIE", e);
				throw new IllegalArgumentException(e) ;
			}
		} else {
			return value;
		}
	}

	@Override
	public Cacheable toCachedValue(Object element) {
    	ByteArrayOutputStream cacheBuffer = byteBuffers.get();
    	try {
            cacheBuffer.reset();
            ObjectOutputStream oos = new ObjectOutputStream(cacheBuffer);
            oos.writeObject(element);
            oos.flush();
            if (cacheBuffer.size() > config.getMaxCacheableSize()) {
                log.debug("Element too large to be stored in cache.");
                return null;
            } else {
                return new Cacheable(cacheBuffer.size(), cacheBuffer.toByteArray());
            }
        } catch (IOException e) {
            log.error("Element is not serializable, using REFERENCE_CACHED_VALUE_STRATEGIE for " + element);
            throw new IllegalArgumentException(e) ;
        }
        finally {
        	if (cacheBuffer.size() > 10*1024*1024) {
        		try {
            		byteBuffers.get().close();
            		byteBuffers.remove();
        		}
        		catch (Exception ignore) {
				}
        	}
        }
	}

}
