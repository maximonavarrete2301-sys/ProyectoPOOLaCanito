package utilidades;

//Victor Diaz

import modelo.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Venta {

    private String idDocumento;
    private TipoDocumento tipo;
    private LocalDate fecha;
    private Cliente cliente;
    private Pago pago;
    private ArrayList<Pasaje> pasajes;

    public Venta(String idDocumento, TipoDocumento tipo, LocalDate fecha, Cliente cliente) {
        this.idDocumento = idDocumento;
        this.tipo = tipo;
        this.fecha = fecha;
        this.cliente = cliente;
        this.pasajes = new ArrayList<>();

        if (this.cliente != null) {
            this.cliente.addVenta(this);
        }
    }

    public String getIdDocumento() {
        return idDocumento;
    }

    public TipoDocumento getTipo() {
        return tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void createPasaje(int asiento, Viaje viaje, Pasajero pasajero) {
        if (viaje == null || pasajero == null) {
            return;
        }

        Pasaje nuevo = new Pasaje(asiento, viaje, pasajero, this);
        pasajes.add(nuevo);
    }

    public Pasaje[] getPasajes() {
        return pasajes.toArray(new Pasaje[0]);
    }

    public int getMonto() {
        int total = 0;

        for (Pasaje p : pasajes) {
            total = total + p.getViaje().getPrecio();
        }

        return total;
    }

    public boolean pagaMonto() {
        if (pago != null) {
            return false;
        }

        pago = new PagoEfectivo(getMonto());
        return true;
    }

    public boolean pagaMonto(long nroTarjeta) {
        if (pago != null) {
            return false;
        }

        pago = new PagoTarjeta(getMonto(), nroTarjeta);
        return true;
    }

    public String getTipoPago() {
        if (pago == null) {
            return null;
        }

        if (pago instanceof PagoEfectivo) {
            return "efectivo";
        }

        if (pago instanceof PagoTarjeta) {
            return "tarjeta";
        }

        return null;
    }

    public int getMontoPagado() {
        if (pago == null) {
            return 0;
        }

        return pago.getMonto();
    }
<<<<<<< HEAD:src/modelo/Venta.java

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Venta ventaComparada = (Venta) obj;

        if (idDocumento == null) {
            return ventaComparada.idDocumento == null;
        }

        return idDocumento.equals(ventaComparada.idDocumento) && tipo == ventaComparada.tipo;
    }
}



=======
}
>>>>>>> 6bda7e00d4af761d685a3ce315b5af2a9c343d3f:src/utilidades/Venta.java
