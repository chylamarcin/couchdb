/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weka;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * Klasa do obsługii biblioteki WEKA.
 *
 * @author Mateusz Ślęzak &amp; Marcin Chyła
 */
public class WekaService {

    /**
     * Pole przechowujące dane typu Instances.
     */
    private Instances data;

    /**
     * Pole przechowujące ścieżkę do pliku z danymi.
     */
    private String fileName;

    /**
     * Bezparametrowy konstruktor klasy. Modyfikator dostępu private
     * uniemożliwia wykorzystanie go do stworzenia obiektu klasy poza nią.
     */
    private WekaService() {
    }

    /**
     * Konstruktor klasy. Ustawia wartość pola przechowującego ścieżkę do pliku
     * z danymi oraz wczytuje dane.
     *
     * @param path ścieżka do pliku
     */
    public WekaService(String path) {
        fileName = path;
        loadData();
    }

    /**
     * Metoda do wczytywania danych.
     */
    private void loadData() {
        try {
            ArffLoader loader = new ArffLoader();
            loader.setFile(new File(fileName));
            data = loader.getDataSet();
        } catch (IOException ex) {

        }
    }

    /**
     * Metoda do zwracania danych.
     *
     * @return Obiekt klasy Instances.
     */
    public Instances getData() {
        return data;
    }

    /**
     * Metoda do pobierania atrybutów z zestawu danych.
     *
     * @return ArrayList zawierająca informacje o atrybutach z wczytanych
     * danych.
     */
    public ArrayList<String> getAttributesName() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < data.numAttributes(); i++) {
            Attribute attribut = data.attribute(i);
            if (attribut.name().equals("class")) {
                list.add("klasa");
            } else {
                list.add(attribut.name());
            }
        }
        return list;
    }

    /**
     * Metoda do pobierania obiektów z zestawu danych.
     *
     * @return ArrayList danych typu Instance pochodzących z wczytanych danych.
     */
    public ArrayList<Instance> getInstances() {
        ArrayList<Instance> list = new ArrayList<>();
        for (int i = 0; i < data.numInstances(); i++) {
            list.add(data.instance(i));
        }
        return list;
    }
}
