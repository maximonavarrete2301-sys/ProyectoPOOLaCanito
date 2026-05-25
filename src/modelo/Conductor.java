package modelo;

//Rodrigo Henriquez

import java.util.ArrayList;
import utilidades.Direccion;

public class Conductor extends Tripulante {

    private ArrayList<Viaje> viajes;

    public Conductor(utilidades.IdPersona id, utilidades.Nombre nom, Direccion dir) {
        super(id, nom, dir);

        viajes = new ArrayList<>();
    }

    @Override
    public void addViaje(Viaje viaje) {
        viajes.add(viaje);
    }

    @Override
    public int getNroViajes() {

        int total = viajes.size();

        return total;
    }
}
