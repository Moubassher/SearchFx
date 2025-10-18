package com.context.search;

import java.util.Map;
import java.util.function.Function;

public interface Searchable<T> {

    Map<String, Function<T, String>> getSearchableFields();
}
