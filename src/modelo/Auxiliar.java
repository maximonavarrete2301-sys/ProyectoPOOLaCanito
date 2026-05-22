package modelo;

//Rodrigo Henriquez
import java.util.ArrayList;
import java.util.List;

public class Auxiliar extends Tripulante {

    private List<Viaje> viajes;

    public Auxiliar(utilidades.IdPersona id, utilidades.Nombre nom, Direccion dir) {
        super(id, nom, dir);
        this.viajes = new ArrayList<>();
    }

    @Override
    public void addViaje(Viaje viaje) {
        this.viajes.add(viaje);
    }

    @Override
    public int getNroViajes() {
        return this.viajes.size();
    }
}