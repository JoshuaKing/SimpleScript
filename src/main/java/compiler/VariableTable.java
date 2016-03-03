package compiler;

import classes.Method;
import classes.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by josking on 3/2/16.
 */
public class VariableTable {
    public static final String METHOD_KEY = "_Method";
    private static VariableTable root = new VariableTable(null, "");

    private static VariableTable instance = root;
    private VariableTable parent;
    private String parentKey;
    private Map<String, Object> variables = new HashMap<>();

    VariableTable(VariableTable parent, String parentKey) {
        this.parent = parent;
        this.parentKey = parentKey;
    }

    public void addToScope(Variable variable) {
        variables.put(variable.getName(), variable);
        VariableTable inst = instance;
        System.out.println("Adding " + variable.getName() + " = " + variable.getValue() + " to...");
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

    public VariableTable addScope(String scope) {
        variables.putIfAbsent(scope, new VariableTable(this, scope));
        instance = (VariableTable) variables.get(scope);
        return instance;
    }

    public static boolean importPackage(String pkg) {
        String[] pkgs = pkg.split("\\.");
        Map<String, Object> findPackage = getRoot().variables;
        for (String p : pkgs) {
            if (findPackage.get(p) != null && findPackage.get(p).getClass().equals(VariableTable.class)) {
                findPackage = (Map<String, Object>) findPackage.get(p);
            } else {
                System.err.println("Package " + pkg + " not available for import (not found).");
                return false;
            }
        }
        for (Object obj : findPackage.values()) {
            Variable variable = (Variable) obj;
            if (variable.getAccess().equals(Variable.Access.Public)) instance.addToScope(variable);
        }
        return true;
    }

    private static VariableTable getRoot() {
        return root;
    }

    public static VariableTable getInstance() {
        return instance;
    }

    public Variable get(String variable) {
        // Bottom Up with no path
        if (!variable.contains(".")) {
            VariableTable instance = this;
            while (instance != null) {
                Object var = instance.variables.get(variable);
                if (var != null && var.getClass().equals(Variable.class)) return (Variable) var;
                instance = instance.getParent();
            }
        }

        // Top Down w/Path
        VariableTable instance = getRoot();
        for (String part : variable.split("\\.")) {
            Object obj = instance.variables.get(part);
            if (obj != null) {
                if (obj.getClass().equals(Variable.class)) {
                    return (Variable) obj;
                } else {
                    instance = (VariableTable) obj;
                }
            }
        }
        return null;
    }

    public Method getMethod(String method) {
        // Bottom Up w/Path
        VariableTable instance = this.getParent();
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
                instance = (VariableTable) obj;
            }
        }
        return null;
    }

    public VariableTable getParent() {
        return parent;
    }

    public void addToScope(List<Variable> vars) {
        for (Variable v : vars) {
            addToScope(v);
        }
    }

    public void addMethod(Method method) {
        variables.put(METHOD_KEY, method);
        VariableTable inst = instance;
        System.out.println("Adding " + METHOD_KEY + " to...");
        while (inst != null) {
            System.out.println("-> " + inst.parentKey);
            inst = inst.getParent();
        }
    }
}
