package modelo;
import java.io.Serializable;

//Rodrigo Henriquez

import utilidades.Direccion;
import utilidades.IdPersona;
import utilidades.Nombre;

public abstract class Tripulante extends Persona implements Serializable {
    private static final long serialVersionUID = 1L;

    private Direccion direccion;

    public Tripulante(IdPersona id, Nombre nom, Direccion dir) {

        super(id, nom);

        direccion = dir;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public abstract void addViaje(Viaje viaje);

    public abstract int getNroViajes();
}
