package com.moubassher.search;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SearchBar<T extends Searchable<T>>  extends VBox {

    @FXML
    private TextField searchField;

    @FXML
    private TitledPane titledPane;

    @FXML
    private FlowPane filtersPane;

    private Set<String> activeFields =new HashSet<>();

    private T searchable;

    private List<T> items;

    private Map<String, Map<String, List<T>>> invertedIndex;

    public SearchBar() {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("search_bar.fxml"));

        fxmlLoader.setController(this);
        try {
            VBox root = fxmlLoader.load(); // root created by FXML
            this.getChildren().setAll(root.getChildren()); // copy children into 'this'

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setItems(List<T> items) {
        this.items = items;
        invertedIndex = Search.buildInvertedIndex(items);
    }
    public void activateSearch(Function<List<T>, Pane> resultFunc) {
        if (activeFields.isEmpty()) {
            activeFields = readActiveFilters();
        }

        searchField.textProperty().addListener((a, b, query) -> {
            if (query != null && !query.isBlank()) {

                List<Map.Entry<T, Double>> results = Search.fuzzySearch(query, items, invertedIndex, activeFields);

                // Map the entries to a list of T and pass them at once
                List<T> resultItems = results.stream()
                        .map(Map.Entry::getKey)
                        .toList();

                resultFunc.apply(FXCollections.observableList(resultItems));
            }
        });
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
        addFilters(); // now that we know what searchable type we have, build filters
    }



    public void addFilters() {

        Map<String, Function<T, String>> fields = searchable.getSearchableFields();


        fields.forEach((field, function) -> {
            CheckBox filterBox = new CheckBox(field);
            filterBox.setSelected(true); // enabled by default (optional)
            filtersPane.getChildren().add(filterBox);

            // Optional: keep track of selected filters if you maintain a Set<String> activeFields
            filterBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    activeFields.add(field);
                } else {
                    activeFields.remove(field);
                }
            });
        });
    }




    public void setExpanded(boolean value) {
        titledPane.setExpanded(value);
    }

    public boolean isExpanded() {
        return titledPane.isExpanded();
    }
}
