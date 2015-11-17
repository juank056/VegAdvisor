/**
 *
 */
package com.vegadvisor.client.util;

import java.util.Random;

/**
 * @author Juan Camilo Esta clase va a manejar todo lo relacionado con los
 *         passwords
 */
public class PasswordManager {

    /**
     * Caracteres
     */
    private static String[] characters = {"A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7",
            "8", "9", "0", "!", "@", "#", "$", "+", "%", "^", "&", "*", "(",
            "<", "?", "~", ")", ">", "[", "]", "{", "}", "?"};

    /**
     * Constructor del Manager de Passwords
     */
    private PasswordManager() {
    }

    /**
     * Genera un Password aleatorio para un usuario
     *
     * @param fName  Primer Nombre del Usuario
     * @param mName  Segundo Nombre
     * @param lName  Primer Apellido
     * @param slName Segundo Apellido
     * @return
     */
    public static String generatePassword(String fName, String mName,
                                          String lName, String slName) {
        String passwd = Constants.BLANKS;
        passwd += fName.substring((new Random()).nextInt(fName.length()));
        passwd += generateTrash(2);
        if (mName.length() > 0)
            passwd += mName.substring((new Random()).nextInt(mName.length()));
        passwd += generateTrash(3);
        passwd += lName.substring((new Random()).nextInt(lName.length()));
        passwd += generateTrash(3);
        if (slName.length() > 0)
            passwd += slName.substring((new Random()).nextInt(slName.length()));
        passwd += generateTrash(2);
        return passwd.trim();
    }

    /**
     * Genera una cadena aleatoria de Longitud i
     *
     * @param i Longitud de la cadena que se quiere generar
     * @return Cadena de Caracteres aleatorios de longitud i
     */
    private static String generateTrash(int i) {
        String trash = Constants.BLANKS;
        for (int j = 0; j < i; j++) {
            trash += characters[(new Random()).nextInt(characters.length)];
        }
        return trash;
    }

    /**
     * Este metodo encripta un password
     *
     * @param passw el password en texto claro
     * @return el password encriptado
     */
    public static String encryptPassword(String passw) {
        return Crypto.getStringMessageDigest(passw, Crypto.MD5);
    }

}
