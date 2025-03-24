/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package practica1;

/**
 *
 * @author Adrián
 */
public class IBANGenerator {
    public static String generarIBAN(String ccc) {
        if (ccc == null || ccc.length() != 20 || !ccc.matches("\\d{20}")) return null;

        String pais = "ES";
        String ibanTemporal = ccc + "142800"; // ES = 14 y 28

        try {
            // Java no puede manejar números tan grandes con int o long, usamos BigInteger
            java.math.BigInteger numero = new java.math.BigInteger(ibanTemporal);
            int resto = numero.mod(java.math.BigInteger.valueOf(97)).intValue();
            int digitoControl = 98 - resto;
            String dc = (digitoControl < 10) ? "0" + digitoControl : String.valueOf(digitoControl);

            return pais + dc + ccc;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

