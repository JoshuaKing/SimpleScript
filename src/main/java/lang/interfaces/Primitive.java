package lang.interfaces;

import lang.objects.SimpleFloat;
import lang.objects.SimpleInteger;

/**
 * Created by Josh on 17/03/2016.
 */
public interface Primitive extends Object {
    SimpleInteger toInteger();
    SimpleFloat toFloat();
    String toString();
    boolean toBoolean();
}
