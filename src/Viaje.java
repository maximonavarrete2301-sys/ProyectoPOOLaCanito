import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
public class Viaje {
    private LocalDate fecha;
    private LocalTime hora;
    private int precio;
    private Bus bus;
    private ArrayList<Pasaje> pasajes;

    public Viaje(LocalDate fecha, LocalTime hora, int precio, Bus bus) {
        this.fecha=fecha;
        this.hora=hora;
        this.precio=precio;
        this.bus=bus;
        this.pasajes=new ArrayList<>();
        bus.addViaje(this);
    }

    public LocalDate getFecha() {
        return fecha;
    }
    public LocalTime getHora() {
        return hora;
    }
    public int getPrecio() {
        return precio;
    }
    public void setPrecio(int precio) {
        this.precio=precio;
    }
    public Bus getBus() {
        return bus;
    }
    public String[][] getAsientos() {
        String[][] asientos = new String[bus.getNroAsientos()][2];

        for (int i = 0; i < bus.getNroAsientos(); i++) {
            asientos[i][0] = String.valueOf(i + 1);
            asientos[i][1] = "Libre";
        }
        for (Pasaje pasaje : pasajes) {
            int numeroAsiento = pasaje.getAsiento();
            asientos[numeroAsiento - 1][1] = "Ocupado";
        }
        return asientos;
    }
    public void addPasaje(Pasaje pasaje) {
        pasajes.add(pasaje);
    }

    public String[][] getListaPasajeros() {
        String[][] lista = new String[pasajes.size()][4];

        for (int i = 0; i < pasajes.size(); i++) {
            Pasajero pasajero = pasajes.get(i).getPasajero();
            lista[i][0] = pasajero.getIdPersona().toString();
            lista[i][1] = pasajero.getNombre().toString();
            lista[i][2] = pasajero.getNomContacto();
            lista[i][3] = pasajero.getFonoContacto();
        }

        return lista;
    }
    public boolean existeDisponibilidad() {
        return pasajes.size() < bus.getNroAsientos();
    }
    public int getNroAsientosDisponibles() {
        return bus.getNroAsientos() - pasajes.size();
    }
}
