package ar.edu.itba.paw.persistence.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum StringSearchType {
    EQUALS(s -> s, String::equals),
    PREFIX(s -> '%' + s, String::startsWith),
    SUFFIX(s -> s + '%', String::endsWith),
    CONTAINS(s -> '%' + s + '%', String::contains),
    CONTAINS_NO_ACC(s -> '%' + s + "%", (s, s2) -> StringUtils.stripAccents(s).contains(StringUtils.stripAccents(s2)));

    private final Function<String, String> transformer;
    private final BiFunction<String, String, Boolean> operator;

    StringSearchType(Function<String, String> transformer, BiFunction<String, String, Boolean> operator) {
        this.transformer = transformer;
        this.operator = operator;
    }

    public String transform(String s) {
        return this.transformer.apply(s);
    }

    public boolean operate(String s1, String s2) {
        return this.operator.apply(s1, s2);
    }
}
