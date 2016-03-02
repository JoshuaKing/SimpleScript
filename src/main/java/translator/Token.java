package translator;

/**
 * Created by josking on 3/2/16.
 */
public class Token {
    enum Type {
        WhiteSpace("\\s+", true),
        OpenBrace("{"),
        CloseBrace("}"),
        OpenParenthesis("("),
        CloseParenthesis(")"),
        OpenBracket("("),
        CloseBracket(")"),
        Dot("."),
        Colon(":"),
        Semicolon(";"),
        TypeFloat("\\d+\\.\\d+", true),
        TypeInteger("\\d+", true),
        DoubleQuote("\""),
        SingleQuote("'"),
        KeywordTrue("true"),
        KeywordFalse("false"),
        KeywordIf("if"),
        KeywordClass("class"),
        KeywordFor("for"),
        KeywordImport("import"),
        KeywordPackage("package"),
        KeywordReturn("return"),
        KeywordPrivate("private"),
        KeywordPublic("public"),
        KeywordVoid("void"),
        KeywordInt("int"),
        KeywordBoolean("boolean"),
        KeywordFloat("float"),
        KeywordString("string"),
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
        Variable("[a-zA-Z][a-zA-Z0-9]*", true),
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
