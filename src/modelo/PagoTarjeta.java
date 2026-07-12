package modelo;
import java.io.Serializable;

//Victor Diaz

public class PagoTarjeta extends Pago implements Serializable {
    private static final long serialVersionUID = 1L;

    private long nroTarjeta;

    public PagoTarjeta(int monto, long nroTarjeta){
        super(monto);
        this.nroTarjeta=nroTarjeta;

    }

    public  long getNroTarjeta(){

        return nroTarjeta;
    }



}
