package translator;

/**
 * Created by josking on 3/2/16.
 */
public class Token {
    enum Type {
        OpenBrace("{"),
        CloseBrace("}"),
        OpenParenthesis("("),
        CloseParenthesis(")"),
        OpenBracket("("),
        CloseBracket(")"),
        Dot("."),
        Colon(":"),
        Semicolon(";"),
        Float("\\d+\\.\\d+", true),
        Integer("\\d+", true),
        DoubleQuote("\""),
        SingleQuote("'"),
        WhiteSpace("\\s+", true),
        KeywordTrue("true"),
        KeywordFalse("false"),
        KeywordIf("if"),
        KeywordClass("class"),
        KeywordFor("for"),
        KeywordImport("import"),
        KeywordInt("int"),
        KeywordBoolean("boolean"),
        BooleanEquals("=="),
        BooleanNotEquals("!="),
        BooleanNot("!"),
        BooleanAnd("&&"),
        BooleanOr("||"),
        LessThan("<"),
        GreaterThan(">"),
        Times("*"),
        Minus("-"),
        Plus("+"),
        Divide("/"),
        Mod("%"),
        Power("**"),
        ArithmeticEquals("="),
        ArithmeticNot("!"),
        BitwiseOr("|"),
        BitwiseAnd("&"),
        BitwiseNot("~"),
        BitwiseXor("^"),
        Variable("[a-zA-Z][a-zA-Z]*", true),
        LineComment("//.*", true),
        OpenComment("/*"),
        CloseComment("*/"),
        Unknown(".*", true)
        ;

        String value;
        boolean isRegex;

        Type(String value) {
            this(value, false);
        }

        Type(String value, boolean isRegex) {
            this.value = value;
            this.isRegex = isRegex;
        }

        public static Type fromString(String str) {
            for (Type t : Type.values()) {
                if (t.isRegex && str.matches(t.value)) return t;
                else if (!t.isRegex && str.equals(t.value)) return t;
            }
            return null;
        }
    }

    String text;
    Type type;

    public Token(String text) {
        this.text = text;
        type = Type.fromString(text);
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}
