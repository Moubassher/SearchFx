package com.moubassher.search;

import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public interface Searchable<T> {

    Map<String, Function<T, String>> getSearchableFields();

    Map<String, Function<T, Boolean>> getBooleanFilters();

    Map<String, ToIntFunction<T>> getRangeFilters();
}
