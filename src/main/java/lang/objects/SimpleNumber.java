package lang.objects;

import lang.interfaces.Compare;
import lang.interfaces.Primitive;

/**
 * Created by Josh on 16/03/2016.
 */
public abstract class SimpleNumber<T extends SimpleNumber> extends SimpleObject implements Primitive, Compare<SimpleNumber, T>, Comparable<SimpleNumber> {
    abstract double asDouble();
    abstract int asInteger();
    abstract float asFloat();
}
