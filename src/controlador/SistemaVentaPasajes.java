package controlador;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import utilidades.*;
import modelo.*;
import excepciones.*;

public class SistemaVentaPasajes {

    private static SistemaVentaPasajes instancia;

    private ArrayList<Cliente> clientes;
    private ArrayList<Pasajero> pasajeros;
    private ArrayList<Viaje> viajes;
    private ArrayList<Venta> ventas;

    private ControladorEmpresas controladorEmpresas;

    private SistemaVentaPasajes() {
        clientes = new ArrayList<>();
        pasajeros = new ArrayList<>();
        viajes = new ArrayList<>();
        ventas = new ArrayList<>();

        controladorEmpresas = ControladorEmpresas.getInstancia();
    }

    public static SistemaVentaPasajes getInstancia() {
        if (instancia == null) {
            instancia = new SistemaVentaPasajes();
        }
        return instancia;
    }

    public void createCliente(IdPersona id, Nombre nom, String fono, String email) {
        if (findCliente(id).isPresent()) {
            throw new SistemaVentaPasajesException(" ya  existe cliente con el id ingresado ");
        }

        Cliente nuevo = new Cliente(id, nom, email);
        nuevo.setTelefono(fono);
        clientes.add(nuevo);
    }

    public void createPasajero(IdPersona id, Nombre nom, String fono, Nombre nomContacto, String fonoContacto) {
        if (findPasajero(id).isPresent()) {
            throw new SistemaVentaPasajesException("ya existe pasajero con el id ingresado ");
        }

        Pasajero nuevo = new Pasajero(id, nom, nomContacto, fonoContacto);
        nuevo.setTelefono(fono);
        pasajeros.add(nuevo);
    }

    public void createViaje(LocalDate fecha, LocalTime hora, int precio, int duracion, String patenteBus, IdPersona[] idTripulantes, String[] nomComunas) {
        Optional<Bus> busOpt = controladorEmpresas.findBus(patenteBus);

        if (busOpt.isEmpty()) {
            throw new SistemaVentaPasajesException(" no  existe bus con esa patente  ");
        }

        if (findViaje(fecha, hora, patenteBus).isPresent()) {
            throw new SistemaVentaPasajesException("ya existe viaje con fecha  hora y patente de bus ");
        }

        Empresa empresa = busOpt.get().getEmpresa();

        Optional<Auxiliar> auxOpt = empresa.findAuxiliar(idTripulantes[0]);
        if (auxOpt.isEmpty()) {
            throw new SistemaVentaPasajesException(" no existe auxiliar con el id y rut en la empresa ");
        }

        Tripulante[] tripulantesObjs = new Tripulante[idTripulantes.length];
        tripulantesObjs[0] = auxOpt.get();

        for (int i = 1; i < idTripulantes.length; i++) {
            Optional<Conductor> condOpt = empresa.findConductor(idTripulantes[i]);
            if (condOpt.isEmpty()) {
                throw new SistemaVentaPasajesException("no existe conductor con el id y rut en la empresa");
            }
            tripulantesObjs[i] = condOpt.get();
        }

        Optional<Terminal> termSalida = controladorEmpresas.findTerminalPorComuna(nomComunas[0]);
        if (termSalida.isEmpty()) {
            throw new SistemaVentaPasajesException("no existe terminal de salida en esa comuna ");
        }

        Optional<Terminal> termLlegada = controladorEmpresas.findTerminalPorComuna(nomComunas[1]);
        if (termLlegada.isEmpty()) {
            throw new SistemaVentaPasajesException(" no existe terminal de llegada en esa comuna ");
        }

        Terminal[] terminalesObjs = new Terminal[]{termSalida.get(), termLlegada.get()};

        Viaje nuevo = new Viaje(fecha, hora, precio, duracion, busOpt.get(), tripulantesObjs, terminalesObjs);
        viajes.add(nuevo);
    }

    public void iniciaVenta(String idDoc, TipoDocumento tipo, LocalDate fechaViaje, String comSalida, String comLlegada, int nroPasajes, IdPersona idCliente) {
        if (findVenta(idDoc, tipo).isPresent()) {
            throw new SistemaVentaPasajesException(" ya existe venta con el id y tipo de documento ");
        }

        Optional<Cliente> clienteOpt = findCliente(idCliente);
        if (clienteOpt.isEmpty()) {
            throw new SistemaVentaPasajesException(" no existe cliente con ese id ");
        }

        String[][] disponibles = getHorariosDisponibles(fechaViaje, comSalida, comLlegada, nroPasajes);

        if (disponibles.length == 0) {
            throw new SistemaVentaPasajesException(" no existen viajes disponibles en la fecha y con terminales en las comunas de salida y llegada ");
        }

        Venta nueva = new Venta(idDoc, tipo, LocalDate.now(), clienteOpt.get());
        ventas.add(nueva);
    }

    public String[][] getHorariosDisponibles(LocalDate fechaViaje, String comunaSalida, String comunaLlegada, int nroPasajes) {
        ArrayList<Viaje> filtrados = new ArrayList<>();

        for (Viaje v : viajes) {
            if (v.getFecha().equals(fechaViaje)
                    && v.getTerminalSalida().getDireccion().getComuna().equals(comunaSalida)
                    && v.getTerminalLlegada().getDireccion().getComuna().equals(comunaLlegada)
                    && v.existeDisponibilidad(nroPasajes)) {
                filtrados.add(v);
            }
        }

        String[][] datos = new String[filtrados.size()][4];

        for (int i = 0; i < filtrados.size(); i++) {
            Viaje v = filtrados.get(i);
            datos[i][0] = v.getBus().getPatente();
            datos[i][1] = v.getHora().toString();
            datos[i][2] = String.valueOf(v.getPrecio());
            datos[i][3] = String.valueOf(v.getNroAsientosDisponibles());
        }

        return datos;
    }

    public String[][] listAsientosDeViaje(LocalDate fecha, LocalTime hora, String patenteBus) {
        Optional<Viaje> viajeOpt = findViaje(fecha, hora, patenteBus);

        if (viajeOpt.isEmpty()) {
            return new String[0][0];
        }

        return viajeOpt.get().getAsientos();
    }

    public Optional<String> getNombrePasajero(IdPersona idPasajero) {
        Optional<Pasajero> pasajero = findPasajero(idPasajero);

        if (pasajero.isPresent()) {
            return Optional.of(pasajero.get().getNombreCompleto().toString());
        }

        return Optional.empty();
    }

    public Optional<Integer> getMontoVenta(String idDocumento, TipoDocumento tipo) {
        Optional<Venta> ventaOpt = findVenta(idDocumento, tipo);
        if (ventaOpt.isPresent()) {
            return Optional.of(ventaOpt.get().getMonto());
        }
        return Optional.empty();
    }

    public void vendePasaje(String idDoc, TipoDocumento tipo, LocalDate fechaViaje, LocalTime hora, String patenteBus, int asiento, IdPersona idPasajero) {
        Optional<Venta> ventaOpt = findVenta(idDoc, tipo);
        if (ventaOpt.isEmpty()) {
            throw new SistemaVentaPasajesException("  no existe venta con el id y tipo de documento ");
        }

        Optional<Pasajero> pasajeroOpt = findPasajero(idPasajero);
        if (pasajeroOpt.isEmpty()) {
            throw new SistemaVentaPasajesException(" no existe pasajero con ese id ");
        }

        Optional<Viaje> viajeOpt = findViaje(fechaViaje, hora, patenteBus);
        if (viajeOpt.isEmpty()) {
            throw new SistemaVentaPasajesException(" no existe viaje con la fecha hora y patente del bus ");
        }

        ventaOpt.get().createPasaje(asiento, viajeOpt.get(), pasajeroOpt.get());
    }

    public void pagaVenta(String idDocumento, TipoDocumento tipo) {
        Optional<Venta> ventaOpt = findVenta(idDocumento, tipo);

        if (ventaOpt.isEmpty()) {
            throw new SistemaVentaPasajesException(" no existe venta con el id y tipo de documento ");
        }

        boolean pagado = ventaOpt.get().pagaMonto();

        if (!pagado) {
            throw new SistemaVentaPasajesException("  la venta ya fue pagada");
        }
    }

    public void pagaVenta(String idDocumento, TipoDocumento tipo, long nroTarjeta) {
        Optional<Venta> ventaOpt = findVenta(idDocumento, tipo);

        if (ventaOpt.isEmpty()) {
            throw new SistemaVentaPasajesException("no existe venta con el id y tipo de documento ");
        }

        boolean pagado = ventaOpt.get().pagaMonto(nroTarjeta);

        if (!pagado) {
            throw new SistemaVentaPasajesException(" la venta ya fue pagada");
        }
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
        String[][] lista = new String[viajes.size()][7];
        for (int i = 0; i < viajes.size(); i++) {
            Viaje v = viajes.get(i);
            lista[i][0] = v.getFecha().toString();
            lista[i][1] = v.getHora().toString();
            lista[i][2] = v.getFechaHoraTermino().toLocalTime().toString();
            lista[i][3] = String.valueOf(v.getPrecio());
            lista[i][4] = String.valueOf(v.getNroAsientosDisponibles());
            lista[i][5] = v.getBus().getPatente();
            lista[i][6] = v.getTerminalSalida().getDireccion().getComuna();
        }
        return lista;
    }

    public String[][] listPasajerosViaje(LocalDate fecha, LocalTime hora, String patenteBus) {
        Optional<Viaje> viajeOpt = findViaje(fecha, hora, patenteBus);

        if (viajeOpt.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe viaje con la fecha hora y patente del bus ");
        }

        return viajeOpt.get().getListaPasajeros();
    }

    public Optional<Cliente> findCliente(IdPersona id) {
        for (Cliente c : clientes) {
            if (c.getIdPersona().equals(id)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    public Optional<Pasajero> findPasajero(IdPersona idPersona) {
        for (Pasajero p : pasajeros) {
            if (p.getIdPersona().equals(idPersona)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    public Optional<Viaje> findViaje(LocalDate fecha, LocalTime hora, String patenteBus) {
        for (Viaje v : viajes) {
            if (v.getFecha().equals(fecha)
                    && v.getHora().equals(hora)
                    && v.getBus().getPatente().equals(patenteBus)) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }

    public Optional<Venta> findVenta(String idDocumento, TipoDocumento tipoDocumento) {
        for (Venta v : ventas) {
            if (v.getIdDocumento().equals(idDocumento)
                    && v.getTipo() == tipoDocumento) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }
}