package fr.rci.tools.cache.test;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.rci.tools.cache.Cached;

/**
 *
 */
@Service
public class SimpleTestServiceImpl implements SimpleTestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTestServiceImpl.class);

    private final Map<String, Integer> counts = new HashMap<String, Integer>();

    /**
     *
     * @see ReplacementProviderTestService#replace(java.lang.String)
     */
    @Override
    @Cached
    public ComputedValue compute(final String arg) {
        return new ComputedValue("computed value of " + arg + " by thread " + Thread.currentThread().getName());
    }

    /**
     * @see fr.rci.tools.cache.test.orange.voices.technical.simple.SimpleTestService#action()
     */
    @Override
    @Cached
    public void action() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("done");
        }
    }

    @Override
    @Cached
    public int getCallCount(final String etiquette, final int index) {
        return getCallCountNoCache(etiquette, index);

    }

    @Override
    public int getCallCountNoCache(final String etiquette, final int index) {
        String key = etiquette + index;
        Integer res = counts.get(key);
        if (res == null) {
            res = 0;
        }
        res++;
        counts.put(key, res);
        return res;

    }

	@Override
//    @Cached
	public void getCallCount(String etiquette) {
		getCallCountNoCache(etiquette, 1);
	}

	@Override
    @Cached
	public byte[] getLargeValue(String etiquette, byte[] bs) {
		return bs;
	}

}
