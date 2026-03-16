package com.moubassher.search;

import com.moubassher.search.testSearchables.TestProduct;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        List<TestProduct> products = createTestProducts();
        ListView<TestProduct> results = new ListView<>();

        SearchBar<TestProduct> searchBar = new SearchBar<>(products, new TestProduct(),
                queryResults -> {
                results.setItems(FXCollections.observableArrayList(queryResults));
                });

        searchBar.addRangeFunction("السعر", TestProduct::getPrice);

        VBox root = new VBox(10, searchBar, results);
        RangePicker rangePicker = new RangePicker();

        Scene scene = new Scene(root, 500, 500);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private List<TestProduct> createTestProducts() {
        return List.of(
                new TestProduct("Paracetamol 500mg", "Panadol", "Analgesic", 500),
                new TestProduct("Paracetamol Extra", "Tylenol", "Analgesic", 700),
                new TestProduct("Paracetamol Syrup", "Panadol", "Analgesic", 900),
                new TestProduct("Ibuprofen 200mg", "Advil", "Analgesic", 300),
                new TestProduct("Ibuprofen Forte", "Nurofen", "Analgesic", 100),
                new TestProduct("Cough Syrup", "Robitussin", "Cold", 1000),
                new TestProduct("Vitamin C 1000mg", "Redoxon", "Supplement", 1100),
                new TestProduct("Zinc Tablets", "Redoxon", "Supplement", 950),
                new TestProduct("Amoxicillin 500mg", "Amoxil", "Antibiotic", 750),
                new TestProduct("Amoxicillin Syrup", "Moxatag", "Antibiotic", 550),
                new TestProduct("Azithromycin 250mg", "Zithromax", "Antibiotic", 450),
                new TestProduct("Dextromethorphan Syrup", "Vicks", "Cold", 350),
                new TestProduct("Loratadine 10mg", "Claritin", "Allergy", 250),
                new TestProduct("Cetirizine 10mg", "Zyrtec", "Allergy", 770),
                new TestProduct("Levocetirizine 5mg", "Xyzal", "Allergy", 670),
                new TestProduct("Omeprazole 20mg", "Losec", "Gastro", 400),
                new TestProduct("Esomeprazole 40mg", "Nexium", "Gastro", 450),
                new TestProduct("Pantoprazole 40mg", "Pantoloc", "Gastro", 870),
                new TestProduct("Metformin 500mg", "Glucophage", "Diabetes", 100),
                new TestProduct("Glimepiride 2mg", "Amaryl", "Diabetes", 350),
                new TestProduct("Insulin Glargine 100IU", "Lantus", "Diabetes", 750),
                new TestProduct("Salbutamol Inhaler", "Ventolin", "Respiratory", 760),
                new TestProduct("Fluticasone Inhaler", "Flixotide", "Respiratory", 770),
                new TestProduct("Montelukast 10mg", "Singulair", "Respiratory", 350),
                new TestProduct("Prednisone 20mg", "Deltasone", "Steroid", 670),
                new TestProduct("Hydrocortisone Cream", "Cortizone", "Steroid", 18),
                new TestProduct("Betamethasone Ointment", "Diprolene", "Steroid", 32),
                new TestProduct("Acetylcysteine 200mg", "Mucomyst", "Mucolytic", 25),
                new TestProduct("Guaifenesin Syrup", "Mucinex", "Mucolytic", 21),
                new TestProduct("Loperamide 2mg", "Imodium", "Gastro", 14),
                new TestProduct("Ranitidine 150mg", "Zantac", "Gastro", 17),
                new TestProduct("Probiotic Capsules", "VSL#3", "Supplement", 55),
                new TestProduct("Omega-3 Capsules", "Nordic Naturals", "Supplement", 48),
                new TestProduct("Calcium 600mg", "Caltrate", "Supplement", 20),
                new TestProduct("Magnesium 250mg", "Mag-Tab", "Supplement", 19),
                new TestProduct("Vitamin D3 1000IU", "Nature Made", "Supplement", 16),
                new TestProduct("Aspirin 100mg", "Bayer", "Analgesic", 11),
                new TestProduct("Naproxen 250mg", "Aleve", "Analgesic", 15),
                new TestProduct("Diclofenac 50mg", "Voltaren", "Analgesic", 13),
                new TestProduct("Phenylephrine Syrup", "Sudafed", "Cold", 22)
        );
    }
}

