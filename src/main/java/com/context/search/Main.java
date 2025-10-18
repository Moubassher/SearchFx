package com.context.search;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        SearchBar searchBar = new SearchBar();
        searchBar.setExpanded(true);
        Scene scene = new Scene(searchBar, 500, 500);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String [] args) {
        launch(args);
    }
}
