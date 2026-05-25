package modelo;

import java.util.ArrayList;

public class Bus {
    private String patente;
    private String marca;
    private String modelo;
    private int nroAsientos;
    private Empresa empresa;
    private ArrayList<Viaje> viajes;

    public Bus(String patente, int nroAsientos, Empresa empresa) {
        this.patente = patente;
        this.nroAsientos = nroAsientos;
        this.empresa = empresa;
        this.viajes = new ArrayList<>();
        if (this.empresa != null) {
            this.empresa.addBus(this);
        }
    }

    public String getPatente() {
        return patente;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getNroAsientos() {
        return nroAsientos;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void addViaje(Viaje viaje) {
        if (viaje != null) {
            viajes.add(viaje);
        }
    }

    public Viaje[] getViajes() {
        return viajes.toArray(new Viaje[0]);
    }
}