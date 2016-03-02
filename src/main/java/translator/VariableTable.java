package translator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by josking on 3/2/16.
 */
public class VariableTable {
    private static VariableTable root = new VariableTable(null, "");

    private static VariableTable instance = root;
    private VariableTable parent;
    private String parentKey;
    private Map<String, Object> variables = new HashMap<>();

    VariableTable(VariableTable parent, String parentKey) {
        this.parent = parent;
        this.parentKey = parentKey;
    }

    public void addToScope(Var variable) {
        variables.put(variable.getName(), variable);
        VariableTable inst = instance;
        System.out.println("Adding " + variable.getName() + " to...");
        while (inst != null) {
            System.out.println("-> " + inst.parentKey);
            inst = inst.getParent();
        }
    }

    public static VariableTable addPackage(String pkg) {
        VariableTable add = getRoot();
        String[] pkgs = pkg.split("\\.");
        for (String p : pkgs) {
            add.variables.putIfAbsent(p, new VariableTable(add, p));
            add = (VariableTable) add.variables.get(p);
        }
        instance = add;
        return instance;
    }

    public VariableTable addClass(String cls) {
        variables.putIfAbsent(cls, new VariableTable(this, cls));
        instance = (VariableTable) variables.get(cls);
        return instance;
    }

    public void importPackage(String pkg) {
        String[] pkgs = pkg.split("\\.");
        Map<String, Object> findPackage = getRoot().variables;
        for (String p : pkgs) {
            if (findPackage.get(p) != null && findPackage.get(p).getClass().equals(VariableTable.class)) {
                findPackage = (Map<String, Object>) findPackage.get(p);
            } else {
                System.err.println("Package " + pkg + " not available for import (not found).");
                return;
            }
        }
        for (Object obj : findPackage.values()) {
            Var var = (Var) obj;
            if (var.getAccess().equals(Var.Access.Public)) instance.addToScope(var);
        }
        //instance.variables.putAll(findPackage);
    }

    private static VariableTable getRoot() {
        return root;
    }

    public static VariableTable getInstance() {
        return instance;
    }

    public Var get(String variable) {
        // Bottom Up with no path
        if (!variable.contains(".")) {
            VariableTable instance = this;
            while (instance != null) {
                Object var = instance.variables.get(variable);
                if (var != null && var.getClass().equals(Var.class)) return (Var) var;
                instance = instance.getParent();
            }
        }

        // Top Down w/Path
        VariableTable instance = getRoot();
        for (String part : variable.split("\\.")) {
            Object obj = instance.variables.get(part);
            if (obj != null) {
                if (obj.getClass().equals(Var.class)) {
                    return (Var) obj;
                } else {
                    instance = (VariableTable) obj;
                }
            }
        }
        return null;
    }

    public VariableTable getParent() {
        return parent;
    }
}
