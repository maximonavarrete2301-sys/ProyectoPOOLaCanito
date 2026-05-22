package modelo;//Rodrigo Henriquez
import java.util.ArrayList;

public class Empresa {

    private utilidades.Rut rut;
    private String nombre;
    private String url;

    private ArrayList<modelo.Bus> buses;
    private ArrayList<modelo.Conductor> conductores;
    private ArrayList<modelo.Auxiliar> auxiliares;

    public Empresa(utilidades.Rut rut, String nombre) {
        this.rut = rut;
        this.nombre = nombre;
        this.url = "";

        this.buses = new ArrayList<>();
        this.conductores = new ArrayList<>();
        this.auxiliares = new ArrayList<>();
    }

    public utilidades.Rut getRut() {
        return rut;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addBus(modelo.Bus bus) {
        if (bus != null) {
            buses.add(bus);
        }
    }

    public modelo.Bus[] getBuses() {
        return buses.toArray(new modelo.Bus[0]);
    }

    public boolean addConductor(utilidades.IdPersona id, utilidades.Nombre nom, utilidades.Direccion dir) {

        for (modelo.Conductor c : conductores) {
            if (c.getIdPersona().equals(id)) {
                return false;
            }
        }

        for (modelo.Auxiliar a : auxiliares) {
            if (a.getIdPersona().equals(id)) {
                return false;
            }
        }

        modelo.Conductor conductor = new modelo.Conductor(id, nom, dir);
        conductores.add(conductor);

        return true;
    }

    public boolean addAuxiliar(utilidades.IdPersona id, utilidades.Nombre nom, utilidades.Direccion dir) {

        // Verifica que no exista un tripulante con el mismo id
        for (modelo.Conductor c : conductores) {
            if (c.getIdPersona().equals(id)) {
                return false;
            }
        }

        for (modelo.Auxiliar a : auxiliares) {
            if (a.getIdPersona().equals(id)) {
                return false;
            }
        }

        modelo.Auxiliar auxiliar = new modelo.Auxiliar(id, nom, dir);
        auxiliares.add(auxiliar);

        return true;
    }

    public modelo.Tripulante[] getTripulantes() {

        modelo.Tripulante[] tripulantes =
                new modelo.Tripulante[conductores.size() + auxiliares.size()];

        int i = 0;

        for (modelo.Auxiliar a : auxiliares) {
            tripulantes[i] = a;
            i++;
        }

        for (modelo.Conductor c : conductores) {
            tripulantes[i] = c;
            i++;
        }

        return tripulantes;
    }

    public utilidades.Venta[] getVentas() {

        ArrayList<utilidades.Venta> ventas = new ArrayList<>();

        for (modelo.Bus bus : buses) {

            modelo.Viaje[] viajes = bus.getViajes();

            for (modelo.Viaje viaje : viajes) {

                utilidades.Venta[] ventasViaje = viaje.getVentas();

                for (utilidades.Venta venta : ventasViaje) {

                    if (!ventas.contains(venta)) {
                        ventas.add(venta);
                    }
                }
            }
        }

        return ventas.toArray(new utilidades.Venta[0]);
    }
}
