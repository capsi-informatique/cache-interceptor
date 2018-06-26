package fr.rci.tools.cache;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.rci.tools.cache.CacheKey;

/**
 * JUnit Test de l'objet CacheKey.
 */
public class CacheKeyTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheKeyTest.class);

    private static final Object[] OBJET = new String[] { "03306300" };

    private static final Object[] OBJET1 = new String[] { "03306400" };

    private static final Object[] OBJET2 = new Object[2];

    private static final Object[] OBJET3 = new Object[] { new int[] { 0, 1 } };

    private static final Object[] OBJET4 = new Object[] { new int[] { 0, 1 } };

    private static final String SERVICE = "SBGeographique";

    private static final String METHOD = "recupereLocalite";

    private CacheKey key1;

    private CacheKey key2;

    /**
     * Before Class.
     */
    @BeforeClass
    public static void beforeClass() {
    }

    /**
     * Test le bon comportement de la méthode équals.
     */
    @Test
    public void testEqualsMethodeValide() {

        LOGGER.debug("Test methode : equals");

        this.key1 = new CacheKey(SERVICE, METHOD, OBJET);
        this.key2 = new CacheKey(SERVICE, METHOD, OBJET);

        Assert.assertTrue(key1.equals(key2));
    }

    /**
     * Test le bon comportement de la méthode équals.
     */
    @Test
    public void testEqualsMethodeValide2() {

        LOGGER.debug("Test methode : equals");

        this.key1 = new CacheKey(SERVICE, METHOD, OBJET3);
        this.key2 = new CacheKey(SERVICE, METHOD, OBJET4);

        Assert.assertTrue(key1.equals(key2));
    }

    /**
     * Test le bon comportement de la méthode équals Test que la méthode 'equals' retourne false quand les deux sont
     * créés avec des paramètres différents.
     */
    @Test
    public void testEqualsMethodeInvalide() {

        LOGGER.debug("Test methode : equals invalide");

        this.key1 = new CacheKey(SERVICE, METHOD, OBJET);
        this.key2 = new CacheKey(SERVICE, METHOD, OBJET1);

        Assert.assertFalse(key1.equals(key2));
    }

    /**
     * Test le bon comportement de la méthode équals Test que la méthode 'equals' retourne false quand les deux sont
     * créés avec des paramètres différents.
     */
    @Test
    public void testEqualsMethodeInvalide2() {

        LOGGER.debug("Test methode : equals invalide");

        this.key1 = new CacheKey(SERVICE, METHOD, OBJET);
        this.key2 = new CacheKey(SERVICE, METHOD, OBJET2);

        Assert.assertFalse(key1.equals(key2));
    }

    /**
     * Test le bon comportement de la méthode équals Test que la méthode 'equals' retourne false quand un objet est
     * null.
     */
    @Test
    public void testEqualsMethodeInvalide3() {

        LOGGER.debug("Test methode : equals invalide");

        this.key1 = new CacheKey(SERVICE, METHOD, OBJET);

        Assert.assertFalse(key1 == null);
    }

    /**
     * Test le bon comportement de la méthode hashcode Test que les valeurs de hashcodes de deux clés, créées avec les
     * mêmes paramètres sont identiques.
     */
    @Test
    public void testHashCodeMethodeValide() {

        LOGGER.debug("Test methode : HasCode ");

        this.key1 = new CacheKey(SERVICE, METHOD, OBJET);
        this.key2 = new CacheKey(SERVICE, METHOD, OBJET);

        Assert.assertEquals(key1.hashCode(), key2.hashCode());
    }

    /**
     * Test le bon comportement de la méthode hashcode Test que les valeurs de hashcodes de deux clés, créées avec les
     * mêmes paramètres sont identiques.
     */
    @Test
    public void testHashCodeMethodeValide2() {

        LOGGER.debug("Test methode : HasCode ");

        this.key1 = new CacheKey(SERVICE, METHOD, OBJET3);
        this.key2 = new CacheKey(SERVICE, METHOD, OBJET4);

        Assert.assertEquals(key1.hashCode(), key2.hashCode());
    }

    /**
     * Test la non égalité des valeurs de hashcodes de deux clés, créées avec des paramètres différents.
     */
    @Test
    public void testHashCodeMethodeInvalide() {

        LOGGER.debug("Test methode : HasCode invalide");

        this.key1 = new CacheKey(SERVICE, METHOD, OBJET);
        this.key2 = new CacheKey(SERVICE, METHOD, OBJET1);

        Assert.assertNotSame(key1.hashCode(), key2.hashCode());
    }

}
