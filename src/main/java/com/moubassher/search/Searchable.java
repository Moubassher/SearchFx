package com.moubassher.search;

import java.util.Map;
import java.util.function.Function;

public interface Searchable<T> {

    Map<String, Function<T, String>> getSearchableFields();

    Map<String, Function<T, Boolean>> getBooleanFilters();
}
