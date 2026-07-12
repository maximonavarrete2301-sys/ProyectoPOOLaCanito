package persistencia;
//Victor Diaz, Maximo Navarrete, Rodrigo Henriquez

import controlador.ControladorEmpresas;
import controlador.SistemaVentaPasajes;
import excepciones.SVPException;
import modelo.*;
import utilidades.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class IOSVP {

    private static IOSVP instance;

    private IOSVP() {}

    public static IOSVP getInstance() {
        if (instance == null) {
            instance = new IOSVP();
        }
        return instance;
    }

    private static final String ARCHIVO_DATOS_INICIALES = "SVPDatosIniciales.txt";
    private static final String ARCHIVO_OBJETOS = "SVPObjetos.obj";

    public Object[] readDatosIniciales() {
        List<Object> objetos = new ArrayList<>();
        List<Empresa> empresas = new ArrayList<>();
        List<Tripulante> tripulantes = new ArrayList<>();
        List<Terminal> terminales = new ArrayList<>();
        List<Bus> buses = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(ARCHIVO_DATOS_INICIALES), "UTF-8"))) {

            String linea;
            int seccion = 1;
            int nroLinea = 0;

            while ((linea = br.readLine()) != null) {
                nroLinea++;
                linea = linea.trim();

                if (linea.equals("+")) {
                    seccion++;
                    continue;
                }
                if (linea.isEmpty()) {
                    continue;
                }

                String[] p = linea.split(";");
                int lineaActual = nroLinea;

                try {
                    switch (seccion) {
                        case 1: {
                            validarColumnas(p, 8, lineaActual);
                            String tipo = p[0].trim();
                            Rut rut = Rut.of(p[1].trim());
                            Tratamiento trat = parseTratamiento(p[2]);
                            Nombre nom = new Nombre(trat, p[3].trim(), p[4].trim(), p[5].trim());
                            String fono = p[6].trim();

                            if (tipo.equals("C") || tipo.equals("CP")) {
                                Cliente cli = new Cliente(rut, nom, p[7].trim());
                                cli.setTelefono(fono);
                                objetos.add(cli);
                            }

                            if (tipo.equals("P") || tipo.equals("CP")) {
                                int idx = tipo.equals("P") ? 7 : 8;
                                validarColumnas(p, idx + 5, lineaActual);
                                Tratamiento tratC = parseTratamiento(p[idx]);
                                Nombre nomC = new Nombre(tratC,
                                        p[idx + 1].trim(), p[idx + 2].trim(), p[idx + 3].trim());
                                String fonoC = p[idx + 4].trim();
                                Pasajero pas = new Pasajero(rut, nom, nomC, fonoC);
                                pas.setTelefono(fono);
                                objetos.add(pas);
                            }
                            break;
                        }

                        case 2: {
                            validarColumnas(p, 3, lineaActual);
                            Empresa emp = new Empresa(Rut.of(p[0].trim()), p[1].trim(), p[2].trim());
                            empresas.add(emp);
                            objetos.add(emp);
                            break;
                        }

                        case 3: {
                            validarColumnas(p, 10, lineaActual);
                            String tipo = p[0].trim();
                            Rut rutT = Rut.of(p[1].trim());
                            Tratamiento trat = parseTratamiento(p[2]);
                            Nombre nomT = new Nombre(trat, p[3].trim(), p[4].trim(), p[5].trim());
                            Direccion dir = new Direccion(p[6].trim(), p[7].trim(), p[8].trim());
                            Rut rutEmp = Rut.of(p[9].trim());

                            Empresa emp = findEmpresa(empresas, e -> e.getRut().equals(rutEmp))
                                    .orElseThrow(() -> new SVPException("No existe empresa para tripulante en línea " + lineaActual));

                            int cantAntes = emp.getTripulantes().length;

                            if (tipo.equals("A")) {
                                emp.addAuxiliar(rutT, nomT, dir);
                            } else if (tipo.equals("C")) {
                                emp.addConductor(rutT, nomT, dir);
                            } else {
                                throw new SVPException("Tipo de tripulante inválido en línea " + lineaActual);
                            }

                            Tripulante[] todos = emp.getTripulantes();
                            if (todos.length > cantAntes) {
                                Tripulante nuevo = todos[todos.length - 1];
                                tripulantes.add(nuevo);
                                objetos.add(nuevo);
                            }
                            break;
                        }

                        case 4: {
                            validarColumnas(p, 4, lineaActual);
                            Direccion dir = new Direccion(p[1].trim(), p[2].trim(), p[3].trim());
                            Terminal term = new Terminal(p[0].trim(), dir);
                            terminales.add(term);
                            objetos.add(term);
                            break;
                        }

                        case 5: {
                            validarColumnas(p, 5, lineaActual);
                            Rut rutEmp = Rut.of(p[4].trim());
                            Empresa emp = findEmpresa(empresas, e -> e.getRut().equals(rutEmp))
                                    .orElseThrow(() -> new SVPException("No existe empresa para bus en línea " + lineaActual));

                            Bus bus = new Bus(p[0].trim(), Integer.parseInt(p[3].trim()), emp);
                            bus.setMarca(p[1].trim());
                            bus.setModelo(p[2].trim());
                            buses.add(bus);
                            objetos.add(bus);
                            break;
                        }

                        case 6: {
                            validarColumnas(p, 9, lineaActual);
                            LocalDate fecha = LocalDate.parse(p[0].trim(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                            LocalTime hora = LocalTime.parse(p[1].trim(), DateTimeFormatter.ofPattern("HH:mm"));
                            int precio = Integer.parseInt(p[2].trim());
                            int duracion = Integer.parseInt(p[3].trim());

                            String patente = p[4].trim();
                            Rut rutAux = Rut.of(p[5].trim());
                            Rut rutCond = Rut.of(p[6].trim());
                            String nomSalida = p[7].trim();
                            String nomLlegada = p[8].trim();

                            Bus bus = findBus(buses, b -> b.getPatente().equalsIgnoreCase(patente))
                                    .orElseThrow(() -> new SVPException("No existe bus para viaje en línea " + lineaActual));
                            Terminal salida = findTerminal(terminales, t -> t.getNombre().equalsIgnoreCase(nomSalida))
                                    .orElseThrow(() -> new SVPException("No existe terminal de salida en línea " + lineaActual));
                            Terminal llegada = findTerminal(terminales, t -> t.getNombre().equalsIgnoreCase(nomLlegada))
                                    .orElseThrow(() -> new SVPException("No existe terminal de llegada en línea " + lineaActual));

                            Empresa empresa = bus.getEmpresa();
                            Auxiliar aux = (Auxiliar) findTripulante(empresa, rutAux, "Auxiliar")
                                    .orElseThrow(() -> new SVPException("No existe auxiliar para viaje en línea " + lineaActual));
                            Conductor cond = (Conductor) findTripulante(empresa, rutCond, "Conductor")
                                    .orElseThrow(() -> new SVPException("No existe conductor para viaje en línea " + lineaActual));

                            Viaje viaje = new Viaje(fecha, hora, precio, duracion,
                                    bus, aux, new Conductor[]{cond}, salida, llegada);
                            objetos.add(viaje);
                            break;
                        }

                        default:
                            throw new SVPException("Sección inválida en línea " + lineaActual);
                    }
                } catch (SVPException e) {
                    throw e;
                } catch (RuntimeException e) {
                    throw new SVPException("Error en la línea " + nroLinea + " del archivo "
                            + ARCHIVO_DATOS_INICIALES + ": " + e.getMessage());
                }
            }

        } catch (FileNotFoundException e) {
            throw new SVPException("No existe o no se puede abrir el archivo " + ARCHIVO_DATOS_INICIALES);
        } catch (IOException e) {
            throw new SVPException("Error al leer el archivo " + ARCHIVO_DATOS_INICIALES
                    + ": " + e.getMessage());
        }

        return objetos.toArray();
    }

    private void validarColumnas(String[] datos, int minimo, int nroLinea) {
        if (datos.length < minimo) {
            throw new SVPException("Faltan datos en la línea " + nroLinea);
        }
    }

    private Tratamiento parseTratamiento(String texto) {
        return texto.trim().equalsIgnoreCase("SRA") ? Tratamiento.SRA : Tratamiento.SR;
    }

    public void saveControladores(Object[] controladores) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(ARCHIVO_OBJETOS))) {
            oos.writeObject(controladores);
        } catch (FileNotFoundException e) {
            throw new SVPException("No se puede abrir o crear el archivo " + ARCHIVO_OBJETOS);
        } catch (IOException e) {
            throw new SVPException("No se puede grabar en el archivo " + ARCHIVO_OBJETOS + ": " + e.getMessage());
        }
    }

    public Object[] readControladores() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(ARCHIVO_OBJETOS))) {
            return (Object[]) ois.readObject();
        } catch (FileNotFoundException e) {
            throw new SVPException("No existe o no se puede abrir el archivo " + ARCHIVO_OBJETOS);
        } catch (IOException e) {
            throw new SVPException("No se puede leer el archivo " + ARCHIVO_OBJETOS + ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new SVPException("Error al deserializar objetos de " + ARCHIVO_OBJETOS + ": " + e.getMessage());
        }
    }

    public void savePasajesDeVenta(Pasaje[] pasajes, String nombreArchivo) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(nombreArchivo);
        } catch (FileNotFoundException e) {
            throw new SVPException("No se puede abrir o crear el archivo " + nombreArchivo);
        }

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(fos, "UTF-8"))) {
            for (int i = 0; i < pasajes.length; i++) {
                if (pasajes[i] != null) {
                    pw.print(pasajes[i].toString());
                    if (i < pasajes.length - 1) pw.println();
                }
            }
        } catch (IOException e) {
            throw new SVPException("No se puede grabar en el archivo " + nombreArchivo + ": " + e.getMessage());
        }
    }

    public Optional<Empresa> findEmpresa(List<Empresa> empresas, Predicate<Empresa> criterio) {
        return empresas.stream().filter(criterio).findFirst();
    }

    public Optional<Bus> findBus(List<Bus> buses, Predicate<Bus> criterio) {
        return buses.stream().filter(criterio).findFirst();
    }

    public Optional<Terminal> findTerminal(List<Terminal> terminales, Predicate<Terminal> criterio) {
        return terminales.stream().filter(criterio).findFirst();
    }

    public Optional<Tripulante> findTripulante(Empresa empresa, IdPersona idBuscado, String rol) {
        return Arrays.stream(empresa.getTripulantes())
                .filter(t -> t != null && t.getIdPersona().equals(idBuscado))
                .filter(t -> {
                    if (rol.equalsIgnoreCase("Auxiliar"))  return t instanceof Auxiliar;
                    if (rol.equalsIgnoreCase("Conductor")) return t instanceof Conductor;
                    return false;
                })
                .findFirst();
    }
}