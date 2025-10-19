package com.moubassher.search;

import com.moubassher.search.testSearchables.TestProduct;
import javafx.application.Application;
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

        SearchBar searchBar = new SearchBar();

        ListView<TestProduct> results = new ListView<>();
        searchBar.activateSearch(products, new TestProduct(), queryResults -> {
            ObservableList<TestProduct> matches = (ObservableList<TestProduct>) queryResults;
            results.setItems(matches);

            return results;
        });

        VBox root = new VBox(10, searchBar, results);
        Scene scene = new Scene(root, 500, 500);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
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
                new TestProduct("Zinc Tablets", "Redoxon", "Supplement"),
                new TestProduct("Amoxicillin 500mg", "Amoxil", "Antibiotic"),
                new TestProduct("Amoxicillin Syrup", "Moxatag", "Antibiotic"),
                new TestProduct("Azithromycin 250mg", "Zithromax", "Antibiotic"),
                new TestProduct("Dextromethorphan Syrup", "Vicks", "Cold"),
                new TestProduct("Loratadine 10mg", "Claritin", "Allergy"),
                new TestProduct("Cetirizine 10mg", "Zyrtec", "Allergy"),
                new TestProduct("Levocetirizine 5mg", "Xyzal", "Allergy"),
                new TestProduct("Omeprazole 20mg", "Losec", "Gastro"),
                new TestProduct("Esomeprazole 40mg", "Nexium", "Gastro"),
                new TestProduct("Pantoprazole 40mg", "Pantoloc", "Gastro"),
                new TestProduct("Metformin 500mg", "Glucophage", "Diabetes"),
                new TestProduct("Glimepiride 2mg", "Amaryl", "Diabetes"),
                new TestProduct("Insulin Glargine 100IU", "Lantus", "Diabetes"),
                new TestProduct("Salbutamol Inhaler", "Ventolin", "Respiratory"),
                new TestProduct("Fluticasone Inhaler", "Flixotide", "Respiratory"),
                new TestProduct("Montelukast 10mg", "Singulair", "Respiratory"),
                new TestProduct("Prednisone 20mg", "Deltasone", "Steroid"),
                new TestProduct("Hydrocortisone Cream", "Cortizone", "Steroid"),
                new TestProduct("Betamethasone Ointment", "Diprolene", "Steroid"),
                new TestProduct("Acetylcysteine 200mg", "Mucomyst", "Mucolytic"),
                new TestProduct("Guaifenesin Syrup", "Mucinex", "Mucolytic"),
                new TestProduct("Loperamide 2mg", "Imodium", "Gastro"),
                new TestProduct("Ranitidine 150mg", "Zantac", "Gastro"),
                new TestProduct("Probiotic Capsules", "VSL#3", "Supplement"),
                new TestProduct("Omega-3 Capsules", "Nordic Naturals", "Supplement"),
                new TestProduct("Calcium 600mg", "Caltrate", "Supplement"),
                new TestProduct("Magnesium 250mg", "Mag-Tab", "Supplement"),
                new TestProduct("Vitamin D3 1000IU", "Nature Made", "Supplement"),
                new TestProduct("Aspirin 100mg", "Bayer", "Analgesic"),
                new TestProduct("Naproxen 250mg", "Aleve", "Analgesic"),
                new TestProduct("Diclofenac 50mg", "Voltaren", "Analgesic"),
                new TestProduct("Phenylephrine Syrup", "Sudafed", "Cold")
        );
    }
}

