/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application.state;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Klasa obsługująca wczytywanie i zapisywanie danych do połączenia z bazą
 * CouchDB. Dane przechowywane w bazie SQLite.
 *
 * @author Mateusz Ślęzak &amp; Marcin Chyła
 */
public class ApplicationSettings {

    /**
     * Obiekt typu Connection. Przechowuje aktualne połączenie z bazą SQLite
     */
    private Connection c;

    /**
     * Obiekt typu Statement. Wynonuje operacje na bazie.
     */
    private Statement stmt;

    /**
     * Obiekt typu ResultSet. Przechowuje wynik zapytania SELECT.
     */
    private ResultSet rs;

    /**
     * Metoda do otwierania połączenia z bazą SQLite.
     */
    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:settings.db");
        } catch (Exception e) {

        }
    }

    /**
     * Metoda do zamykania połączenia z bazą SQLite.
     */
    private void close() {
        try {
            if (rs != null) {
                c.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (c != null) {
                c.close();
            }
        } catch (SQLException ex) {

        }
    }

    /**
     * Metoda do pobierania adresu serwera CouchDB.
     *
     * @return Tablica, której pierwszym elementem jest adres URL serwera. Drugi
     * element to port, na którym serwer bazy CouchDB nasłuchuje.
     */
    public String[] getAddressDB() {
        String[] tab = new String[2];
        try {
            connect();
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM address;");
            while (rs.next()) {
                tab[0] = rs.getString("url");
                tab[1] = rs.getString("port");
            }
            close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return tab;
    }

    /**
     * Metoda do zapisywania adresu serwera CouchDB.
     *
     * @param tab tablica ustawień, pierwszy element to nowy adres URL, drugi
     * element to nowy port serwera CouchDB.
     * @return True, jeśli zapis do bazy zakończony powodzeniem, false w
     * przeciwnym wypadku.
     */
    public boolean setAdressDB(String[] tab) {
        try {
            connect();
            stmt = c.createStatement();
            String sql = "UPDATE address set url = '" + tab[0] + "', port=" + tab[1];
            stmt.executeUpdate(sql);
            close();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

}
