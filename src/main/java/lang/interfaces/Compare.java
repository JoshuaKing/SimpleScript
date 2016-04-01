package lang.interfaces;

/**
 * Created by Josh on 17/03/2016.
 */
public interface Compare<T, R> {
    R greater(T other);
    R lesser(T other);
}
