package modelo;//MAXIMO NAVARRETE FERNANDEZ
import utilidades.IdPersona;
import utilidades.Nombre;

import java.util.ArrayList;

public class Cliente extends Persona {
    private String email;
    private ArrayList<utilidades.Venta> ventas;

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

    public void addVenta(utilidades.Venta venta) {
        if (venta != null) {
            ventas.add(venta);
        }
    }

    public utilidades.Venta[] getVentas() {
        return ventas.toArray(new utilidades.Venta[0]);
    }
}