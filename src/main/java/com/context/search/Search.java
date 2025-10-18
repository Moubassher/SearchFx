package com.context.search;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Search {

    /**
     * Performs a fuzzy full-text search over an inverted index using word-level similarity
     * and IDF (Inverse Document Frequency) weighting for relevance ranking.
     * <p>
     * This method is generic and can search any type of object {@code <T>} as long as an
     * inverted index and a word-extraction function are provided.
     * </p>
     *
     * <h3>How it works</h3>
     * <ul>
     *   <li>Splits the query into individual terms.</li>
     *   <li>Computes fuzzy similarity between each query term and every indexed word.</li>
     *   <li>Applies a per-term weight so that earlier query words carry slightly higher importance.</li>
     *   <li>Multiplies each similarity score by an IDF factor, so rarer words contribute more to ranking.</li>
     *   <li>Aggregates scores across all matching words for each item.</li>
     *   <li>Normalizes the total score by the number of words in the item to balance short vs. long entries.</li>
     *   <li>Returns the results sorted by descending relevance score.</li>
     * </ul>
     *
     * <h3>Scoring Formula</h3>
     * For each fuzzy-matched word:
     * <pre>
     * weightedScore = similarity * queryWordWeight * idf
     * </pre>
     * where:
     * <ul>
     *   <li>{@code similarity} is the fuzzy ratio between the query term and the indexed word (0–100)</li>
     *   <li>{@code queryWordWeight} = 1.0 - (position * 0.1), giving more weight to earlier query terms</li>
     *   <li>{@code idf} = log(totalItems / (1 + documentFrequency)), boosting rare words</li>
     * </ul>
     *
     * @param query              the user’s search input (e.g. "instan nodles")
     * @param invertedIndex      a map of indexed words to a list of items that contain them
     * @param allWordsExtractor  a function that extracts all searchable words from an item {@code T}
     * @param <T>                the type of the indexed items (e.g. Product, Book, etc.)
     * @return a list of map entries {@code (item → score)}, sorted in descending order of relevance
     *
     * @implNote
     * - The fuzzy similarity uses {@link me.xdrop.fuzzywuzzy.FuzzySearch#ratio(String, String)}.
     * - A similarity threshold of 70 is applied to reduce noise.
     * - This method is fully in-memory and does not persist results.
     *
     ** <h3>Example</h3>
     *  * <pre>{@code
     *  * Map<String, List<Product>> index = buildInvertedIndex(products);
     *  * List<Map.Entry<Product, Double>> results =
     *  *     fuzzySearch("instan nodles", index, Product::getAllWords);
     *  * }</pre>
     *
     *  */

    public static <T> List<Map.Entry<T, Double>> fuzzySearch(
            String query,
            Map<String, List<T>> invertedIndex,
            Function<T, List<String>> allWordsExtractor) {

        String[] queryTerms = query.toLowerCase().split("\\s+");
        Map<T, Double> scoredResults = new HashMap<>();

        // Total unique items
        int totalItems = invertedIndex.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet())
                .size();

        // Compute IDF for each word
        Map<String, Double> idfMap = new HashMap<>();
        for (String word : invertedIndex.keySet()) {
            int df = invertedIndex.get(word).size();
            double idf = Math.log((double) totalItems / (1 + df));
            idfMap.put(word, idf);
        }

        // Fuzzy search and scoring
        for (int i = 0; i < queryTerms.length; i++) {
            String term = queryTerms[i];
            double queryWeight = 1.0 - (i * 0.1); // earlier words matter more

            for (String word : invertedIndex.keySet()) {
                int similarity = FuzzySearch.ratio(term, word);
                if (similarity >= 70) {
                    double idf = idfMap.getOrDefault(word, 1.0);

                    for (T item : invertedIndex.get(word)) {
                        double weightedScore = similarity * queryWeight * idf;
                        scoredResults.merge(item, weightedScore, Double::sum);
                    }
                }
            }
        }

        // Normalize by number of words in the item
        List<Map.Entry<T, Double>> ranked = scoredResults.entrySet().stream()
                .map(e -> Map.entry(e.getKey(),
                        e.getValue() / Math.max(1, allWordsExtractor.apply(e.getKey()).size())))
                .sorted(Map.Entry.<T, Double>comparingByValue().reversed())
                .collect(Collectors.toList());

        return ranked;
    }


    public static <T> Map<String, List<T>> buildInvertedIndex(
            Collection<T> items,
            Function<T, List<String>> textExtractors) {

        return items.stream()
                .flatMap(item ->
                        textExtractors.apply(item).stream() // get all text fields (brand, generic, etc.)
                                .filter(Objects::nonNull)
                                .flatMap(s -> Arrays.stream(s.toLowerCase().split("\\s+")))
                                .map(word -> Map.entry(word, item))
                )
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }

    public static <T> List<String> getSearchParameters(T item, Function<T, List<String>> textExtractor) {
        return textExtractor.apply(item).stream()
                .filter(Objects::nonNull)
                .flatMap(s -> Arrays.stream(s.toLowerCase().split("\\s+")))
                .collect(Collectors.toList());
    }
}
