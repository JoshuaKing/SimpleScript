package Syntax;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josh on 8/03/2016.
 */
public class SymbolTable {
    private static SymbolTable root = new SymbolTable(null, "RootScope");
    private static SymbolTable instance = root;

    private SymbolTable parentScope;
    private String name;
    private Map<String, SymbolTable> scopes = new HashMap<>();
    private Map<String, Symbol> symbols = new HashMap<>();

    private SymbolTable(SymbolTable parent, String name) {
        parentScope = parent;
        this.name = name;
    }

    public static SymbolTable getInstance() {
        return instance;
    }

    public static SymbolTable newScope(String scope) {
        String[] parts = scope.split("\\.");
        for (String part : parts) {
            instance.scopes.put(part, new SymbolTable(instance, part));
            instance = instance.scopes.get(part);
        }
        return instance;
    }

    public static boolean addToScope(Symbol child) {
        if (instance.symbols.containsKey(child.name)) return false;
        return updateSymbol(child);
    }

    public static boolean updateSymbol(Symbol child) {
        instance.symbols.put(child.name, child);
        return true;
    }

    public static SymbolTable exitScope() {
        return instance = instance.parentScope;
    }

    public static Symbol find(String name) {
        // Bottom Up with no path
        if (!name.contains(".")) {
            SymbolTable search = instance;
            while (search != null) {
                Symbol symbol = search.symbols.get(name);
                if (symbol != null) return symbol;
                search = search.parentScope;
            }
            return null;
        }

        // Top Down w/Path
        SymbolTable search = root;
        for (String part : name.split("\\.")) {
            Symbol symbol = search.symbols.get(part);
            if (symbol != null) return symbol;
            search = search.scopes.get(name);
            if (search != null) return null;
        }
        return null;
    }

    public String toString() {
        String dump = "{\n";
        dump += indent("Symbols: {");
        for (Symbol symbol : symbols.values()) {
            dump += indent(indent(symbol.symbol.name() + " " + symbol.name + " : " + symbol.result.name() + " [" + symbol.access.name() + "]"));
        }
        dump += indent("}");
        dump += indent("Scopes: [");
        for (String scope : scopes.keySet()) {
            dump += indent(indent(scope + " : " + scopes.get(scope).toString()));
        }
        dump += indent("]");
        dump += "}";
        return dump;
    }

    public static String dump() {
        return root.toString();
    }

    private static String indent(String text) {
        String[] lines = text.split("\\n");
        StringBuilder content = new StringBuilder();
        String tab = "   ";
        for (String line : lines) {
            content.append(tab).append(line).append('\n');
        }
        return content.toString();
    }
}
