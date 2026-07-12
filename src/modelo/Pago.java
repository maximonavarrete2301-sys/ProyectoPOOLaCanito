package modelo;
import java.io.Serializable;

//Victor Diaz

public abstract class Pago implements Serializable {
    private static final long serialVersionUID = 1L;

    private  int monto;

    public  Pago(int monto){

        this.monto=monto;
    }

    public int getMonto() {
        return monto;
    }
}
