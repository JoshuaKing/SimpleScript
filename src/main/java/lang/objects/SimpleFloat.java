package lang.objects;

import lang.interfaces.Primitive;

/**
 * Created by Josh on 16/03/2016.
 */
public class SimpleFloat extends SimpleNumber<SimpleFloat> {
    float value;

    public SimpleFloat(float f) {
        value = f;
    }

    public SimpleFloat(double d) {
        value = (float) d;
    }

    @Override
    public SimpleInteger toInteger() {
        return new SimpleInteger((int) value);
    }

    @Override
    public SimpleFloat toFloat() {
        return this;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean toBoolean() {
        return false;
    }
    @Override
    public int compareTo(SimpleNumber o) {
        return java.lang.Float.compare(value, o.toFloat().value);
    }

    @Override
    public SimpleFloat greater(SimpleNumber other) {
        return compareTo(other) == 1 ? this : other.toFloat();
    }

    @Override
    public SimpleFloat lesser(SimpleNumber other) {
        return compareTo(other) == -1 ? this : other.toFloat();
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public int asInteger() {
        return (int) value;
    }

    @Override
    public float asFloat() {
        return value;
    }


    public static SimpleFloat valueOf(Object o) throws ClassCastException {
        if (o instanceof Primitive) return ((Primitive) o).toFloat();
        if (o instanceof Float) return new SimpleFloat((Float) o);
        if (o instanceof Integer) return new SimpleFloat((Integer) o);
        throw new ClassCastException("Cannot cast " + o.getClass().getSimpleName() + " to " + SimpleInteger.class.getSimpleName());
    }
}
