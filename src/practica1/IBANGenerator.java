/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package practica1;

import java.util.Map;
import java.util.HashMap;


/**
 *
 * @author Adrián
 */
public class IBANGenerator {
    private static final Map<Character, Integer> letraToNumero = new HashMap<>();
    static {
        letraToNumero.put('A', 10); letraToNumero.put('B', 11); letraToNumero.put('C', 12); letraToNumero.put('D', 13);
        letraToNumero.put('E', 14); letraToNumero.put('F', 15); letraToNumero.put('G', 16); letraToNumero.put('H', 17);
        letraToNumero.put('I', 18); letraToNumero.put('J', 19); letraToNumero.put('K', 20); letraToNumero.put('L', 21);
        letraToNumero.put('M', 22); letraToNumero.put('N', 23); letraToNumero.put('O', 24); letraToNumero.put('P', 25);
        letraToNumero.put('Q', 26); letraToNumero.put('R', 27); letraToNumero.put('S', 28); letraToNumero.put('T', 29);
        letraToNumero.put('U', 30); letraToNumero.put('V', 31); letraToNumero.put('W', 32); letraToNumero.put('X', 33);
        letraToNumero.put('Y', 34); letraToNumero.put('Z', 35);
    }

    public static String generarIBAN(String ccc, String paisCodigo) {
        if (ccc == null || ccc.length() != 20 || !ccc.matches("\\d{20}")) return null;
        if (paisCodigo == null || paisCodigo.length() != 2) return null;

        char letra1 = Character.toUpperCase(paisCodigo.charAt(0));
        char letra2 = Character.toUpperCase(paisCodigo.charAt(1));
        Integer num1 = letraToNumero.get(letra1);
        Integer num2 = letraToNumero.get(letra2);

        if (num1 == null || num2 == null) return null;

        String ibanTemporal = ccc + num1 + num2 + "00";

        try {
            java.math.BigInteger numero = new java.math.BigInteger(ibanTemporal);
            int resto = numero.mod(java.math.BigInteger.valueOf(97)).intValue();
            int digitoControl = 98 - resto;
            String dc = (digitoControl < 10) ? "0" + digitoControl : String.valueOf(digitoControl);

            return paisCodigo.toUpperCase() + dc + ccc;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

