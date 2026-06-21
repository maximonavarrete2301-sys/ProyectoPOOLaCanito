package modelo;

//Rodrigo Henriquez

import java.util.ArrayList;
import utilidades.Rut;
import utilidades.IdPersona;
import utilidades.Nombre;
import utilidades.Direccion;

public class Empresa {

    private Rut rut;
    private String nombre;
    private String url;

    private ArrayList<Bus> buses;
    private ArrayList<Tripulante> tripulantes;

    public Empresa(Rut rut, String nombre, String url) {
        this.rut = rut;
        this.nombre = nombre;
        this.url = url;

        buses = new ArrayList<>();
        tripulantes = new ArrayList<>();
    }

    public Rut getRut() {
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

    public void addBus(Bus bus) {
        buses.add(bus);
    }

    public Bus[] getBuses() {
        Bus[] lista = new Bus[buses.size()];

        for (int i = 0; i < buses.size(); i++) {
            lista[i] = buses.get(i);
        }

        return lista;
    }
    public boolean addConductor(IdPersona id, Nombre nom, Direccion dir) {

        if (findConductor(id) != null) {
            return false;
        }

        Conductor conductor = new Conductor(id, nom, dir);
        tripulantes.add(conductor);

        return true;
    }
    public boolean addAuxiliar(IdPersona id, Nombre nom, Direccion dir) {

        if (findAuxiliar(id) != null) {
            return false;
        }

        Auxiliar auxiliar = new Auxiliar(id, nom, dir);
        tripulantes.add(auxiliar);

        return true;
    }

    public Conductor findConductor(IdPersona id) {

        for (Tripulante t : tripulantes) {

            if (t instanceof Conductor) {

                if (t.getIdPersona().equals(id)) {
                    return (Conductor) t;
                }
            }
        }

        return null;
    }

    public Auxiliar findAuxiliar(IdPersona id) {

        for (Tripulante t : tripulantes) {

            if (t instanceof Auxiliar) {

                if (t.getIdPersona().equals(id)) {
                    return (Auxiliar) t;
                }
            }
        }

        return null;
    }

    public Tripulante[] getTripulantes() {

        Tripulante[] lista = new Tripulante[tripulantes.size()];

        for (int i = 0; i < tripulantes.size(); i++) {
            lista[i] = tripulantes.get(i);
        }

        return lista;
    }

    public Venta[] getVentas() {

        ArrayList<Venta> ventasLista = new ArrayList<>();

        for (Bus bus : buses) {

            for (Viaje viaje : bus.getViajes()) {

                for (Venta venta : viaje.getVentas()) {
                    ventasLista.add(venta);
                }
            }
        }

        Venta[] lista = new Venta[ventasLista.size()];

        for (int i = 0; i < ventasLista.size(); i++) {
            lista[i] = ventasLista.get(i);
        }

        return lista;
    }
}