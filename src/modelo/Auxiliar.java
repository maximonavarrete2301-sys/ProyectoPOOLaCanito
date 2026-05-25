package modelo;

import utilidades.Direccion;
import java.util.ArrayList;

public class Auxiliar extends Tripulante {

    private ArrayList<Viaje> viajes;

    public Auxiliar(utilidades.IdPersona id, utilidades.Nombre nom, Direccion dir) {
        super(id, nom, dir);

        viajes = new ArrayList<>();
    }

    @Override
    public void addViaje(Viaje viaje) {
        viajes.add(viaje);
    }

    @Override
    public int getNroViajes() {
        int cantidad = viajes.size();

        return cantidad;
    }
}