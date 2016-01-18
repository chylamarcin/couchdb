/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package couchdb;

import application.state.ParseJSON;
import java.util.ArrayList;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Klasa, która zamienia dane z bazy CouchDB, na dane obsługiwane przez
 * bibiotekę WEKA.
 *
 * @author Mateusz Ślęzak &amp; Marcin Chyła
 */
public class PrepareData {

    /**
     * Pole przechowujące nazwę zbioru danych.
     */
    private String nameData;

    /**
     * Pole przechowujące listę wartości zbioru danych.
     */
    private ArrayList<String> valueList;

    /**
     * Pole przechowujące listę atrybutów zbioru danych.
     */
    private ArrayList<String> attributesList;

    /**
     * Pole przechowujące dokumenty JSON skonwertowane do typu String.
     */
    private ArrayList<String> simpleDocumentList;

    /**
     * Bezparametrowy konstruktor klasy. Modyfikator dostępu private
     * uniemożliwia wykorzystanie go do stworzenia obiektu klasy poza nią.
     */
    private PrepareData() {
    }

    /**
     * Konstruktor klasy. Ustawia wszystkie pola klasy.
     *
     * @param nameData nazwa bazy danych na serwerze CouchDB
     */
    public PrepareData(String nameData) {
        this.nameData = nameData;
        CouchDBService cdbs = new CouchDBService();
        simpleDocumentList = cdbs.getSimpleDocuments(nameData);
        valueList = cdbs.getValues(simpleDocumentList);
        attributesList = cdbs.getAttributes(simpleDocumentList);
    }

    /**
     * Metoda do zmiany danych przechowywanych w dokumentach JSON na dane
     * przechowywane w obiekcie typu Instances.
     *
     * @return Zbiór danych typu Instances.
     */
    public Instances getDataForWeka() {
        CouchDBService cdbs = new CouchDBService();
        ArrayList<String> listOfValues = cdbs.getValues(simpleDocumentList);
        ParseJSON p = new ParseJSON();
        ArrayList<String> listOfSimpleAttributes
                = p.getAttributes(simpleDocumentList.get(0));
        ArrayList<String> listOfComplexAttributes = new ArrayList<>();

        FastVector listOfAttributes = getAtributes(simpleDocumentList,
                listOfValues);
        Instances instances = new Instances(nameData, listOfAttributes, 0);

        for (int j = 0; j < simpleDocumentList.size(); j++) {
            Instance instance = new Instance(listOfAttributes.size());
            instances.add(instance);
        }

        int k = 0;
        for (int i = 0; i < instances.numInstances(); i++) {
            Instance ins = instances.instance(i);
            for (int j = 0; j < ins.numAttributes(); j++) {
                String s = listOfValues.get(k);
                if (s.equals("nn")) {
                    return null;
                }
                if (s.equals("")) {
                    k++;
                    j--;
                    continue;
                }
                if (ins.attribute(j).type() == 0) {
                    double d = 0;
                    try {
                        d = Double.parseDouble(s);
                    } catch (NumberFormatException ex) {
                        d = 0;
                    }
                    ins.setValue(j, d);
                } else {

                    ins.setValue(j, s);
                }
                k++;
            }
        }

        return instances;
    }

    /**
     * Metoda do tworzenia zbioru atrybutów przechowywanych w obiekcie typu
     * FastVector.
     *
     * @param simpleDocuments lista dokumentów typu JSON skonwertowanych do
     * typu String.
     * @param listOfValues lista wartości
     * @return Zbiór atrybutów typu FastVector.
     */
    private FastVector getAtributes(ArrayList<String> simpleDocuments,
            ArrayList<String> listOfValues) {
        FastVector att = new FastVector();
        CouchDBService cdbs = new CouchDBService();
        ParseJSON p = new ParseJSON();
        ArrayList<String> listOfSimpleAttributes
                = p.getAttributes(simpleDocumentList.get(0));
        ArrayList<Attribute> listOfAttribute = new ArrayList<>();
        int i = 0;
        for (String s : listOfSimpleAttributes) {
            try {
                float tmp = Float.parseFloat(listOfValues.get(i));
                Attribute a = new Attribute(s);
                att.addElement(a);
            } catch (NumberFormatException ex) {
                ArrayList<String> list = new ArrayList<>();
                list = cdbs.isNominal(i, listOfValues, simpleDocuments);
                if (list == null) {
                    Attribute a = new Attribute(s, (FastVector) null);
                    att.addElement(a);
                } else {
                    FastVector fv = new FastVector();
                    for (String ss : list) {
                        fv.addElement(ss);
                    }
                    Attribute a = new Attribute(s, fv);
                    att.addElement(a);
                }
            }
            i++;
        }
        return att;
    }
}
