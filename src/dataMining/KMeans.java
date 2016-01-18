/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataMining;

import couchdb.PrepareData;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 *
 * @author Mateusz Ślęzak
 */
public class KMeans {

    /**
     * Pole przechowujące wczytane dane
     */
    private Instances dataSet;

    /**
     * Mapa przechowująca grupy z ich środkami w postaci klucz - środek wartość
     * - elementy grupy
     */
    private HashMap<Instance, ArrayList<Instance>> groups;

    /**
     * Pole przechowujące liczbę grup
     */
    private int countOfGroups;

    /**
     * Konstruktor klasy. Wczytuje dane oraz losuje początkowe środki grup.
     *
     * @param nameData nazwa bazy
     * @param countGroups ilość grup
     */
    public KMeans(String nameData, int countGroups) {
        PrepareData pd = new PrepareData(nameData);
        dataSet = pd.getDataForWeka();
        countOfGroups = countGroups;
        createGroups();
    }

    /**
     * Metoda do wczytywania danych do programu.
     */
    @Deprecated
    private void openFile() {
        try {
            ArffLoader loader = new ArffLoader();
            loader.setFile(new File(""));
            dataSet = loader.getDataSet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Główna metoda klasy. Operacje wykonują się w pętli while. Warunkiem
     * wyjścia z pętli jest dwukrotne z rzędu wyznaczenie takich samych
     * współrzędnych środka grupy. Pierwszy etap wykonywany w pętli to
     * przyporządkowanie obiektów do grup, w zależności od tego, do którego
     * środka jest najbliżej. Następnie wyznaczny jest nowy środek grup jako
     * średnia arytmetyczna kolejnych współrzędnych obiektów należących do
     * grupy. Jeśli wszystkie grupy nie zmieniły swoich środków to następuje
     * wyjście z pętli, w przeciwnym wypadku pętla wkonuje się ponownie. Jeśli
     * pętla wykonała się 1000 razy również zostanie zatrzymana. Wówczas zostaną
     * przekazane grupy stworzone w ostatniej iteracji.
     *
     * @return Wynik grupowania.
     */
    public String reviewData() {

        boolean working = true;
        int tmp = 0;
        int checkPoint = 0;
        int countTheSame = 0;
        int isLooped = 0;
        while (working) {
            for (int i = 0; i < dataSet.numInstances(); i++) {
                Instance ins = dataSet.instance(i);
                findGroup(ins);
            }
            ArrayList<Instance> listOfNewMeans = makeNewMeans();
            for (Instance i : groups.keySet()) {
                if (equals(i, listOfNewMeans.get(checkPoint))) {
                    countTheSame++;
                }
                checkPoint++;
            }
            if (checkPoint == countTheSame || isLooped > 1000) {
                working = false;
            } else {

                checkPoint = 0;
                countTheSame = 0;
                groups = new HashMap<>();
                for (Instance in : listOfNewMeans) {
                    groups.put(in, new ArrayList<>());
                }
            }
            isLooped++;
        }
        String s = "";
        if (!working && tmp == 0) {
            int i = 1;

            for (Instance instance : groups.keySet()) {
                ArrayList<Instance> list = groups.get(instance);
                s = s + "**************************************************\n";
                s = s + "Grupa: " + i + "\n";
                s = s + "Liczba elementów w grupie: " + list.size() + "\n";
                s = s + "Środek grupy: " + instance.toString() + "\n";
                s = s + "Elementy grupy: \n";
                for (Instance ins : list) {
                    s = s + ins.toString() + "\n";
                }
                s = s + "\n";
                i++;

            }
        }

        return s;

    }

    /**
     * Metoda do sprawdzania czy instancje podane jako parametr są takie same.
     *
     * @param insA piewrsza instanca
     * @param insB druga instancja
     * @return True, jeśli są takie same, false w przeciwnym wypadku.
     */
    private boolean equals(Instance insA, Instance insB) {
        if (insA.numAttributes() != insB.numAttributes()) {
            throw new NullPointerException("Różna liczba atrybutów");
        } else {
            int countTheSame = 0;
            for (int i = 0; i < insA.numAttributes(); i++) {
                double a = 0;
                double b = 0;
                try {
                    a = Double.parseDouble(insA.toString(i));
                } catch (NumberFormatException ex) {
                    a = 0;
                }
                try {
                    b = Double.parseDouble(insB.toString(i));
                } catch (NumberFormatException ex) {
                    b = 0;
                }
                if (Math.abs(a - b) == 0) {
                    countTheSame++;
                }

            }
            int countAttr = insB.numAttributes();

            if (countTheSame == countAttr) {
                return true;
            } else {
                return false;
            }

        }

    }

    /**
     * Metoda, która przyporządkowuje obiekt podany jako parametr do grupy,
     * której środek jest najbliżej.
     *
     * @param ins obiekt, który ma być przypisany do grupy.
     */
    private void findGroup(Instance ins) {
        ArrayList<Double> list = new ArrayList<>();
        for (Instance instance : groups.keySet()) {
            list.add(euclid(ins, instance));
        }
        double min = list.get(0);
        for (Double d : list) {
            if (min > d) {
                min = d;
            }
        }
        for (Instance instance : groups.keySet()) {
            if (euclid(ins, instance) == min) {
                ArrayList<Instance> listOfInstances = groups.get(instance);
                listOfInstances.add(ins);
                groups.replace(instance, listOfInstances);

            }
        }
    }

    /**
     * Metoda do wyznaczania nowych środków grup.
     *
     * @return lista zawierająca nowe środki.
     */
    private ArrayList<Instance> makeNewMeans() {
        ArrayList<Instance> listOfMeans = new ArrayList<>();
        for (Instance i : groups.keySet()) {
            ArrayList<Instance> list = groups.get(i);
            double[] tab = new double[i.numAttributes()];
            for (Instance in : list) {
                for (int j = 0; j < tab.length; j++) {
                    double d = 0;
                    try {
                        d = Double.parseDouble(in.toString(j));
                    } catch (NumberFormatException ex) {
                        d = 0;
                    }
                    tab[j] = tab[j] + d;
                }
            }
            for (int j = 0; j < tab.length; j++) {
                tab[j] = tab[j] / list.size();
            }
            Instance ins = new Instance(tab.length);
            for (int j = 0; j < tab.length; j++) {
                ins.setValue(j, tab[j]);
            }
            listOfMeans.add(ins);
        }
        return listOfMeans;
    }

    /**
     * Metoda do wyznaczania odległości pomiedzy obiektami podanymi jako
     * parametr. Odległość liczona metryką Euklidesową.
     *
     * @param a pierwszy obiekt
     * @param b drugi obiekt
     * @return Odległość pomiędzy obiektami.
     */
    private double euclid(Instance a, Instance b) {
        float sum = 0;
        float attrA = 0;
        float attrB = 0;
        for (int i = 0; i < a.numAttributes(); i++) {
            try {
                attrA = Float.parseFloat(a.toString(i));
            } catch (NumberFormatException ex) {
                attrA = 0;
            }
            try {
                attrB = Float.parseFloat(b.toString(i));
            } catch (NumberFormatException ex) {
                attrB = 0;
            }

            float d = attrA - attrB;
            sum = sum + d * d;
        }
        return Math.sqrt(sum);
    }

    /**
     * Metoda wywołana w konstruktrze. Z początkowego zbioru danych losuje
     * obiekty, które na początku będą środkami i ustawia je jako klucze w
     * mapie, która przechowuje grupy.
     */
    private void createGroups() {
        groups = new HashMap<>();
        int countGroups = countOfGroups;
        int countInstances = dataSet.numInstances();
        Random r = new Random();
        while (countGroups > 0) {
            groups.put(dataSet.instance(r.nextInt(countInstances + 1)), new ArrayList<>());
            countGroups--;
        }
    }
}
