package modelo;

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

    public Venta(String id, TipoDocumento tipo, LocalDate fec, Cliente cli) {
        this.idDocumento = id;
        this.tipo = tipo;
        this.fecha = fec;
        this.cliente = cli;
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
        Pasaje nuevoPasaje = new Pasaje(asiento, viaje, pasajero, this);
        pasajes.add(nuevoPasaje);
    }

    public Pasaje[] getPasajes() {
        return pasajes.toArray(new Pasaje[0]);
    }

    public int getMonto() {
        int total = 0;
        for (Pasaje p : pasajes) {
            total += p.getViaje().getPrecio();
        }
        return total;
    }

    public boolean pagaMonto() {
        if (this.pago != null) {
            return false;
        }
        this.pago = new PagoEfectivo(getMonto());
        return true;
    }

    public boolean pagaMonto(long nroTarjeta) {
        if (this.pago != null) {
            return false;
        }
        this.pago = new PagoTarjeta(getMonto(), nroTarjeta);
        return true;
    }

    public String getTipoPago() {
        if (pago == null) {
            return null;
        }
        if (pago instanceof PagoEfectivo) {
            return " efectivo ";
        }
        if (pago instanceof PagoTarjeta) {
            return " tarjeta ";
        }
        return "";
    }

    public int getMontoPagado() {
        if (pago == null) {
            return 0;
        }
        return pago.getMonto();
    }
}
