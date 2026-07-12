package utilidades;
import java.io.Serializable;

//Victor Diaz

public class Direccion implements Serializable {
    private static final long serialVersionUID = 1L;

    private String calle;
    private String numero;
    private String comuna;

    public Direccion(String calle, String numero, String comuna) {
        this.calle = calle;
        this.numero = numero;
        this.comuna = comuna;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String toString() {
        return calle + " " + numero + ", " + comuna;
    }
}
