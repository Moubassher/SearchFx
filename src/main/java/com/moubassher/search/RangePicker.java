package com.moubassher.search;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.control.RangeSlider;


import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

public class RangePicker extends HBox {

    @FXML
    private TextField maxField, minField;

    @FXML
    private Label rangeLabel;

    @FXML
    private RangeSlider rangeSlider;

    private DecimalFormat currencyFormat = new DecimalFormat("#,###");

    public RangePicker() {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("range_picker.fxml"));

        fxmlLoader.setController(this);
        try {
            HBox root = fxmlLoader.load(); // root created by FXML
            this.getChildren().setAll(root.getChildren()); // copy children into 'this'

        } catch (IOException e) {
            e.printStackTrace();
        }

        linkRangeSliderWithFields(rangeSlider, minField, maxField);
    }


    public void linkRangeSliderWithFields(RangeSlider slider, TextField minField, TextField maxField) {
        DecimalFormat formatter = new DecimalFormat("#,###");

        // --- Slider → Fields (format display) ---
        slider.lowValueProperty()
                .addListener(
                        (obs, oldVal, newVal) -> {
                            minField.setText(formatter.format(newVal.intValue()));
                        }
                );
        slider.highValueProperty().addListener((obs, oldVal, newVal) ->
                maxField.setText(formatter.format(newVal.intValue()))
        );

        // --- Fields → Slider (update on typing or losing focus) ---
        ChangeListener<String> minFieldListener = (obs, oldVal, newVal) -> {
            try {
                Number parsed = formatter.parse(newVal.replaceAll(",", ""));
                slider.setLowValue(parsed.doubleValue());
            } catch (ParseException e) {
                // ignore invalid text temporarily
            }
        };

        ChangeListener<String> maxFieldListener = (obs, oldVal, newVal) -> {
            try {
                Number parsed = formatter.parse(newVal.replaceAll(",", ""));
                slider.setHighValue(parsed.doubleValue());
            } catch (ParseException e) {
                // ignore invalid text temporarily
            }
        };

        minField.textProperty().addListener(minFieldListener);
        maxField.textProperty().addListener(maxFieldListener);

        // --- Optional: range constraints ---
        slider.lowValueProperty().addListener((obs, oldV, newV) -> {
            if (newV.doubleValue() > slider.getHighValue()) {
                slider.setLowValue(slider.getHighValue());
            }
        });
        slider.highValueProperty().addListener((obs, oldV, newV) -> {
            if (newV.doubleValue() < slider.getLowValue()) {
                slider.setHighValue(slider.getLowValue());
            }
        });
    }

}
