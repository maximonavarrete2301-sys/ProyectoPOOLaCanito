//MAXIMO NAVARRETE FERNANDEZ
import java.util.ArrayList;

public class Cliente extends Persona {
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