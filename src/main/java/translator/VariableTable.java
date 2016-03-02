package translator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by josking on 3/2/16.
 */
public class VariableTable {
    private static Map<Integer, Map<String, Object>> variables = new HashMap<>();
    private static int scope = 0;

    public static void addToScope(Var variable) {
        Map<String, Object> table = variables.getOrDefault(scope, new HashMap<>());
        table.put(variable.getName(), variable);
        variables.put(scope, table);
    }

    public static void addPackage(String pkg) {
        root();
        String[] pkgs = pkg.split("\\.");
        for (String p : pkgs) {
            variables.putIfAbsent(scope, new HashMap<>());
            deeper();
        }
    }

    public static void importPackage(String pkg) {
        String[] pkgs = pkg.split("\\.");
        Map<String, Object> table = variables.get(0);
        for (String p : pkgs) {
            if (table.get(p) != null && table.get(p).getClass().equals(Map.class)) {
                table = (Map<String, Object>) table.get(p);
            } else {
                System.err.println("Package " + pkg + " not available for import (not found).");
                return;
            }
        }
        variables.get(scope).putAll(table);
    }

    public static void root() {
        scope = 0;
    }

    public static void deeper() {
        scope++;
    }

    public static void shallower() {
        scope--;
    }

    public static Var get(String variable) {
        // When no path is given
        if (!variable.contains(".")) {
            for (int i = scope; i >= 0; i--) {
                if (variables.get(i).get(variable) == null) continue;
                if (variables.get(i).get(variable).getClass().equals(Var.class)) {
                    return (Var) variables.get(i).get(variable);
                }
            }
        }


        String[] parts = variable.split("\\.");
        int s = scope;
        Var var = null;
        // Bottom Up
        for (String part : parts) {
            if (variables.get(s) != null) {
                if (variables.get(s).get(part) == null) break;
                if (variables.get(s).get(part).getClass().equals(Var.class)) {
                    var = (Var) variables.get(s).get(part);
                }
            }
            s--;
            if (s < 0 && parts.length > 1) var = null;
            if (s <= 0) {
                break;
            }
        }

        if (var != null) return var;

        // Top Down
        s = 0;
        var = null;
        // Bottom Up
        for (String part : parts) {
            if (variables.get(s) != null) {
                if (variables.get(s).get(part) == null) break;
                if (variables.get(s).get(part).getClass().equals(Var.class)) {
                    var = (Var) variables.get(s).get(part);
                }
            }
            s++;
            if (variables.get(s) == null) {
                break;
            }
        }

        return var;
    }
}
