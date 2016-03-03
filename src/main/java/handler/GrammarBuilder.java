package handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by josking on 3/3/16.
 */
public class GrammarBuilder {
    List<Map<Function<Object, Boolean>, Object[]>> execution = new ArrayList<>();

    public GrammarBuilder() {
        execution.add(new LinkedHashMap<>());
    }

    public GrammarBuilder isTrue(Function<Object, Boolean> callable, Object... parameters) {
        execution.get(execution.size() - 1).put(callable, parameters);
        return this;
    }

    public GrammarBuilder and(Function<Object, Boolean> callable, Object... parameters) {
        isTrue(callable, parameters);
        return this;
    }

    public GrammarBuilder or() {
        execution.add(new LinkedHashMap<>());
        return this;
    }

    public Supplier<Boolean> build() {
        return () -> execution.stream().filter(map -> {
                return map.entrySet().stream().filter(e -> {System.out.println(e.getKey().apply(e.getValue())); return true;}).findAny().isPresent();
                /*.filter(e -> {
                Boolean b = e.getKey().apply(e.getValue());
                return (b == null || !b);
            }).findFirst().isPresent();*/
        }).findAny().isPresent();
    }

    public GrammarBuilder notNull(Function<Object, Boolean> callable, Object... parameters) {
        execution.get(execution.size() - 1).put(callable, parameters);
        return this;
    }
}
