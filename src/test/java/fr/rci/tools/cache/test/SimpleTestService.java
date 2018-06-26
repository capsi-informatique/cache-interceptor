package fr.rci.tools.cache.test;

/**
 *
 */
public interface SimpleTestService {

    /**
     *
     */
    ComputedValue compute(String arg);

    /**
     *
     */
    void action();

    public int getCallCount(String etiquette, int index);

    public int getCallCountNoCache(String etiquette, int index);

    public void getCallCount(String etiquette);

	byte[] getLargeValue(String etiquette, byte[] bs);

}
