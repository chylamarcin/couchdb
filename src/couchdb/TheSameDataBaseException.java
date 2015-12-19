/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package couchdb;

/**
 *
 * @author Mateusz
 */
class TheSameDataBaseException extends Exception {

    public TheSameDataBaseException() {
        super();
    }

    public TheSameDataBaseException(String message) {
        super(message);
    }

    public TheSameDataBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public TheSameDataBaseException(Throwable cause) {
        super(cause);
    }
}
