package fr.rci.tools.cache;

import java.io.Serializable;
import java.util.Arrays;

import javax.xml.ws.Holder;

/**
 * Key used to for the cache, based on the service de base, the method of the service, and the object being retrieved.
 */
public class CacheKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int THIRTEEN = 13;

    private static final int NINETEEN = 19;

    private String service;

    private String method;

    private Object[] parameters;

    private volatile int hashCode;

    /**
     * Constructeur. Genere une cle composite.
     * 
     * @param service nom du service
     * @param method nom de la methode
     * @param parameters liste de parametres passes a la methode
     */
    public CacheKey(final String service, final String method, final Object[] parameters) {
        super();
        this.service = service;
        this.method = method;
        this.parameters = parameters;
    }

    /**
     * Equals.
     */
    @Override
    public boolean equals(final Object o) {
        if (o != null && o.getClass().equals(this.getClass())) {
            CacheKey cacheKey = (CacheKey) o;
            return service.equals(cacheKey.service) && method.equals(cacheKey.method)
                    && Arrays.deepEquals(cacheKey.parameters, this.parameters);
        }
        return false;
    }

	/**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int result = THIRTEEN;
            result = NINETEEN * result + service.hashCode();
            result = NINETEEN * result + method.hashCode();
            result = NINETEEN * result + Arrays.deepHashCode(parameters);
            hashCode = result;
        }
        return hashCode;
    }

 	/**
     * Affichage du contenu de l'objet sous forme de String.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[cacheKey =[service =");
        sb.append(service);
        sb.append(";method =");
        sb.append(method);
        sb.append(";parameters= ");
        sb.append(Arrays.toString(parameters));
        sb.append(";hashcode = ");
        sb.append(hashCode);
        sb.append("]]");
        return sb.toString();
    }
}
