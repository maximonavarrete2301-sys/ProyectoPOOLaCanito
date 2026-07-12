//Maximo Navarrete Fernandez
package modelo;
import java.io.Serializable;
import modelo.Venta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
public class Viaje implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate fecha;
    private LocalTime hora;
    private int precio;
    private int duracion;
    private Bus bus;
    private Terminal terminalSalida;
    private Terminal terminalLlegada;

    private ArrayList<Conductor> conductores;
    private Auxiliar auxiliar;
    private ArrayList<Pasaje> pasajes;

    public Viaje(LocalDate fecha, LocalTime hora, int precio, int duracion, Bus bus, Auxiliar auxiliar, Conductor[] conductoresArr, Terminal terminalSalida, Terminal terminalLlegada) {

        this.fecha=fecha;
        this.hora=hora;
        this.precio=precio;
        this.duracion=duracion;
        this.bus= bus;
        this.auxiliar=auxiliar;

        this.terminalSalida= terminalSalida;
        this.terminalLlegada=terminalLlegada;
        this.conductores=new ArrayList<>();
        this.pasajes=new ArrayList<>();

        if (this.auxiliar != null) {
            this.auxiliar.addViaje(this);
        }

        if (conductoresArr != null) {
            for (Conductor conductor : conductoresArr) {
                if (conductor != null) {
                    this.conductores.add(conductor);
                    conductor.addViaje(this);
                }
            }
        }

        if (this.bus != null) {
            this.bus.addViaje(this);
        }

        if (this.terminalSalida != null) {
            this.terminalSalida.addSalida(this);
        }

        if (this.terminalLlegada != null) {
            this.terminalLlegada.addLlegada(this);
        }
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
        this.precio = precio;
    }

    public int getDuracion() {
        return duracion;
    }

    public Bus getBus() {
        return bus;
    }

    public Terminal getTerminalSalida() {
        return terminalSalida;
    }

    public Terminal getTerminalLlegada() {
        return terminalLlegada;
    }

    public LocalDateTime getFechaHoraTermino() {
        return LocalDateTime.of(fecha, hora).plusMinutes(duracion);
    }

    public void addPasaje(Pasaje pasaje) {
        if (pasaje != null && !pasajes.contains(pasaje)) {
            pasajes.add(pasaje);
        }
    }

    public void addTripulante(Tripulante tripulante) {

        if (tripulante instanceof Auxiliar) {

            if (auxiliar == null) {
                auxiliar = (Auxiliar) tripulante;
                auxiliar.addViaje(this);
            }

        } else if (tripulante instanceof Conductor) {

            Conductor conductor = (Conductor) tripulante;

            if (!conductores.contains(conductor)) {
                conductores.add(conductor);
                conductor.addViaje(this);
            }
        }
    }

    public String[] getAsientos() {

        String[] asientos = new String[bus.getNroAsientos()];

        for (int i = 0; i < asientos.length; i++) {
            asientos[i] = String.valueOf(i + 1);
        }

        for (Pasaje pasaje : pasajes) {
            asientos[pasaje.getAsiento() - 1] = "*";
        }

        return asientos;
    }

    public String[][] getListaPasajeros() {

        String[][] lista = new String[pasajes.size()][4];

        for (int i = 0; i < pasajes.size(); i++) {

            Pasajero pasajero = pasajes.get(i).getPasajero();

            lista[i][0] = pasajero.getIdPersona().toString();
            lista[i][1] = pasajero.getNombreCompleto().toString();
            lista[i][2] = pasajero.getNomContacto().toString();
            lista[i][3] = pasajero.getFonoContacto();
        }

        return lista;
    }

    public boolean existeDisponibilidad(int nroAsientos) {
        return getNroAsientosDisponibles() >= nroAsientos;
    }

    public int getNroAsientosDisponibles() {
        return bus.getNroAsientos() - pasajes.size();
    }

    public String[][] getTripulantes() {

        int cantidad = conductores.size();

        if (auxiliar != null) {
            cantidad++;
        }

        String[][] datos = new String[cantidad][3];

        int fila = 0;

        if (auxiliar != null) {

            datos[fila][0] = auxiliar.getIdPersona().toString();
            datos[fila][1] = auxiliar.getNombreCompleto().toString();
            datos[fila][2] = "Auxiliar";

            fila++;
        }

        for (Conductor conductor : conductores) {

            datos[fila][0] = conductor.getIdPersona().toString();
            datos[fila][1] = conductor.getNombreCompleto().toString();
            datos[fila][2] = "Conductor";

            fila++;
        }

        return datos;
    }

    public Venta[] getVentas() {

        ArrayList<Venta> ventasViaje = new ArrayList<>();

        for (Pasaje pasaje : pasajes) {

            Venta venta = pasaje.getVenta();

            if (!ventasViaje.contains(venta)) {
                ventasViaje.add(venta);
            }
        }

        return ventasViaje.toArray(new Venta[0]);
    }
}
