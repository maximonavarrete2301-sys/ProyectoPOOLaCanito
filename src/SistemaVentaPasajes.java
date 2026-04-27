//Victor Diaz, Maximo Navarrete y Rodrigo Henriquez

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class SistemaVentaPasajes {
    private ArrayList<Cliente> clientes;
    private ArrayList<Pasajero> pasajeros;
    private ArrayList<Bus> buses;
    private ArrayList<Viaje> viajes;
    private ArrayList<Venta> ventas;

    public SistemaVentaPasajes() {
        this.clientes = new ArrayList<>();
        this.pasajeros = new ArrayList<>();
        this.buses = new ArrayList<>();
        this.viajes = new ArrayList<>();
        this.ventas = new ArrayList<>();
    }

    public boolean createCliente(IdPersona id, Nombre nom, String fono, String email) {

        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getIdPersona().equals(id)) {
                return false;
            }
        }


        Cliente nuevo = new Cliente(id, nom, email);
        nuevo.setTelefono(fono);
        clientes.add(nuevo);
        return true;
    }

    public boolean createPasajero(IdPersona id, Nombre nom, String fono, Nombre nomContacto, String fonoContacto) {
        for (int i = 0; i < pasajeros.size(); i++) {
            if (pasajeros.get(i).getIdPersona().equals(id)) {
                return false;
            }
        }

        Pasajero nuevo = new Pasajero(id, nom, nomContacto, fonoContacto);
        nuevo.setTelefono(fono);
        pasajeros.add(nuevo);
        return true;
    }

    public boolean createBus(String patente, String marca, String modelo, int nroAsientos) {
        for (int i = 0; i < buses.size(); i++) {
            if (buses.get(i).getPatente().equals(patente)) {
                return false;
            }
        }

        Bus nuevo = new Bus(patente, nroAsientos);
        nuevo.setMarca(marca);
        nuevo.setModelo(modelo);
        buses.add(nuevo);
        return true;
    }

    public boolean createViaje(LocalDate fecha, LocalTime hora, int precio, String patBus) {

        Bus busEncontrado = null;
        for (int i = 0; i < buses.size(); i++) {
            if (buses.get(i).getPatente().equals(patBus)) {
                busEncontrado = buses.get(i);
                break;
            }
        }


        boolean viajeRepetido = false;
        for (int i = 0; i < viajes.size(); i++) {
            if (viajes.get(i).getFecha().equals(fecha) &&
                    viajes.get(i).getHora().equals(hora) &&
                    viajes.get(i).getBus().getPatente().equals(patBus)) {
                viajeRepetido = true;
                break;
            }
        }

        if (busEncontrado != null && !viajeRepetido) {
            Viaje nuevo = new Viaje(fecha, hora, precio, busEncontrado);
            viajes.add(nuevo);
            return true;
        }
        return false;
    }

    public boolean iniciaVenta(String idDoc, TipoDocumento tipo, LocalDate fechaVenta, IdPersona idCliente) {

        Cliente clienteEncontrado = null;
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getIdPersona().equals(idCliente)) {
                clienteEncontrado = clientes.get(i);
                break;
            }
        }


        boolean ventaRepetida = false;
        for (int i = 0; i < ventas.size(); i++) {
            if (ventas.get(i).getIdDocumento().equals(idDoc) && ventas.get(i).getTipo() == tipo) {
                ventaRepetida = true;
                break;
            }
        }

        if (clienteEncontrado != null && !ventaRepetida) {
            Venta nueva = new Venta(idDoc, tipo, fechaVenta, clienteEncontrado);
            ventas.add(nueva);
            return true;
        }
        return false;
    }

    public String[][] getHorariosDisponibles(LocalDate fechaViaje) {
        ArrayList<Viaje> filtrados = new ArrayList<>();
        for (int i = 0; i < viajes.size(); i++) {
            if (viajes.get(i).getFecha().equals(fechaViaje)) {
                filtrados.add(viajes.get(i));
            }
        }

        String[][] resultado = new String[filtrados.size()][4];
        for (int i = 0; i < filtrados.size(); i++) {
            Viaje v = filtrados.get(i);
            resultado[i][0] = v.getBus().getPatente();
            resultado[i][1] = v.getHora().toString();
            resultado[i][2] = String.valueOf(v.getPrecio());
            resultado[i][3] = String.valueOf(v.getNroAsientosDisponibles());
        }
        return resultado;
    }

    public String[] listAsientosDeViaje(LocalDate fecha, LocalTime hora, String patBus) {
        Viaje viajeEncontrado = null;
        for (int i = 0; i < viajes.size(); i++) {
            if (viajes.get(i).getFecha().equals(fecha) &&
                    viajes.get(i).getHora().equals(hora) &&
                    viajes.get(i).getBus().getPatente().equals(patBus)) {
                viajeEncontrado = viajes.get(i);
                break;
            }
        }

        if (viajeEncontrado == null) {
            return new String[0];
        }

        String[][] matriz = viajeEncontrado.getAsientos();
        String[] lista = new String[matriz.length];
        for (int i = 0; i < matriz.length; i++) {
            lista[i] = matriz[i][1];
        }
        return lista;
    }

    public int getMontoVenta(String idDocumento, TipoDocumento tipo) {
        for (int i = 0; i < ventas.size(); i++) {
            if (ventas.get(i).getIdDocumento().equals(idDocumento) && ventas.get(i).getTipo() == tipo) {
                return ventas.get(i).getMonto();
            }
        }
        return 0;
    }

    public String getNombrePasajero(IdPersona idPasajero) {
        for (int i = 0; i < pasajeros.size(); i++) {
            if (pasajeros.get(i).getIdPersona().equals(idPasajero)) {
                return pasajeros.get(i).getNombreCompleto().toString();
            }
        }
        return null;
    }

    public boolean vendePasaje(String idDoc, LocalDate fecha, LocalTime hora, String patBus, int asiento, IdPersona idPasajero) {

        Venta ventaEncontrada = null;
        for (int i = 0; i < ventas.size(); i++) {
            if (ventas.get(i).getIdDocumento().equals(idDoc)) {
                ventaEncontrada = ventas.get(i);
                break;
            }
        }


        Viaje viajeEncontrado = null;
        for (int i = 0; i < viajes.size(); i++) {
            if (viajes.get(i).getFecha().equals(fecha) &&
                    viajes.get(i).getHora().equals(hora) &&
                    viajes.get(i).getBus().getPatente().equals(patBus)) {
                viajeEncontrado = viajes.get(i);
                break;
            }
        }


        Pasajero pasajeroEncontrado = null;
        for (int i = 0; i < pasajeros.size(); i++) {
            if (pasajeros.get(i).getIdPersona().equals(idPasajero)) {
                pasajeroEncontrado = pasajeros.get(i);
                break;
            }
        }


        if (ventaEncontrada != null && viajeEncontrado != null && pasajeroEncontrado != null) {
            ventaEncontrada.createPasaje(asiento, viajeEncontrado, pasajeroEncontrado);
            return true;
        }
        return false;
    }

    public String[][] listVentas() {
        String[][] lista = new String[ventas.size()][7];
        for (int i = 0; i < ventas.size(); i++) {
            Venta v = ventas.get(i);
            lista[i][0] = v.getIdDocumento();
            lista[i][1] = v.getTipo().toString();
            lista[i][2] = v.getFecha().toString();
            lista[i][3] = v.getCliente().getIdPersona().toString();
            lista[i][4] = v.getCliente().getNombreCompleto().toString();
            lista[i][5] = String.valueOf(v.getPasajes().length);
            lista[i][6] = String.valueOf(v.getMonto());
        }
        return lista;
    }

    public String[][] listViajes() {
        String[][] lista = new String[viajes.size()][5];
        for (int i = 0; i < viajes.size(); i++) {
            Viaje v = viajes.get(i);
            lista[i][0] = v.getFecha().toString();
            lista[i][1] = v.getHora().toString();
            lista[i][2] = String.valueOf(v.getPrecio());
            lista[i][3] = String.valueOf(v.getNroAsientosDisponibles());
            lista[i][4] = v.getBus().getPatente();
        }
        return lista;
    }

    public String[][] listPasajeros(LocalDate fecha, LocalTime hora, String patBus) {
        Viaje viajeEncontrado = null;
        for (int i = 0; i < viajes.size(); i++) {
            if (viajes.get(i).getFecha().equals(fecha) &&
                    viajes.get(i).getHora().equals(hora) &&
                    viajes.get(i).getBus().getPatente().equals(patBus)) {
                viajeEncontrado = viajes.get(i);
                break;
            }
        }

        if (viajeEncontrado != null) {
            return viajeEncontrado.getListaPasajeros();
        }
        return new String[0][0];
    }
}