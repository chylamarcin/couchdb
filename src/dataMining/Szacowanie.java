/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataMining;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 *
 * @author Mateusz Ślęzak
 */
public class Szacowanie {

    /**
     * Ścieżka do pliku z danymi sklasyfikowanymi przez klasyfikator kNN
     */
    private String testKNNFile;

    /**
     * Ścieżka do pliku z oryginalnymi danymi testowymi
     */
    private String testFile;

    /**
     * Dane z klasyfikatora kNN
     */
    private Instances testKNNData;

    /**
     * Oryginalne dane testowe
     */
    private Instances testData;

    /**
     * Konstruktor klasy. Ustawia wartość pól z danymi.
     *
     * @param kNNData dane klasyfikatora kNN
     * @param testData dane testowe
     */
    public Szacowanie(Instances kNNData, Instances testData) {
        testKNNData = kNNData;
        this.testData = testData;
        //openFiles();
    }

    /**
     * Metoda do wczytywania danych.
     */
    @Deprecated
    private void openFiles() {
        try {
            ArffLoader load = new ArffLoader();
            load.setFile(new File(testKNNFile));
            testKNNData = load.getDataSet();
            load.setFile(new File(testFile));
            testData = load.getDataSet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda do przelądania danych i szacowania poprawności klasyfikacji.
     *
     * @return Wynik oceny klasyfikacji.
     */
    public String reviewData() {
        float correctCount = 0;
        for (int i = 0; i < testKNNData.numInstances(); i++) {
            Instance kNN = testKNNData.instance(i);
            Instance test = testData.instance(i);
            if (kNN.toString(10).equals(test.toString(10))) {
                correctCount++;
            }
        }
        float correct = (correctCount / 99) * 100;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        String s = "%\tWynik Klasyfikacji:\n%\t\tSklasyfikowanych obiektów: "
                + testKNNData.numInstances() + "\n%\t\tPoprawnie Sklasyfikowanych: "
                + (int) correctCount + "\n%\t\tPoprawna Klasyfikacja na poziom"
                + "ie: " + df.format(correct) + "\n";
        return s;
    }

}
