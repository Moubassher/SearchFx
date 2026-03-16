package com.moubassher.search.testSearchables;

import com.moubassher.search.Searchable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TestProduct implements Searchable<TestProduct> {
    private  String name;
    private  String category;
    private  String description;
    private int price;
    private boolean hasDiscount;
    public TestProduct() {
    }

    public TestProduct(String name, String category, String description, int price, boolean hasDiscount) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.hasDiscount = hasDiscount;
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

    public int getPrice() {
        return price;
    }

    public void setHasDiscount(boolean hasDiscount) {
        this.hasDiscount = hasDiscount;
    }

    public boolean hasDiscount() {
        return hasDiscount;
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
    public Map<String, Function<TestProduct, Boolean>> getBooleanFilters() {
        Map<String, Function<TestProduct, Boolean>> booleanFilters = new HashMap<>();
        booleanFilters.put("تخفيض", p -> p.hasDiscount);
        return booleanFilters;
    }

    @Override
    public String toString() {
        return name + " ; " + description + " ; " + category;
    }
}
