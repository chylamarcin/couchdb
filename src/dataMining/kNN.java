/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataMining;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

/**
 *
 * @author Mateusz Ślęzak
 */
public class kNN {

    /**
     * Współczynnik k klasyfikatora kNN
     */
    private int k;
    private int numAtt;

    /**
     * Ścieżka do pliku z danymi treningowymi
     */
    private String attributName;

    /**
     * Dane treningowe
     */
    private Instances treningData;

    /**
     * Dane testowe
     */
    private Instances testData;

    /**
     * Konstruktor klasy. Ustawia wartość pola danymi. Ustawia także wartość
     * parametru k klasyfikatora.
     *
     * @param newK współczynnik k klasyfikatora
     * @param testData Dane testowe
     * @param trainingData dane treningowe
     * @param attName nazwa atrybutu, dla którego prowadzona będzie klasyfikacja
     */
    public kNN(int newK, Instances trainingData, Instances testData, String attName) {
        k = newK;
        this.testData = testData;
        this.treningData = trainingData;
        this.attributName = attName;

    }

    /**
     * Metoda wczytująca dane do programu.
     */
    @Deprecated
    private void openFiles() {
        try {
            ArffLoader load = new ArffLoader();

            treningData = load.getDataSet();

            testData = load.getDataSet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Instances getData() {
        return testData;
    }

    /**
     * Metoda, w której wyszukiwane jest k najbliższych sąsiadów ze zbioru
     * danych treningowych dla danych testowych, następnie proponowana jest
     * decyzja która wystąpiła na największej liczbie spośród k sąsiadów.
     *
     * @return Zestaw zmienionych danych typu String.
     */
    public String reviewData() {
        String st = "";
        int type = 0;
        numAtt = 0;
        for (int i = 0; i < testData.numAttributes(); i++) {
            String s = testData.attribute(i).name();
            if (s.equals(attributName)) {
                numAtt = i;
                type = testData.attribute(i).type();
                break;
            }
        }
        for (int i = 0; i < testData.numInstances(); i++) {
            try {
                Instance ins = testData.instance(i);
                if (type == 0) {
                    ins.setValue(numAtt, selectValue(findNeighbors(ins)));
                } else {
                    ins.setValue(numAtt, selectValue(findNeighbors(ins)));
                }
                st = st + ins.toString() + "\n";
            } catch (IndexOutOfBoundsException e) {

            }

        }

        return st;
    }

    /**
     * Metoda do wyszykiwania najczęstrzej decyzji spośród sąsiadów z listy
     * przekazywanej jako parametr.
     *
     * @param list lista sąsiadów
     * @return Decyzja.
     */
    private double selectValue(ArrayList<Instance> list) {
        HashMap<Double, Integer> valuesMap = new HashMap<>();
        for (Instance i : list) {
            if (valuesMap.containsKey(Double.parseDouble(i.toString(numAtt)))) {
                int values = valuesMap.get(Double.parseDouble(i.toString(numAtt))) + 1;
                valuesMap.replace(Double.parseDouble(i.toString(numAtt)), values);
            } else {
                valuesMap.put(Double.parseDouble(i.toString(numAtt)), 1);
            }
        }
        Collection c = valuesMap.values();
        Iterator it = c.iterator();
        int max = Integer.parseInt(it.next().toString());
        while (it.hasNext()) {
            int isMax = Integer.parseInt(it.next().toString());
            if (max < isMax) {
                max = isMax;
            }
        }
        double value = 0;
        for (Double d : valuesMap.keySet()) {
            if (valuesMap.get(d).equals(max)) {
                value = d;
            }
        }

        return value;
    }

    /**
     * Metoda do wyszukiwania k sąsiadów.
     *
     * @param ins obiekt dla którego szukamy sąsiedztwa
     * @return Lista sąsiadów.
     */
    private ArrayList<Instance> findNeighbors(Instance ins) {
        ArrayList<Integer> listOfDistances = new ArrayList<>();
        for (int i = 0; i < treningData.numInstances(); i++) {
            int dist = manhattan(ins, treningData.instance(i));
            listOfDistances.add(dist);
        }

        ArrayList<Integer> listOfKDistances = new ArrayList<>();
        int countOfNeighbors = k;
        while (countOfNeighbors > 0) {
            int min = listOfDistances.get(0);
            for (Integer i : listOfDistances) {
                if (i < min) {
                    min = i;
                }
            }
            listOfKDistances.add(min);
            boolean i = listOfDistances.remove((Object) min);
            countOfNeighbors--;
        }

        ArrayList<Instance> listOfNeighbors = new ArrayList<>();
        for (Integer i : listOfKDistances) {
            for (int j = 0; j < treningData.numInstances(); j++) {
                Instance inst = treningData.instance(j);
                if (manhattan(ins, inst) == i && !listOfNeighbors.contains(inst)) {
                    listOfNeighbors.add(inst);
                    break;
                }
            }
        }
        return listOfNeighbors;
    }

    /**
     * Metoda do liczenia odległości metryką Manhattan.
     *
     * @param a pierwszy obiekt
     * @param b drugi obiekt
     * @return odległość
     */
    private int manhattan(Instance a, Instance b) {
        int sum = 0;
        for (int i = 0; i < a.numAttributes(); i++) {
            if (i == numAtt) {
                continue;
            } else {
                int tmp = 0;
                try {
                    tmp = Math.abs(Integer.parseInt(a.toString(i))
                            - Integer.parseInt(b.toString(i)));
                } catch (NumberFormatException ex) {
                    tmp = 0;
                }
                sum = sum + tmp;
            }
        }
        return sum;
    }

    /**
     * Metoda do zapisywania danych.
     *
     * @param fileName ścieżka, pod którą dane mają być zapisane
     */
    private void saveData(String fileName) {
        try {
            ArffSaver save = new ArffSaver();
            save.setFile(new File(fileName));
            save.setInstances(testData);
            save.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
