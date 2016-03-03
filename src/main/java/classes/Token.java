package classes;

/**
 * Created by josking on 3/2/16.
 */
public class Token {
    public enum Type {
        WhiteSpace("\\s+", true),
        OpenBrace("{"),
        CloseBrace("}"),
        OpenParenthesis("("),
        CloseParenthesis(")"),
        OpenBracket("("),
        CloseBracket(")"),
        Dot("."),
        Comma(","),
        Colon(":"),
        Semicolon(";"),
        ConstFloat("\\d+\\.\\d+", true),
        ConstInteger("\\d+", true),
        ConstString("([\"']).*?\\1", true),
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
        KeywordStatic("static"),
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
        OperatorEquals("="),
        OperatorMultiply("*"),
        OperatorMinus("-"),
        OperatorPlus("+"),
        OperatorDivide("/"),
        OperatorModulo("%"),
        OperatorIncrement("++"),
        OperatorDecrement("--"),
        OperatorPower("**"),
        OperatorDivideBy("/="),
        OperatorMultiplyBy("*="),
        OperatorDecrementBy("-="),
        OperatorIncrementBy("+="),
        OperatorModuloBy("%="),
        BitwiseOr("|"),
        BitwiseAnd("&"),
        BitwiseNot("~"),
        BitwiseXor("^"),
        Name("[a-zA-Z][a-zA-Z0-9]*", true),
        LineComment("//.*", true),
        MultiLineComment("/[*]([\\d\\D]*)[*]/", true),
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
