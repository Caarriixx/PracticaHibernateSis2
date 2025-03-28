/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package practica1;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import practica1.HibernateUtil;
import java.util.Scanner;
import org.hibernate.query.Query;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        // Obtener la sesión de Hibernate
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        if (session != null) {
            System.out.println("Conexión a la base de datos establecida correctamente.");
        } else {
            System.out.println("Error en la conexión a la base de datos.");
            return; // Salimos del programa si no hay conexión
        }

        String filePath = "resources/SistemasVehiculos.xlsx";  // Asegúrate de que la ruta sea correcta
        
/*
        // Leer el archivo Excel
        System.out.println("Leyendo el archivo Excel...");
        List<List<String>> datos = ExcelManager.leerExcel(filePath, 0);
        System.out.println("Datos obtenidos: " + datos);

        // Modificar un valor en el archivo Excel
        System.out.println("Modificando el archivo Excel...");
        ExcelManager.modificarExcel(filePath, 0, 2, 3, "Nuevo Valor");
        System.out.println("Modificación realizada con éxito.");
*/  
        System.out.println("Iniciando validación de NIF/NIE...");
        ExcelManager.validarYCorregirNifNie();
        System.out.println("Proceso finalizado. Verifica el archivo Excel y el XML de errores.");
    
        /*
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el NIF del contribuyente: ");
        String nifInput = scanner.nextLine();

        try {
            // Crear consulta con HQL para obtener el contribuyente
            Query<Contribuyente> query = session.createQuery("FROM Contribuyente WHERE nifnie = :nif", Contribuyente.class);
            query.setParameter("nif", nifInput);

            Contribuyente contribuyente = query.uniqueResult();

            if (contribuyente == null) {
                System.out.println("El contribuyente con NIF " + nifInput + " no se encuentra en la base de datos.");
            } else {
                System.out.println("Contribuyente encontrado.");
                System.out.println("Nombre: " + contribuyente.getNombre());
                System.out.println("Apellidos: " + contribuyente.getApellido1() + " " + contribuyente.getApellido2());
                System.out.println("NIF: " + contribuyente.getNifnie());
                System.out.println("Dirección: " + contribuyente.getDireccion());

                session.beginTransaction();
                String hqlUpdate = "UPDATE Recibos SET totalRecibo = 115 WHERE nifContribuyente = :nif";
                int updatedRows = session.createQuery(hqlUpdate)
                                         .setParameter("nif", contribuyente.getNifnie())
                                         .executeUpdate();
                session.getTransaction().commit();
                System.out.println(updatedRows + " recibos actualizados a 115 euros.");

                Query<Double> avgQuery = session.createQuery("SELECT AVG(totalRecibo) FROM Recibos", Double.class);
                Double media = avgQuery.uniqueResult();
                if (media == null) {
                    media = 0.0;
                }
                System.out.println("La media de los importes de los recibos es: " + media);

                session.beginTransaction();
                String hqlDelete = "DELETE FROM Recibos WHERE totalRecibo < :media";
                int deletedRows = session.createQuery(hqlDelete)
                                         .setParameter("media", media)
                                         .executeUpdate();
                session.getTransaction().commit();
                System.out.println(deletedRows + " recibos eliminados por estar debajo de la media.");
                
            }
        } catch (Exception e) {
            System.out.println("Error al procesar la base de datos: " + e.getMessage());
        } finally {
            // Cerrar la sesión y liberar recursos
            session.close();
            scanner.close();
        }*/
    }
}
