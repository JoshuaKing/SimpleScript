package classes;

import com.google.common.collect.ImmutableList;

import java.util.function.Function;

import static classes.Token.Type.*;

/**
 * Created by josking on 3/2/16.
 */
public class Variable {
    public enum VarType {
        Integer(ImmutableList.of(KeywordInt, ConstInteger), 0, java.lang.Integer::new),
        String(ImmutableList.of(KeywordString, ConstString), "", java.lang.String::new),
        Float(ImmutableList.of(KeywordFloat, ConstFloat), 0.0, java.lang.Float::new),
        Boolean(ImmutableList.of(KeywordBoolean, KeywordTrue, KeywordFalse), false, java.lang.Boolean::new);


        private final ImmutableList<Token.Type> mapping;
        private final Object defaultValue;
        private final Function<String, ?> mapper;

        public Function<java.lang.String, ?> getMapper() {
            return mapper;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public static VarType fromTokenType(Token.Type type) {
            for (VarType vt : VarType.values()) {
                if (vt.mapping.contains(type)) {
                    return vt;
                }
            }
            return null;
        }

        <T> VarType(ImmutableList<Token.Type> mapping, Object defaultValue, Function<String, T> mapper) {
            this.mapper = mapper;
            this.mapping = mapping;
            this.defaultValue = defaultValue;
        }


    }
    public enum Modifiers {
        Public,
        Private,
        Argument
    }

    Modifiers modifiers;
    boolean isStatic = false;
    String name;
    VarType varType;
    Object value;

    public Variable(Modifiers modifiers, boolean isStatic, String name, Object value, VarType varType) {
        this.name = name;
        this.varType = varType;
        this.value = value;
        this.modifiers = modifiers;
        this.isStatic = isStatic;
    }

    public Variable(Modifiers modifiers, boolean isStatic, String name, Token.Type tokenType) {
        this.name = name;
        this.varType = VarType.fromTokenType(tokenType);
        this.value = varType.getDefaultValue();
        this.modifiers = modifiers;
        this.isStatic = isStatic;
    }

    public void setValue(String value) {
        this.value = varType.getMapper().apply(value);
    }

    public VarType getType() {
        return varType;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public VarType getVarType() {
        return varType;
    }
}
