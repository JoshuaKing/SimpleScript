package lang.objects;

import lang.interfaces.Primitive;

public final class SimpleInteger extends SimpleNumber<SimpleInteger> {
    int value;

    public SimpleInteger(int value) {
        this.value = value;
    }

    public SimpleInteger plus(SimpleInteger other) {
        return new SimpleInteger(value + other.value);
    }

    @Override
    public SimpleInteger toInteger() {
        return this;
    }

    @Override
    public SimpleFloat toFloat() {
        return new SimpleFloat(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean toBoolean() {
        return value != 0;
    }

    @Override
    public int compareTo(SimpleNumber o) {
        return java.lang.Integer.compare(value, o.toInteger().value);
    }

    @Override
    public SimpleInteger greater(SimpleNumber other) {
        return compareTo(other) == 1 ? this : other.toInteger();
    }

    @Override
    public SimpleInteger lesser(SimpleNumber other) {
        return compareTo(other) == -1 ? this : other.toInteger();
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public int asInteger() {
        return value;
    }

    @Override
    public float asFloat() {
        return value;
    }

    public static SimpleInteger valueOf(Object o) throws ClassCastException {
        if (o instanceof Primitive) return ((Primitive) o).toInteger();
        if (o instanceof Integer) return new SimpleInteger((Integer) o);
        throw new ClassCastException("Cannot cast " + o.getClass().getSimpleName() + " to " + SimpleInteger.class.getSimpleName());
    }
}
