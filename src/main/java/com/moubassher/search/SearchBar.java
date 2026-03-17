package com.moubassher.search;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class SearchBar<T extends Searchable<T>>  extends VBox {

    @FXML
    private TextField searchField;

    @FXML
    private Label resultSizeDisplay;

    @FXML
    private TitledPane titledPane;

    @FXML
    private FlowPane filtersPane, criteriaPane;

    @FXML
    private Pane rangePane;

    private Set<String> activeFields =new HashSet<>();

    private T searchable;

    private List<T> items;

    private Map<String, Map<String, List<T>>> invertedIndex;

    private List<RangeFilter<T>> rangeFilters = new ArrayList<>();

    private Map<String, Function<T, Boolean>> booleanFilters = new HashMap<>();
    private Set<String> activeBooleanFilters = new HashSet<>();

    private ResultHandler<T> resultHandler;

    public SearchBar(List<T> items, T prototype, ResultHandler<T> resultHandler) {
        loadFxml();
        this.resultHandler = resultHandler;
        setSearchable(prototype);
        setItems(items);
        activateSearch();
    }

    public void initialize(List<T> items, T prototype, ResultHandler<T> resultHandler) {
        this.resultHandler = resultHandler;
        setSearchable(prototype);
        setItems(items);
        activateSearch();
    }



    public SearchBar() {
        loadFxml();
    }

    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("search_bar.fxml"));

        fxmlLoader.setController(this);
        try {
            VBox root = fxmlLoader.load(); // root created by FXML
            this.getChildren().setAll(root.getChildren()); // copy children into 'this'

        } catch (IOException e) {
            e.printStackTrace();
        }

        titledPane.expandedProperty().addListener((a, b, expanded) ->{
            if(expanded) {
                this.toFront();
            } else {
                this.toBack();
            }
        });
    }


    private void activateSearch() {

        if (activeFields.isEmpty()) {
            activeFields = readActiveFilters();
        }

        // run search when typing
        searchField.textProperty().addListener(
                (obs, oldText, query) -> runSearch());
    }


    private void runSearch() {
        String query = searchField.getText();

        // If query empty → show all items
        if (query == null || query.isBlank()) {
            List<T> filtered = items.stream()
                    .filter(this::passesRangeFilters)
                    .toList();
            resultHandler.show(filtered);
            return;
        }

        List<Map.Entry<T, Double>> ranked =
                Search.fuzzySearch(query, items, invertedIndex, activeFields);

        List<T> resultItems =
                ranked.stream()
                        .map(Map.Entry::getKey)
                        .filter(this::passesRangeFilters)
                        .filter(this::passesBooleanFilters)
                        .toList();

        int resultSize = resultItems.size();
        resultSizeDisplay.setText(resultSize+"");

        resultHandler.show(resultItems);
    }

    public void setItems(List<T> items) {
        this.items = items;
        invertedIndex = Search.buildInvertedIndex(items);
    }


    private Set<String> readActiveFilters() {
        return filtersPane.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText) // assuming the checkbox text is the field name
                .collect(Collectors.toSet());
    }

    public void setSearchable(T searchable) {
        this.searchable = searchable;
        addFilters();
        addBooleanFilters();
        addRangeFilters();
    }


    public void addFilters() {
        Map<String, Function<T, String>> fields = searchable.getSearchableFields();

        fields.forEach((field, function) -> {
            CheckBox filterBox = new CheckBox(field);
            filterBox.setSelected(true); // enabled by default
            filtersPane.getChildren().add(filterBox);

            filterBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    activeFields.add(field);
                } else {
                    activeFields.remove(field);
                }
                runSearch();
            });
        });
    }

    private void addRangeFilters() {

        Map<String, ToIntFunction<T>> map = searchable.getRangeFilters();

        map.forEach((name, func) -> {

            Label label = new Label(name);

            TextField min = new TextField();
            min.setPromptText("من");

            TextField max = new TextField();
            max.setPromptText("إلى");

            HBox row = new HBox(8);
            row.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

            row.getChildren().addAll(label, min, max);

            rangePane.getChildren().add(row);

            rangeFilters.add(new RangeFilter<>(name, func, min, max));
        });
    }

    private void buildBooleanCriteria() {

        Map<String, Function<T, Boolean>> map = searchable.getBooleanFilters();

        map.forEach((name, func) -> {

            CheckBox checkBox = new CheckBox(name);
            checkBox.setSelected(true);

            // store function inside checkbox (BEST design)
            checkBox.setUserData(func);

            criteriaPane.getChildren().add(checkBox);

            checkBox.selectedProperty().addListener((obs, was, isNow) -> runSearch());
        });
    }

    private void addBooleanFilters() {
        booleanFilters = searchable.getBooleanFilters();

        booleanFilters.forEach((label, func) -> {
            CheckBox cb = new CheckBox(label);
            cb.setSelected(true); // default enabled
            criteriaPane.getChildren().add(cb);

            cb.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    activeBooleanFilters.add(label);
                } else {
                    activeBooleanFilters.remove(label);
                }
                runSearch();
            });
        });
    }


    public void setExpanded(boolean value) {
        titledPane.setExpanded(value);
    }

    public boolean isExpanded() {
        return titledPane.isExpanded();
    }

    private boolean passesRangeFilters(T item) {

        for (RangeFilter<T> rf : rangeFilters) {

            int value = rf.extractor.applyAsInt(item);

            Integer min = tryParseInt(rf.minField.getText());
            Integer max = tryParseInt(rf.maxField.getText());

            if (min != null && value < min) return false;
            if (max != null && value > max) return false;
        }

        return true;
    }

    private boolean passesBooleanFilters(T item) {
        for (String activeLabel : activeBooleanFilters) {
            Function<T, Boolean> func = booleanFilters.get(activeLabel);
            if (func != null && !func.apply(item)) return false;
        }
        return true;
    }


    private Integer tryParseInt(String text) {

        if (text == null || text.isBlank()) return null;

        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;   // ignore invalid user typing
        }
    }

    private static class RangeFilter<T> {
        String name;
        ToIntFunction<T> extractor;
        TextField minField;
        TextField maxField;

        RangeFilter(String name, ToIntFunction<T> extractor,
                    TextField minField, TextField maxField) {
            this.name = name;
            this.extractor = extractor;
            this.minField = minField;
            this.maxField = maxField;
        }
    }

    @FunctionalInterface
    public interface ResultHandler<T> {
        void show(List<T> results);
    }
}
