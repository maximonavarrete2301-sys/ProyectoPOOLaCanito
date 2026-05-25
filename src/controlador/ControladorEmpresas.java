//Maximo Navarrete Fernandez
package controlador;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import excepciones.SistemaVentaPasajesException;
import utilidades.*;
import modelo.*;

public class ControladorEmpresas {

    private static ControladorEmpresas instance;

    private ArrayList<Empresa> empresas;
    private ArrayList<Bus> buses;
    private ArrayList<Terminal> terminales;

    private ControladorEmpresas() {

        empresas = new ArrayList<>();
        buses = new ArrayList<>();
        terminales = new ArrayList<>();
    }

    public static ControladorEmpresas getInstance() {

        if (instance == null) {
            instance = new ControladorEmpresas();
        }

        return instance;
    }

    public void createEmpresa(Rut rut, String nombre, String url) {

        if (findEmpresa(rut).isPresent()) {

            throw new SistemaVentaPasajesException("Ya existe empresa con el rut indicado");
        }

        Empresa nueva = new Empresa(rut, nombre, url);
        empresas.add(nueva);
    }

    public void createBus(String patente, String marca, String modelo, int nroAsientos, Rut rutEmpresa) {

        Optional<Empresa> empresaOpt = findEmpresa(rutEmpresa);
        if (empresaOpt.isEmpty()) {

            throw new SistemaVentaPasajesException("No existe empresa con el rut indicado");
        }

        if (findBus(patente).isPresent()) {

            throw new SistemaVentaPasajesException("Ya existe bus con la patente indicada");
        }

        Bus nuevo = new Bus(patente, marca, modelo, nroAsientos, empresaOpt.get());
        buses.add(nuevo);
    }

    public void createTerminal(String nombre, Direccion direccion) {

        if (findTerminal(nombre).isPresent()) {

            throw new SistemaVentaPasajesException("Ya existe terminal con el nombre indicado");
        }

        if (findTerminalPorComuna(direccion.getComuna()).isPresent()) {

            throw new SistemaVentaPasajesException("Ya existe terminal en la comuna indicada");
        }

        Terminal nuevo = new Terminal(nombre, direccion);

        terminales.add(nuevo);
    }

    public void hireConductorForEmpresa(Rut rutEmpresa, IdPersona id, Nombre nom, Direccion dir) {
        Optional<Empresa> empresaOpt = findEmpresa(rutEmpresa);
        if (empresaOpt.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe empresa con el rut indicado");
        }

        boolean ok = empresaOpt.get().addConductor(id, nom, dir);

        if (!ok) {

            throw new SistemaVentaPasajesException("Ya esta contratado conductor/auxiliar con el id dado en la empresa señalada");
        }
    }

    public void hireAuxiliarForEmpresa(Rut rutEmpresa, IdPersona id, Nombre nom, Direccion dir) {
        Optional<Empresa> empresaOpt = findEmpresa(rutEmpresa);

        if (empresaOpt.isEmpty()) {

            throw new SistemaVentaPasajesException("No existe empresa con el rut indicado");
        }

        boolean ok = empresaOpt.get().addAuxiliar(id, nom, dir);

        if (!ok) {

            throw new SistemaVentaPasajesException("Ya esta contratado auxiliar/conductor con el id dado en la empresa señalada");
        }
    }

    public String[][] listEmpresas() {

        String[][] listado = new String[empresas.size()][5];
        for (int i = 0; i < empresas.size(); i++) {

            Empresa e = empresas.get(i);
            listado[i][0] = e.getRut().toString();
            listado[i][1] = e.getNombre();
            listado[i][2] = e.getUrl();
            listado[i][3] = String.valueOf(e.getTripulantes().length);
            listado[i][4] = String.valueOf(e.getBuses().length);
        }
        return listado;
    }

    public String[][] listLlegadasSalidasTerminal(String nombre, LocalDate fecha) {
        Optional<Terminal> terminalOpt = findTerminal(nombre);
        if (terminalOpt.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe terminal con el nombre indicado");
        }
        Terminal terminal = terminalOpt.get();
        Viaje[] salidas = terminal.getSalidas();
        Viaje[] llegadas = terminal.getLlegadas();
        int count = 0;
        for (Viaje s : salidas) {
            if (s.getFecha().equals(fecha)) {
                count++;
            }
        }
        for (Viaje l : llegadas) {
            if (l.getFecha().equals(fecha)) {
                count++;
            }
        }
        String[][] matriz = new String[count][5];
        int index = 0;
        for (Viaje v : salidas) {
            if (v.getFecha().equals(fecha)) {
                matriz[index][0] = "Salida";
                matriz[index][1] = v.getHora().toString();
                matriz[index][2] = v.getBus().getPatente();
                matriz[index][3] = v.getBus().getEmpresa().getNombre();
                matriz[index][4] = String.valueOf(v.getListaPasajeros().length);
                index++;
            }
        }
        for (Viaje v : llegadas) {
            if (v.getFecha().equals(fecha)) {
                matriz[index][0] = "Llegada";
                matriz[index][1] = v.getFechaHoraTermino().toLocalTime().toString();
                matriz[index][2] = v.getBus().getPatente();
                matriz[index][3] = v.getBus().getEmpresa().getNombre();
                matriz[index][4] = String.valueOf(v.getListaPasajeros().length);
                index++;
            }
        }
        return matriz;
    }
    public String[][] listVentasEmpresa(Rut rut) {
        Optional<Empresa> empresaOpt = findEmpresa(rut);
        if (empresaOpt.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe empresa con el rut indicado");
        }
        Venta[] ventasEmpresa = empresaOpt.get().getVentas();
        String[][] listado = new String[ventasEmpresa.length][4];

        for (int i = 0; i < ventasEmpresa.length; i++) {

            Venta v = ventasEmpresa[i];

            listado[i][0] = v.getFecha().toString();
            listado[i][1] = v.getTipo().toString();
            listado[i][2] = v.getTipoPago() != null ? String.valueOf(v.getMontoPagado()) : "0";
            listado[i][3] = v.getTipoPago() != null ? v.getTipoPago() : "Pendiente";
        }

        return listado;
    }

    public Optional<Empresa> findEmpresa(Rut rut) {
        for (Empresa e : empresas) {

            if (e.getRut().equals(rut)) {
                return Optional.of(e);
            }
        }

        return Optional.empty();
    }

    Optional<Terminal> findTerminal(String nombre) {
        for (Terminal t : terminales) {
            if (t.getNombre().equals(nombre)) {
                return Optional.of(t);
            }
        }

        return Optional.empty();
    }

    Optional<Terminal> findTerminalPorComuna(String comuna) {
        for (Terminal t : terminales) {

            if (t.getDireccion().getComuna().equals(comuna)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }
    Optional<Bus> findBus(String patente) {
        for (Bus b : buses) {
            if (b.getPatente().equals(patente)) {
                return Optional.of(b);
            }
        }

        return Optional.empty();
    }

    Optional<Conductor> findConductor(IdPersona id, Rut rutEmpresa) {
        Optional<Empresa> empresaOpt = findEmpresa(rutEmpresa);

        if (empresaOpt.isPresent()) {
            return empresaOpt.get().findConductor(id);
        }

        return Optional.empty();
    }

    Optional<Auxiliar> findAuxiliar(IdPersona id, Rut rutEmpresa) {
        Optional<Empresa> empresaOpt = findEmpresa(rutEmpresa);
        if (empresaOpt.isPresent()) {

            return empresaOpt.get().findAuxiliar(id);
        }

        return Optional.empty();
    }
}