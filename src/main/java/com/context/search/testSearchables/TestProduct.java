package com.context.search.testSearchables;

import com.context.search.Searchable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TestProduct implements Searchable<TestProduct> {
    private final String name;
    private final String category;
    private final String description;

    public TestProduct(String name, String category, String description) {
        this.name = name;
        this.category = category;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public Map<String, Function<TestProduct, String>> getSearchableFields() {
        Map<String, Function<TestProduct, String>> fields = new HashMap<>();
        fields.put("name", p -> p.getName());
        fields.put("category", p -> p.getCategory());
        fields.put("description", p -> p.getDescription());
        return fields;
    }

    @Override
    public String toString() {
        return name;
    }
}
