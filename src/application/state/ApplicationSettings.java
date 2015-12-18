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
 *
 * @author Mateusz
 */
public class ApplicationSettings {

    private Connection c;
    private Statement stmt;
    private ResultSet rs;

    private void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:settings.db");
        } catch (Exception e) {
            
        }
    }

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
    
    public boolean setAdressDB(String[] tab){
        try{
            connect();
            stmt = c.createStatement();
            String sql = "UPDATE address set url = '"+tab[0]+"', port="+tab[1];
            stmt.executeUpdate(sql);
            close();
            return true;
        }catch(SQLException ex){
            return false;
        }
    }

}
