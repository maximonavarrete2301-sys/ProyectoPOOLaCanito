//MAXIMO NAVARRETE FERNANDEZ
package modelo;
import java.io.Serializable;
import utilidades.IdPersona;
import utilidades.Nombre;
import modelo.Venta;

import java.util.ArrayList;

public class Cliente extends Persona implements Serializable {
    private static final long serialVersionUID = 1L;
    private String email;
    private ArrayList<Venta> ventas;

    public Cliente(IdPersona id, Nombre nom, String email) {
        super(id, nom);
        this.email = email;
        this.ventas = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addVenta(Venta venta) {
        if (venta != null) {
            ventas.add(venta);
        }
    }

    public Venta[] getVentas() {
        return ventas.toArray(new Venta[0]);
    }
}
