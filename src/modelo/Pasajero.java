//Victor Diaz
package modelo;
import java.io.Serializable;

import utilidades.IdPersona;
import utilidades.Nombre;

public class Pasajero extends Persona implements Serializable {
    private static final long serialVersionUID = 1L;
    private Nombre nomContacto;
    private String fonoContacto;

    public Pasajero(IdPersona id, Nombre nom, Nombre nomContacto, String fonoContacto) {
        super(id, nom);
        this.nomContacto = nomContacto;
        this.fonoContacto = fonoContacto;
    }

    public Nombre getNomContacto() {
        return nomContacto;
    }

    public void setNomContacto(Nombre nom) {
        this.nomContacto = nom;
    }

    public String getFonoContacto() {
        return fonoContacto;
    }

    public void setFonoContacto(String fono) {
        this.fonoContacto = fono;
    }
}
