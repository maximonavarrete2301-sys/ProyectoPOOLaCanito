package controlador;

//Victor Diaz, Maximo Navarrete, Rodrigo Henriquez

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import modelo.*;

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
            throw new SistemaVentaPasajesException("Ya existe cliente con el id");
        }
        Cliente c = new Cliente(id, nom, email);
        c.setTelefono(fono);
        clientes.add(c);
    }

    public void createPasajero(IdPersona id, Nombre nom, String fono, Nombre nomContacto, String fonoContacto) {
        if (findPasajero(id).isPresent()) {
            throw new SistemaVentaPasajesException("Ya existe pasajero con el id");
        }
        Pasajero p = new Pasajero(id, nom, nomContacto, fonoContacto);
        p.setTelefono(fono);
        pasajeros.add(p);
    }

    public void createViaje(LocalDate fecha, LocalTime hora, int precio, int duracion,
                            String patenteBus, IdPersona[] ids, String[] comunas) {

        Optional<Bus> busOpt = controladorEmpresas.findBus(patenteBus);

        if (!busOpt.isPresent()) {
            throw new SistemaVentaPasajesException("no existe bus");
        }

        if (findViaje(fecha, hora, patenteBus).isPresent()) {
            throw new SistemaVentaPasajesException(" ya existe viaje");
        }

        Empresa emp = busOpt.get().getEmpresa();

        Tripulante[] trip = new Tripulante[ids.length];

        Optional<Auxiliar> aux = emp.findAuxiliar(ids[0]);
        if (!aux.isPresent()) {
            throw new SistemaVentaPasajesException("no existe auxiliar");
        }
        trip[0] = aux.get();

        for (int i = 1; i < ids.length; i++) {
            Optional<Conductor> cond = emp.findConductor(ids[i]);
            if (!cond.isPresent()) {
                throw new SistemaVentaPasajesException("no existe conductor");
            }
            trip[i] = cond.get();
        }

        Optional<Terminal> t1 = controladorEmpresas.findTerminalPorComuna(comunas[0]);
        Optional<Terminal> t2 = controladorEmpresas.findTerminalPorComuna(comunas[1]);

        if (!t1.isPresent() || !t2.isPresent()) {
            throw new SistemaVentaPasajesException("no existe terminal");
        }

        Viaje v = new Viaje(fecha, hora, precio, duracion, busOpt.get(), trip,
                new Terminal[]{t1.get(), t2.get()});

        viajes.add(v);
    }

    public void iniciaVenta(String idDoc, TipoDocumento tipo, LocalDate fechaViaje,
                            String comSalida, String comLlegada, int nroPasajes, IdPersona idCliente) {

        if (findVenta(idDoc, tipo).isPresent()) {
            throw new SistemaVentaPasajesException("venta ya existe");
        }

        Optional<Cliente> cli = findCliente(idCliente);
        if (!cli.isPresent()) {
            throw new SistemaVentaPasajesException("no existe cliente");
        }

        String[][] viajesDisp = getHorariosDisponibles(fechaViaje, comSalida, comLlegada, nroPasajes);

        if (viajesDisp.length == 0) {
            throw new SistemaVentaPasajesException("no hay viajes disponibles");
        }

        Venta v = new Venta(idDoc, tipo, LocalDate.now(), cli.get());
        ventas.add(v);
    }

    public void vendePasaje(String idDoc, TipoDocumento tipo,
                            LocalDate fecha, LocalTime hora,
                            String patente, int asiento, IdPersona idPasajero) {

        Optional<Venta> venta = findVenta(idDoc, tipo);
        if (!venta.isPresent()) {
            throw new SistemaVentaPasajesException("no existe venta");
        }

        Optional<Pasajero> pas = findPasajero(idPasajero);
        if (!pas.isPresent()) {
            throw new SistemaVentaPasajesException("no existe pasajero");
        }

        Optional<Viaje> via = findViaje(fecha, hora, patente);
        if (!via.isPresent()) {
            throw new SistemaVentaPasajesException("no existe viaje");
        }

        Viaje v = via.get();

        if (!v.existeDisponibilidad(1)) {
            throw new SistemaVentaPasajesException("no hay asientos disponibles");
        }

        venta.get().createPasaje(asiento, v, pas.get());
    }

    public void pagaVenta(String id, TipoDocumento tipo) {
        Optional<Venta> v = findVenta(id, tipo);

        if (!v.isPresent()) {
            throw new SistemaVentaPasajesException("no existe venta");
        }

        if (!v.get().pagaMonto()) {
            throw new SistemaVentaPasajesException("venta ya pagada");
        }
    }

    public void pagaVenta(String id, TipoDocumento tipo, long tarjeta) {
        Optional<Venta> v = findVenta(id, tipo);

        if (!v.isPresent()) {
            throw new SistemaVentaPasajesException("no existe venta");
        }

        if (!v.get().pagaMonto(tarjeta)) {
            throw new SistemaVentaPasajesException("venta ya pagada");
        }
    }

    public Optional<Cliente> findCliente(IdPersona id) {
        for (Cliente c : clientes) {
            if (c.getIdPersona().equals(id)) return Optional.of(c);
        }
        return Optional.empty();
    }

    public Optional<Pasajero> findPasajero(IdPersona id) {
        for (Pasajero p : pasajeros) {
            if (p.getIdPersona().equals(id)) return Optional.of(p);
        }
        return Optional.empty();
    }

    public Optional<Viaje> findViaje(LocalDate f, LocalTime h, String pat) {
        for (Viaje v : viajes) {
            if (v.getFecha().equals(f)
                    && v.getHora().equals(h)
                    && v.getBus().getPatente().equals(pat)) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }

    public Optional<Venta> findVenta(String id, TipoDocumento tipo) {
        for (Venta v : ventas) {
            if (v.getIdDocumento().equals(id) && v.getTipo() == tipo) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }
}
