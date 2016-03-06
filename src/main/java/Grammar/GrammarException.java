package Grammar;

/**
 * Created by Josh on 3/03/2016.
 */
public class GrammarException extends Exception {
    boolean fatal = false;

    public GrammarException(String err, boolean fatal) {
        super(err);

        this.fatal = fatal;
    }
}
