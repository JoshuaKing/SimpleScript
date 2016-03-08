package Syntax;

/**
 * Created by Josh on 8/03/2016.
 */
public class Symbol {
    public enum AccessType {
        Public("public"),
        Protected(""),
        Private("private");

        private String value;

        AccessType(String value) {
            this.value = value;
        }

        public static AccessType of(String value) {
            if (value == null) return null;
            for (AccessType accessType : AccessType.values()) {
                if (value.equals(accessType.value)) return accessType;
            }
            return null;
        }
    }

    public enum SymbolType {
        Variable,
        Method,
        Class
    }

    public enum ResultType {
        String("string"),
        Integer("int"),
        Float("float"),
        Boolean("boolean"),
        Map("map"),
        List("list"),
        Void("void"),
        None(null),
        Unknown(null);

        private String value;

        ResultType(String value) {
            this.value = value;
        }

        public static ResultType of(String value) {
            if (value == null) return null;
            for (ResultType resultType : ResultType.values()) {
                if (value.equals(resultType.value)) return resultType;
            }
            return null;
        }
    }

    private static int count = 0;

    public final String id = "SYMBOL-" + count++;
    public String name = "Scope";
    public SymbolType symbol = SymbolType.Variable;
    public AccessType access = AccessType.Protected;
    public ResultType result = ResultType.Unknown;

    public Symbol() { }

    public Symbol(String name) {
        this(name, SymbolType.Variable, ResultType.Void);
    }

    public Symbol(String name, SymbolType symbol, ResultType result) {
        this(name, symbol, result, AccessType.Protected);
    }

    public Symbol(String name, SymbolType symbol, ResultType result, AccessType access) {
        this.name = name;
        this.symbol = symbol;
        this.result = result;
        this.access = access;
    }
}
