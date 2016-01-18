/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.state;

/**
 * Klasa do obsługii ustawień serwera CouchDB.
 *
 * @author Mateusz Ślęzak &amp; Marcin Chyła
 */
public class ApplicationState {

    /**
     * Pole przechowujące adres URL serwera CouchDB.
     */
    private String URL;

    /**
     * Pole przechowujące numer portu serwera CouchDB.
     */
    private String port;

    /**
     * Pole przechowujące komplet ustawień serwera CouchDB w postaci tabeli.
     */
    private String[] tab;

    /**
     * Konstruktor klasy. Uruchamia metodę, która pobiera ustawienia z bazy
     * SQLite i przypisuje odpowienie wartości do odpowiednich pól klasy.
     */
    public ApplicationState() {
        fillTabFromSettings();
        URL = tab[0];
        port = tab[1];
    }

    /**
     * Metoda, która pobiera dane z bazy SQLite. Wykorzystuje klasę
     * ApplicationSettings.
     */
    private void fillTabFromSettings() {
        ApplicationSettings as = new ApplicationSettings();
        tab = as.getAddressDB();
    }

    /**
     * Metoda, która zmienia wartości tabeli przechowującej kompletne dane
     * serwera bazy CouchDB. Następnie dane te przesyłąne są do bazy SQLite.
     *
     * @param URL nowy adres serwera CouchDB
     * @param port nowy port serwera CouchDB
     */
    public void fillTabFromApplication(String URL, String port) {
        tab[0] = URL;
        tab[1] = port;
        saveTab();
    }

    /**
     * Metoda, która zwraca tablicę zawierającą kompletne dane ustawień serwera
     * CouchDB.
     *
     * @return Tablica ustawień serwera CouchDB.
     */
    public String[] sendTabToApplication() {
        return tab;
    }

    /**
     * Metoda, która zapisuje ustawienia przechowywane w tablicy ustawień do
     * bazy SQLite. Wykorzystuje klasę ApplicationSettings.
     *
     * @return True, jeśli ustawinia zostały poprawnie zapisane, false w
     * przeciwnym wypadku.
     */
    private boolean saveTab() {
        ApplicationSettings as = new ApplicationSettings();
        return as.setAdressDB(tab);
    }
}
