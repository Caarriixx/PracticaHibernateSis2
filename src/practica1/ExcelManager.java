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
            generarXmlErrores();
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
    
    private static void generarXmlErrores() {
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

