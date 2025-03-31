/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package practica1;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;


/**
 *
 * @author Adrián
 */
public class ExcelManager {
    private static final String CONTRIBUYENTES_FILE = "resources/SistemasVehiculos.xlsx";
    private static final String ORDENANZAS_FILE = "resources/SistemasOrdenanzas.xlsx";
    private static final int COLUMNA_NIF_NIE = 0;
    private static final int COLUMNA_NOMBRE = 3;
    private static final int COLUMNA_APELLIDO1 = 1;
    private static final int COLUMNA_APELLIDO2 = 2;
    private static final String XML_ERRORES = "resources/ErroresNifNie.xml";
    private static List<Map<String, String>> errores = new ArrayList<>();
    private static Set<String> nifDuplicados = new HashSet<>();
    private static Set<String> nifRegistrados = new HashSet<>();
    private static List<Map<String, String>> erroresCCC = new ArrayList<>();
    private static Set<String> correosGenerados = new HashSet<>();

    public static void procesarExcelGeneral() {
        try (FileInputStream fis = new FileInputStream(new File(CONTRIBUYENTES_FILE));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                procesarContribuyente(row);
                
            }

            try (FileOutputStream fos = new FileOutputStream(new File(CONTRIBUYENTES_FILE))) {
                workbook.write(fos);
            }
            
            generarXmlErroresNifNie();
            generarXmlErroresCCC();
            System.out.println("Excel procesado. Se han generado correcciones y erroresCCC.xml si aplicaba.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void procesarContribuyente(Row row) {
        String nifNie = obtenerValorCeldaComoString(row.getCell(0)).trim();
        String nombre = obtenerValorCeldaComoString(row.getCell(3)).trim();
        String apellido1 = obtenerValorCeldaComoString(row.getCell(1)).trim();
        String apellido2 = obtenerValorCeldaComoString(row.getCell(2)).trim();
        int rowNum = row.getRowNum() + 1;
        
        boolean nifNieValido = NifNieValidator.esNifNieValido(nifNie);
        
        boolean tieneDatos = !nombre.isEmpty() || !apellido1.isEmpty() || !apellido2.isEmpty();
                
        if (nifNie.isEmpty()) {
            if (tieneDatos && !nifRegistrados.contains("BLANCO_" + rowNum)) {
                registrarError(row, "NIF BLANCO");
                nifRegistrados.add("BLANCO_" + rowNum); // Evita duplicados
            }
            return;
        }
        
        if(!nifNieValido){
            String nifCorregido = NifNieValidator.calcularLetraCorrecta(nifNie);
            if(nifCorregido != null){
                row.getCell(0).setCellValue(nifCorregido);
                nifNie = nifCorregido;
                nifNieValido = true;
            }else{
                registrarError(row, "NIF ERRÓNEO");
            }
        }

        if (nifRegistrados.contains(nifNie)) {
            if (!nifDuplicados.contains(nifNie)) { // Solo añadir la primera vez
                registrarError(row, "NIF DUPLICADO");
                nifDuplicados.add(nifNie);
            }
            return;
        }

        nifRegistrados.add(nifNie);
        
        
        
        String ccc = obtenerValorCeldaComoString(row.getCell(9)).replaceAll("\\s+", "").trim();
        boolean cccValido = CCCValidator.esCCCValido(ccc);
        boolean cccSubsanable = false;

        
        if(!cccValido){
            String cccCorregido = CCCValidator.corregirCCC(ccc);
            if(cccCorregido != null){
                row.getCell(9).setCellValue(cccCorregido);
                cccValido = true;
                cccSubsanable = true;
            }else{
                Map<String, String> error = new HashMap<>();
                error.put("id", String.valueOf(rowNum));
                error.put("Nombre", nombre);
                error.put("Apellidos", apellido1 + " " + apellido2);
                error.put("NIFNIE", nifNie);
                error.put("CCCErroneo", ccc);
                error.put("TipoError", "IMPOSIBLE GENERAR IBAN");
                erroresCCC.add(error);
            }
        }
        

        boolean ibanGenerable = nifNieValido && cccValido;

        String pais = obtenerValorCeldaComoString(row.getCell(8)).trim(); // columna I = índice 8

        if (ibanGenerable && !pais.isEmpty()) {
            String iban = IBANGenerator.generarIBAN(ccc, pais);
            if (iban != null) {
                row.getCell(10, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(iban);
                if(cccSubsanable){
                    Map<String, String> error = new HashMap<>();
                    error.put("id", String.valueOf(rowNum));
                    error.put("Nombre", nombre);
                    error.put("Apellidos", apellido1 + " " + apellido2);
                    error.put("NIFNIE", nifNie);
                    error.put("CCCErroneo", ccc);
                    error.put("IBANCorrecto", iban);
                    erroresCCC.add(error);
                }
            }
        }
        
        String email = obtenerValorCeldaComoString(row.getCell(6)).trim();
         
        boolean generarCorreo = ibanGenerable && email.isEmpty();

        if (generarCorreo) {
            String base = (nombre.isEmpty() ? "" : nombre.substring(0,1).toLowerCase())
                        + (apellido1.isEmpty() ? "" : apellido1.substring(0,1).toLowerCase())
                        + (apellido2.isEmpty() ? "" : apellido2.substring(0,1).toLowerCase());

            int sufijo = 0;
            String correoGenerado;
            do {
                String numero = (sufijo < 10) ? "0" + sufijo : String.valueOf(sufijo);
                correoGenerado = base + numero + "@vehiculos2025.com";
                sufijo++;
            } while (correosGenerados.contains(correoGenerado)); // Lista global para evitar duplicados

            correosGenerados.add(correoGenerado);
            row.getCell(6, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(correoGenerado);
    }
}

    public static void validarYCorregirNifNie() {
        try (FileInputStream fis = new FileInputStream(new File(CONTRIBUYENTES_FILE));
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Ignorar la primera fila
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                String nifNie = obtenerValorCeldaComoString(row.getCell(COLUMNA_NIF_NIE)).trim();
                String nombre = obtenerValorCeldaComoString(row.getCell(COLUMNA_NOMBRE));
                String apellido1 = obtenerValorCeldaComoString(row.getCell(COLUMNA_APELLIDO1));
                String apellido2 = obtenerValorCeldaComoString(row.getCell(COLUMNA_APELLIDO2));
                
                boolean tieneDatos = !nombre.isEmpty() || !apellido1.isEmpty() || !apellido2.isEmpty();
                
                if (nifNie.isEmpty()) {
                    if (tieneDatos && !nifRegistrados.contains("BLANCO_" + i)) {
                        registrarError(row, "NIF BLANCO");
                        nifRegistrados.add("BLANCO_" + i); // Evita duplicados
                    }
                    continue;
                }
                
                if (nifRegistrados.contains(nifNie)) {
                    if (!nifDuplicados.contains(nifNie)) { // Solo añadir la primera vez
                        registrarError(row, "NIF DUPLICADO");
                        nifDuplicados.add(nifNie);
                    }
                    continue;
                }
                
                nifRegistrados.add(nifNie);
                
                if (!NifNieValidator.esNifNieValido(nifNie)) {
                    String nifNieCorregido = NifNieValidator.calcularLetraCorrecta(nifNie);
                    if (nifNieCorregido != null) {
                        row.getCell(COLUMNA_NIF_NIE).setCellValue(nifNieCorregido);
                    } else {
                        registrarError(row, "NIF ERRÓNEO");
                    }
                }
            }
            
            try (FileOutputStream fos = new FileOutputStream(new File(CONTRIBUYENTES_FILE))) {
                workbook.write(fos);
                workbook.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (!errores.isEmpty()) {
            generarXmlErroresNifNie();
        }
    }
    
    private static void registrarError(Row row, String tipoError) {
        Map<String, String> error = new HashMap<>();
        error.put("id", String.valueOf(row.getRowNum() + 1)); // Para que coincida con Excel (1-based index)
        error.put("NIF_NIE", obtenerValorCeldaComoString(row.getCell(COLUMNA_NIF_NIE)));
        error.put("Nombre", obtenerValorCeldaComoString(row.getCell(COLUMNA_NOMBRE)));
        error.put("PrimerApellido", obtenerValorCeldaComoString(row.getCell(COLUMNA_APELLIDO1)));
        error.put("SegundoApellido", obtenerValorCeldaComoString(row.getCell(COLUMNA_APELLIDO2)));
        error.put("TipoDeError", tipoError);
        errores.add(error);
    }
    
    private static String obtenerValorCeldaComoString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        } else {
            return "";
        }
    }
    
    private static void generarXmlErroresNifNie() {
        try (FileWriter writer = new FileWriter(XML_ERRORES)) {
            writer.write("<Contribuyentes>\n");
            for (Map<String, String> error : errores) {
                writer.write(String.format("    <Contribuyente id=\"%s\">\n", error.get("id")));
                writer.write(String.format("        <NIF_NIE>%s</NIF_NIE>\n", error.get("NIF_NIE")));
                writer.write(String.format("        <Nombre>%s</Nombre>\n", error.get("Nombre")));
                writer.write(String.format("        <PrimerApellido>%s</PrimerApellido>\n", error.get("PrimerApellido")));
                writer.write(String.format("        <SegundoApellido>%s</SegundoApellido>\n", error.get("SegundoApellido")));
                writer.write(String.format("        <TipoDeError>%s</TipoDeError>\n", error.get("TipoDeError")));
                writer.write("    </Contribuyente>\n");
            }
            writer.write("</Contribuyentes>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void generarXmlErroresCCC() {
        try (FileWriter writer = new FileWriter("resources/ErroresCCC.xml")) {
            writer.write("<Cuentas>\n");
            for (Map<String, String> error : erroresCCC) {
                // Saltar si no hay CCC erróneo real (por seguridad)
                String cccErroneo = error.get("CCCErroneo");
                if (cccErroneo == null || cccErroneo.trim().isEmpty()) continue;

                writer.write(String.format("  <Cuenta id=\"%s\">\n", error.get("id")));
                writer.write(String.format("    <Nombre>%s</Nombre>\n", error.get("Nombre")));
                writer.write(String.format("    <Apellidos>%s</Apellidos>\n", error.get("Apellidos")));
                writer.write(String.format("    <NIFNIE>%s</NIFNIE>\n", error.get("NIFNIE")));
                writer.write(String.format("    <CCCErroneo>%s</CCCErroneo>\n", cccErroneo));

                if (error.containsKey("IBANCorrecto")) {
                    writer.write(String.format("    <IBANCorrecto>%s</IBANCorrecto>\n", error.get("IBANCorrecto")));
                } else if (error.containsKey("TipoError")) {
                    writer.write(String.format("    <TipoError>%s</TipoError>\n", error.get("TipoError")));
                }

                writer.write("  </Cuenta>\n");
            }
            writer.write("</Cuentas>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<List<String>> leerExcel(String filePath, int hojaIndex) {
        List<List<String>> datos = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(hojaIndex);
            for (Row row : sheet) {
                List<String> fila = new ArrayList<>();
                for (Cell cell : row) {
                    fila.add(cell.toString());
                }
                datos.add(fila);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datos;
    }

    public static void modificarExcel(String filePath, int hojaIndex, int fila, int columna, String nuevoValor) {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(hojaIndex);
            Row row = sheet.getRow(fila);
            if (row == null) row = sheet.createRow(fila);
            Cell cell = row.getCell(columna, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            cell.setCellValue(nuevoValor);
            try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
