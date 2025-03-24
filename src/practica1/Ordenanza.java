package practica1;
// Generated 24-feb-2025 22:39:36 by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * Ordenanza generated by hbm2java
 */
public class Ordenanza  implements java.io.Serializable {


     private int id;
     private String ayuntamiento;
     private String tipoVehiculo;
     private String unidad;
     private double minimoRango;
     private double maximoRango;
     private double importe;
     private Set vehiculoses = new HashSet(0);

    public Ordenanza() {
    }

	
    public Ordenanza(int id, String ayuntamiento, String tipoVehiculo, String unidad, double minimoRango, double maximoRango, double importe) {
        this.id = id;
        this.ayuntamiento = ayuntamiento;
        this.tipoVehiculo = tipoVehiculo;
        this.unidad = unidad;
        this.minimoRango = minimoRango;
        this.maximoRango = maximoRango;
        this.importe = importe;
    }
    public Ordenanza(int id, String ayuntamiento, String tipoVehiculo, String unidad, double minimoRango, double maximoRango, double importe, Set vehiculoses) {
       this.id = id;
       this.ayuntamiento = ayuntamiento;
       this.tipoVehiculo = tipoVehiculo;
       this.unidad = unidad;
       this.minimoRango = minimoRango;
       this.maximoRango = maximoRango;
       this.importe = importe;
       this.vehiculoses = vehiculoses;
    }
   
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public String getAyuntamiento() {
        return this.ayuntamiento;
    }
    
    public void setAyuntamiento(String ayuntamiento) {
        this.ayuntamiento = ayuntamiento;
    }
    public String getTipoVehiculo() {
        return this.tipoVehiculo;
    }
    
    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }
    public String getUnidad() {
        return this.unidad;
    }
    
    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
    public double getMinimoRango() {
        return this.minimoRango;
    }
    
    public void setMinimoRango(double minimoRango) {
        this.minimoRango = minimoRango;
    }
    public double getMaximoRango() {
        return this.maximoRango;
    }
    
    public void setMaximoRango(double maximoRango) {
        this.maximoRango = maximoRango;
    }
    public double getImporte() {
        return this.importe;
    }
    
    public void setImporte(double importe) {
        this.importe = importe;
    }
    public Set getVehiculoses() {
        return this.vehiculoses;
    }
    
    public void setVehiculoses(Set vehiculoses) {
        this.vehiculoses = vehiculoses;
    }




}


