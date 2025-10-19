package com.context.search;

import com.context.search.testSearchables.TestProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.context.search.Search.buildInvertedIndex;
import static com.context.search.Search.fuzzySearch;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SearchTests {


    @Test
    @DisplayName("Search is IDF-enabled")
    void testIdfInSearch() {
        List<TestProduct> products = createTestProducts();

        Map<String, Map<String, List<TestProduct>>> invertedIndex = buildInvertedIndex(products);
        Set<String> activeFields = Set.of("name", "description", "category");

        List<Map.Entry<TestProduct, Double>> paracetamolResults =
                fuzzySearch("paracetamol", products, invertedIndex, activeFields);

        List<Map.Entry<TestProduct, Double>> zincResults =
                fuzzySearch("zinc", products, invertedIndex, activeFields);

        double avgParacetamolScore = paracetamolResults.stream()
                .mapToDouble(Map.Entry::getValue)
                .average().orElse(0);

        double avgZincScore = zincResults.stream()
                .mapToDouble(Map.Entry::getValue)
                .average().orElse(0);

        assertTrue(avgZincScore > avgParacetamolScore,
                "Expected higher average score for rarer term 'zinc'");

    }

    private List<TestProduct> createTestProducts() {
        return List.of(
                new TestProduct("Paracetamol 500mg", "Panadol", "Analgesic"),
                new TestProduct("Paracetamol Extra", "Tylenol", "Analgesic"),
                new TestProduct("Paracetamol Syrup", "Panadol", "Analgesic"),
                new TestProduct("Ibuprofen 200mg", "Advil", "Analgesic"),
                new TestProduct("Ibuprofen Forte", "Nurofen", "Analgesic"),
                new TestProduct("Cough Syrup", "Robitussin", "Cold"),
                new TestProduct("Vitamin C 1000mg", "Redoxon", "Supplement"),
                new TestProduct("Zinc Tablets", "Redoxon", "Supplement")
        );
    }

}
