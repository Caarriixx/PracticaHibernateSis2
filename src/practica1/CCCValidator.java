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
public class CCCValidator {
    private static final int[] FACTORES = {1, 2, 4, 8, 5, 10, 9, 7, 3, 6};

    public static boolean esCCCValido(String ccc) {
        if (ccc == null || ccc.length() != 20 || !ccc.matches("\\d{20}")) return false;

        String entidad = ccc.substring(0, 4);
        String oficina = ccc.substring(4, 8);
        String dc = ccc.substring(8, 10);
        String cuenta = ccc.substring(10);

        String dcCalculado = calcularDigitosControl(entidad, oficina, cuenta);
        return dc.equals(dcCalculado);
    }

    public static String corregirCCC(String ccc) {
        if (ccc == null || ccc.length() != 20 || !ccc.matches("\\d{20}")) return null;

        String entidad = ccc.substring(0, 4);
        String oficina = ccc.substring(4, 8);
        String cuenta = ccc.substring(10);

        String dcCorrecto = calcularDigitosControl(entidad, oficina, cuenta);
        return entidad + oficina + dcCorrecto + cuenta;
    }

    private static String calcularDigitosControl(String entidad, String oficina, String cuenta) {
        String bloque1 = "00" + entidad + oficina;
        String bloque2 = cuenta;

        int dc1 = calcularDC(bloque1);
        int dc2 = calcularDC(bloque2);

        return "" + dc1 + dc2;
    }

    private static int calcularDC(String bloque) {
        int suma = 0;
        for (int i = 0; i < 10; i++) {
            int digito = Character.getNumericValue(bloque.charAt(i));
            suma += digito * FACTORES[i];
        }
        int resto = suma % 11;
        int resultado = 11 - resto;
        if (resultado == 10) return 1;
        if (resultado == 11) return 0;
        return resultado;
    }
}
