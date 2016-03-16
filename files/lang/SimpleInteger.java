package lang;

public final class SimpleInteger {
    int value;

    public SimpleInteger(int value) {
        this.value = value;
    }

    public static SimpleInteger valueOf(int value) {
        return new SimpleInteger(value);
    }

    public static SimpleInteger valueOf(SimpleInteger simpleInteger) {
        return new SimpleInteger(simpleInteger.value);
    }
    
    public SimpleInteger plus(SimpleInteger other) {
        return new SimpleInteger(value + other.getValue());
    }

    public int getValue() {
        return value;
    }
}
