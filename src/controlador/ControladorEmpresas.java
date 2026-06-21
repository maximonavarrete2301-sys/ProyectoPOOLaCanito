//Maximo Navarrete Fernandez
package controlador;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import excepciones.SVPException;
import utilidades.*;
import modelo.*;

public class ControladorEmpresas implements Serializable {
    private static final long serialVersionUID = 1L;
    private static ControladorEmpresas instance;

    private List<Empresa> empresas;
    private List<Bus> buses;
    private List<Terminal> terminales;

    private ControladorEmpresas() {
        empresas = new LinkedList<>();
        buses = new LinkedList<>();
        terminales = new LinkedList<>();
    }

    public static ControladorEmpresas getInstance() {
        if (instance == null) {
            instance = new ControladorEmpresas();
        }
        return instance;
    }

    public void createEmpresa(Rut rut, String nombre, String url) {
        if (findEmpresa(rut).isPresent()) {
            throw new SVPException("Ya existe empresa con el rut indicado");
        }
        Empresa nueva = new Empresa(rut, nombre);
        nueva.setUrl(url);
        empresas.add(nueva);
    }

    public void createBus(String patente, String marca, String modelo, int nroAsientos, Rut rutEmpresa) {
        Optional<Empresa> empresaOpt = findEmpresa(rutEmpresa);
        if (empresaOpt.isEmpty()) {
            throw new SVPException("No existe empresa con el rut indicado");
        }
        if (findBus(patente).isPresent()) {
            throw new SVPException("Ya existe bus con la patente indicada");
        }
        Bus nuevo = new Bus(patente, nroAsientos, empresaOpt.get());
        nuevo.setMarca(marca);
        nuevo.setModelo(modelo);
        buses.add(nuevo);
        empresaOpt.get().addBus(nuevo);
    }

    public void createTerminal(String nombre, Direccion direccion) {
        if (findTerminal(nombre).isPresent()) {
            throw new SVPException("Ya existe terminal con el nombre indicado");
        }
        if (findTerminalPorComuna(direccion.getComuna()).isPresent()) {
            throw new SVPException("Ya existe terminal en la comuna indicada");
        }
        Terminal nuevo = new Terminal(nombre, direccion);
        terminales.add(nuevo);
    }

    public void hireConductorForEmpresa(Rut rutEmpresa, IdPersona id, Nombre nom, Direccion dir) {
        Optional<Empresa> empresaOpt = findEmpresa(rutEmpresa);
        if (empresaOpt.isEmpty()) {
            throw new SVPException("No existe empresa con el rut indicado");
        }
        boolean ok = empresaOpt.get().addConductor(id, nom, dir);
        if (!ok) {
            throw new SVPException("Ya está contratado el conductor con el id indicado en la empresa señalada");
        }
    }

    public void hireAuxiliarForEmpresa(Rut rutEmpresa, IdPersona id, Nombre nom, Direccion dir) {
        Optional<Empresa> empresaOpt = findEmpresa(rutEmpresa);
        if (empresaOpt.isEmpty()) {
            throw new SVPException("No existe empresa con el rut indicado");
        }
        boolean ok = empresaOpt.get().addAuxiliar(id, nom, dir);
        if (!ok) {
            throw new SVPException("Ya está contratado el auxiliar con el id indicado en la empresa señalada");
        }
    }

    public String[][] listEmpresas() {
        return empresas.stream()
                .map(e -> new String[]{
                        e.getRut().toString(),
                        e.getNombre(),
                        e.getUrl(),
                        String.valueOf(e.getTripulantes().length),
                        String.valueOf(e.getBuses().length)
                })
                .toArray(String[][]::new);
    }

    public String[][] listLlegadasSalidasTerminal(String nombre, LocalDate fecha) {
        Optional<Terminal> terminalOpt = findTerminal(nombre);
        if (terminalOpt.isEmpty()) {
            throw new SVPException("No existe terminal con el nombre indicado");
        }
        Terminal terminal = terminalOpt.get();
        List<String[]> listaResultados = new LinkedList<>();

        Arrays.stream(terminal.getSalidas())
                .filter(v -> v.getFecha().equals(fecha))
                .forEach(v -> listaResultados.add(new String[]{
                        "Salida",
                        v.getHora().toString(),
                        v.getBus().getPatente(),
                        v.getBus().getEmpresa().getNombre(),
                        String.valueOf(v.getListaPasajeros().length)
                }));

        Arrays.stream(terminal.getLlegadas())
                .filter(v -> v.getFecha().equals(fecha))
                .forEach(v -> listaResultados.add(new String[]{
                        "Llegada",
                        v.getFechaHoraTermino().toLocalTime().toString(),
                        v.getBus().getPatente(),
                        v.getBus().getEmpresa().getNombre(),
                        String.valueOf(v.getListaPasajeros().length)
                }));

        return listaResultados.toArray(new String[0][]);
    }

    public String[][] listVentasEmpresa(Rut rut) {
        Optional<Empresa> empresaOpt = findEmpresa(rut);
        if (empresaOpt.isEmpty()) {
            throw new SVPException("No existe empresa con el rut indicado");
        }
        Venta[] ventasEmpresa = empresaOpt.get().getVentas();

        return Arrays.stream(ventasEmpresa)
                .map(v -> new String[]{
                        v.getFecha().toString(),
                        v.getTipo().toString(),
                        v.getTipoPago() != null ? String.valueOf(v.getMontoPagado()) : "0",
                        v.getTipoPago() != null ? v.getTipoPago() : "Pendiente"
                })
                .toArray(String[][]::new);
    }

    protected Optional<Empresa> findEmpresa(Rut rut) {
        return empresas.stream()
                .filter(e -> e.getRut().equals(rut))
                .findFirst();
    }

    protected Optional<Terminal> findTerminal(String nombre) {
        return terminales.stream()
                .filter(t -> t.getNombre().equals(nombre))
                .findFirst();
    }

    protected Optional<Terminal> findTerminalPorComuna(String comuna) {
        return terminales.stream()
                .filter(t -> t.getDireccion().getComuna().equals(comuna))
                .findFirst();
    }

    protected Optional<Bus> findBus(String patente) {
        return buses.stream()
                .filter(b -> b.getPatente().equals(patente))
                .findFirst();
    }

    protected Optional<Conductor> findConductor(IdPersona id, Rut rutEmpresa) {
        return findEmpresa(rutEmpresa)
                .flatMap(e -> Arrays.stream(e.getTripulantes())
                        .filter(t -> t instanceof Conductor && t.getIdPersona().equals(id))
                        .map(t -> (Conductor) t)
                        .findFirst());
    }

    protected Optional<Auxiliar> findAuxiliar(IdPersona id, Rut rutEmpresa) {
        return findEmpresa(rutEmpresa)
                .flatMap(e -> Arrays.stream(e.getTripulantes())
                        .filter(t -> t instanceof Auxiliar && t.getIdPersona().equals(id))
                        .map(t -> (Auxiliar) t)
                        .findFirst());
    }

    protected void setInstanciaPersistente(ControladorEmpresas obj) {
        instance = obj;
    }

    protected void setDatosIniciales(Object[] objetos) {
        empresas.clear();
        buses.clear();
        terminales.clear();

        Arrays.stream(objetos).forEach(obj -> {
            if (obj instanceof Empresa) {
                empresas.add((Empresa) obj);
            } else if (obj instanceof Bus) {
                buses.add((Bus) obj);
            } else if (obj instanceof Terminal) {
                terminales.add((Terminal) obj);
            }
        });
    }
}