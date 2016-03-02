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

    public static void deeper() {
        scope++;
    }

    public static void shallower() {
        scope--;
    }

    public static Var get(String variable) {
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
