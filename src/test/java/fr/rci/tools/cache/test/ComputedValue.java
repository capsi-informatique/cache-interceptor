package fr.rci.tools.cache.test;

/**
 *
 */
public class ComputedValue {

    private String value;

    /**
     *
     */
    public ComputedValue() {
    }

    /**
     *
     */
    public ComputedValue(final String value) {
        super();
        this.value = value;
    }

    /**
    *
    */
    @Override
    public String toString() {
        return value;
    }

}
