//Victor Diaz 

package modelo;

import modelo.Venta;

import java.util.Random;

public class Pasaje {
    private long numero;
    private int asiento;
    private Viaje viaje;
    private Pasajero pasajero;
    private Venta venta;

    public Pasaje(int asiento, Viaje viaje, Pasajero pasajero, Venta venta) {
        this.numero = Math.abs(new Random().nextLong());
        this.asiento = asiento;
        this.viaje = viaje;
        this.pasajero = pasajero;
        this.venta = venta;
        if (this.viaje != null) {
            this.viaje.addPasaje(this);
        }
    }

    public long getNumero() {
        return numero;
    }

    public int getAsiento() {
        return asiento;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public Venta getVenta() {
        return venta;
    }

    public String toString() {
        String texto = "==============================================\n";
        texto += "PASAJE N°: " + numero + "\n";
        texto += "Documento: " + venta.getIdDocumento() + " (" + venta.getTipo() + ")\n";
        texto += "Pasajero: " + pasajero.getNombreCompleto() + "\n";
        texto += "Fecha viaje: " + viaje.getFecha() + "\n";
        texto += "Hora salida: " + viaje.getHora() + "\n";
        texto += "Bus: " + viaje.getBus().getPatente() + "\n";
        texto += "Origen: " + viaje.getTerminalSalida().getNombre() + "\n";
        texto += "Destino: " + viaje.getTerminalLlegada().getNombre() + "\n";
        texto += "Asiento: " + asiento + "\n";
        texto += "Precio: $" + viaje.getPrecio() + "\n";
        texto += "==============================================\n";

        return texto;
    }
}
