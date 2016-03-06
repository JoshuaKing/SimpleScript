package compiler;

import classes.Method;
import classes.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by josking on 3/2/16.
 */
public class SymbolTable {
    public static final String METHOD_KEY = "_Method";
    private static SymbolTable root = new SymbolTable(null, "");

    private static SymbolTable instance = root;
    private SymbolTable parent;
    private String parentKey;
    private Map<String, Object> variables = new HashMap<>();

    SymbolTable(SymbolTable parent, String parentKey) {
        this.parent = parent;
        this.parentKey = parentKey;
    }

    public void addToScope(Variable variable) {
        variables.put(variable.getName(), variable);
        SymbolTable inst = instance;
        System.out.println("Adding " + variable.getName() + " = " + variable.getValue() + " to...");
        while (inst != null) {
            System.out.println("-> " + inst.parentKey);
            inst = inst.getParent();
        }
    }

    public static SymbolTable addPackage(String pkg) {
        SymbolTable add = getRoot();
        String[] pkgs = pkg.split("\\.");
        for (String p : pkgs) {
            add.variables.putIfAbsent(p, new SymbolTable(add, p));
            add = (SymbolTable) add.variables.get(p);
        }
        instance = add;
        return instance;
    }

    public SymbolTable addNewScope(String scope) {
        variables.putIfAbsent(scope, new SymbolTable(this, scope));
        instance = (SymbolTable) variables.get(scope);
        return instance;
    }

    public static boolean importPackage(String pkg) {
        String[] pkgs = pkg.split("\\.");
        Map<String, Object> findPackage = getRoot().variables;
        for (String p : pkgs) {
            if (findPackage.get(p) != null && findPackage.get(p).getClass().equals(SymbolTable.class)) {
                findPackage = (Map<String, Object>) findPackage.get(p);
            } else {
                System.err.println("Package " + pkg + " not available for import (not found).");
                return false;
            }
        }
        for (Object obj : findPackage.values()) {
            Variable variable = (Variable) obj;
            if (variable.getModifier().equals(Variable.Modifier.Public)) instance.addToScope(variable);
        }
        return true;
    }

    private static SymbolTable getRoot() {
        return root;
    }

    public static SymbolTable getInstance() {
        return instance;
    }

    public Variable get(String variable) {
        // Bottom Up with no path
        if (!variable.contains(".")) {
            SymbolTable instance = this;
            while (instance != null) {
                Object var = instance.variables.get(variable);
                if (var != null && var.getClass().equals(Variable.class)) return (Variable) var;
                instance = instance.getParent();
            }
        }

        // Top Down w/Path
        SymbolTable instance = getRoot();
        for (String part : variable.split("\\.")) {
            Object obj = instance.variables.get(part);
            if (obj != null) {
                if (obj.getClass().equals(Variable.class)) {
                    return (Variable) obj;
                } else {
                    instance = (SymbolTable) obj;
                }
            }
        }
        return null;
    }

    public Method getMethod(String method) {
        // Bottom Up w/Path
        SymbolTable instance = this.getParent();
        Object obj = null;
        String methodId = method + '.' + METHOD_KEY;
        for (String part : methodId.split("\\.")) {
            while (obj == null) {
                obj = instance.variables.get(part);
                if (obj != null) break;
                instance = instance.getParent();
                if (instance == null) {
                    System.err.println("No such method " + method);
                    return null;
                }
            }
            obj = instance.variables.get(part);
            if (obj.getClass().equals(Method.class)) {
                return (Method) obj;
            } else {
                instance = (SymbolTable) obj;
            }
        }
        return null;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public void addToScope(List<Variable> vars) {
        for (Variable v : vars) {
            addToScope(v);
        }
    }

    public void addMethod(Method method) {
        variables.put(METHOD_KEY, method);
        SymbolTable inst = instance;
        System.out.println("Adding " + METHOD_KEY + " to...");
        while (inst != null) {
            System.out.println("-> " + inst.parentKey);
            inst = inst.getParent();
        }
    }
}
