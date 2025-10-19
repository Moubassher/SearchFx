package com.moubassher.search;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Search {


    /**
     * Performs a weighted full-text search with basic IDF (Inverse Document Frequency) scoring
     * over a pre-built inverted index. The search is generic and works for any class implementing
     * the {@code Searchable<T>} interface.
     * <p>
     * This method assumes that the inverted index maps each token (word) to a map of field names
     * and the items containing that word in each field:
     * <pre>{@code
     * Map<String, Map<String, Set<T>>> invertedIndex
     * }</pre>
     * Example structure:
     * <pre>{@code
     * {
     *   "instant": { "brandName": [product1, product2], "genericName": [product3] },
     *   "noodles": { "brandName": [product1] }
     * }
     * }</pre>
     * <p>
     * The method filters by a given set of active field names, computes an IDF score per matching term,
     * and accumulates scores for each matching item. The final result is sorted by descending total score.
     * <p>
     * <b>Notes:</b>
     * <ul>
     *   <li>This version matches terms exactly (no fuzzy matching yet).</li>
     *   <li>IDF favors rare words — items matching less frequent words score higher.</li>
     *   <li>Normalization by field count or term count is optional and not applied here.</li>
     * </ul>
     *
     * @param <T>            the type of searchable items
     * @param query          the search query (split into lowercase words)
     * @param invertedIndex  the pre-built inverted index of all searchable fields
     * @param allItems       the full collection of items (used for IDF computation)
     * @param activeFields   the set of field names to include in this search (e.g. "brandName")
     * @return a list of entries (item → score), sorted by descending score
     *
     * @throws NullPointerException if {@code invertedIndex} or {@code allItems} is null
     *
     * @see Searchable
     */

    public static <T extends Searchable<T>> List<Map.Entry<T, Double>> fuzzySearch(
            String query,
            List<T> allItems,
            Map<String, Map<String, List<T>>> invertedIndex,
            Set<String> activeFields)
    {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String[] words = query.toLowerCase().split("\\s+");
        Map<T, Double> scores = new HashMap<>();
        int totalDocs = allItems.size();

        for (String word : words) {
            Map<String, List<T>> fieldMap = invertedIndex.get(word);
            if (fieldMap == null) continue;

            // include only active fields
            Set<T> matchingItems = fieldMap.entrySet().stream()
                    .filter(e -> activeFields.contains(e.getKey()))
                    .flatMap(e -> e.getValue().stream())
                    .collect(Collectors.toSet());

            // compute IDF
            double idf = Math.log((double) totalDocs / (1 + matchingItems.size()));

            // accumulate weighted scores
            for (T item : matchingItems) {
                scores.merge(item, idf, Double::sum);
            }
        }

        List<Map.Entry<T, Double>> ranked= scores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        return ranked;
    }


    public static <T extends Searchable<T>> Map<String, Map<String, List<T>>> buildInvertedIndex(Collection<T> items) {
        Map<String, Map<String, List<T>>> index = new HashMap<>();

        for (T item : items) {
            // Go through each searchable field
            for (Map.Entry<String, Function<T, String>> entry : item.getSearchableFields().entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue().apply(item);

                if (fieldValue == null || fieldValue.isBlank()) continue;

                // Split into individual words
                String[] words = fieldValue.toLowerCase().split("\\s+");

                for (String word : words) {
                    index.computeIfAbsent(word, k -> new HashMap<>()) // get or create word map
                            .computeIfAbsent(fieldName, k -> new ArrayList<>()) // get or create field list
                            .add(item); // add this item
                }
            }
        }

        return index;
    }




    public static <T extends Searchable> List<String> getSearchParameters(T item, Function<T, List<String>> textExtractor) {

        return textExtractor.apply(item).stream()
                .filter(Objects::nonNull)
                .flatMap(s -> Arrays.stream(s.toLowerCase().split("\\s+")))
                .collect(Collectors.toList());
    }
}
