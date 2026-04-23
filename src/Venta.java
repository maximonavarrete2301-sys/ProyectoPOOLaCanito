import java.time.LocalDate;

public class Venta extends SistemaVentaPasajes {
    private String idDocumento;
    private TipoDocumento tipo;
    private LocalDate fecha;

    public Venta(String id, TipoDocumento tipo, LocalDate fec, Cliente cli) {
        this.idDocumento = id;
        this.tipo = tipo;
        this.fecha = fec;
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
        return null;
    }

    public void createPasaje(int asiento, Viaje viaje, Pasajero pasajero) {
    }

    public Pasaje[] getPasajes() {
        return null;
    }

    public int getMonto() {
        return 0;
    }
}