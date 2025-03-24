/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package practica1;

import java.util.HashMap;
import java.util.Map;

public class NifNieValidator {
    private static final String LETRAS = "TRWAGMYFPDXBNJZSQVHLCKE";
    private static final Map<Character, Integer> NIE_PREFIXES = new HashMap<>();
    
    static {
        NIE_PREFIXES.put('X', 0);
        NIE_PREFIXES.put('Y', 1);
        NIE_PREFIXES.put('Z', 2);
    }

    public static boolean esNifNieValido(String nifNie) {
        if (nifNie == null || nifNie.length() < 8 || nifNie.length() > 9) {
            return false;
        }
        
        String numeroParte;
        char letraEsperada;
        
        if (Character.isLetter(nifNie.charAt(0))) { // Es un NIE
            Integer prefijo = NIE_PREFIXES.get(nifNie.charAt(0));
            if (prefijo == null) return false;
            numeroParte = prefijo + nifNie.substring(1, 8);
        } else { // Es un NIF
            numeroParte = nifNie.substring(0, 8);
        }
        
        if (!numeroParte.matches("\\d{8}")) { // Verificar que tenga exactamente 8 dígitos
            return false;
        }
        
        try {
            int numero = Integer.parseInt(numeroParte);
            letraEsperada = LETRAS.charAt(numero % 23);
        } catch (NumberFormatException e) {
            return false;
        }
        
        return Character.toUpperCase(nifNie.charAt(nifNie.length() - 1)) == letraEsperada;
    }
    
    public static String calcularLetraCorrecta(String nifNie) {
        if (nifNie == null || nifNie.length() < 8 || nifNie.length() > 9) {
            return null;
        }
        
        String numeroParte;
        
        if (Character.isLetter(nifNie.charAt(0))) { // NIE
            Integer prefijo = NIE_PREFIXES.get(nifNie.charAt(0));
            if (prefijo == null) return null;
            numeroParte = prefijo + nifNie.substring(1, 8);
        } else { // NIF
            numeroParte = nifNie.substring(0, 8);
        }
        
        if (!numeroParte.matches("\\d{8}")) { // Verificar que tenga exactamente 8 dígitos
            return null;
        }
        
        try {
            int numero = Integer.parseInt(numeroParte);
            return numeroParte + LETRAS.charAt(numero % 23);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}