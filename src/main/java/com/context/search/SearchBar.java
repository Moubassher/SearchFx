package com.context.search;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchBar  extends VBox {

    @FXML
    private TitledPane titledPane;

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

    public void setExpanded(boolean value) {
        titledPane.setExpanded(value);
    }

    public boolean isExpanded() {
        return titledPane.isExpanded();
    }
}
