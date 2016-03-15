package Syntax;

/**
 * Created by Josh on 8/03/2016.
 */
public class Symbol {
    public enum AccessType {
        Public("public"),
        Protected(null),
        Private("private"),
        Unaddressable(null),
        None(null);

        private String value;

        AccessType(String value) {
            this.value = value;
        }

        public static AccessType fromDeclaredType(String value) {
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
        String("string", "String"),
        Integer("int", "Integer"),
        Float("float", "Float"),
        Boolean("boolean", "Boolean"),
        Map("map", "Map"),
        List("list", "List"),
        Void("void", null),
        None(null, null),
        UserClass(null, null);

        private String declaredName;
        private String grammarName;

        ResultType(String declaredName, String grammarName) {
            this.declaredName = declaredName;
            this.grammarName = grammarName;
        }

        public static ResultType fromDeclaredType(String value) {
            if (value == null) return null;
            for (ResultType resultType : ResultType.values()) {
                if (value.equals(resultType.declaredName)) return resultType;
            }
            return null;
        }

        public static ResultType fromGrammarName(String value) {
            if (value == null) return null;
            for (ResultType resultType : ResultType.values()) {
                if (value.equals(resultType.grammarName)) return resultType;
            }
            return null;
        }
    }

    private static int count = 0;

    private final String id = "SYMBOL-" + count++;
    public String name;
    public SymbolType symbol = SymbolType.Variable;
    public AccessType access = AccessType.None;
    public ResultType result = ResultType.None;

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

    public static Symbol newScopeSymbol(String prefix) {
        Symbol symbol = new Symbol();
        symbol.name = prefix + "-SCOPE-" + symbol.id;
        return symbol;
    }
}
