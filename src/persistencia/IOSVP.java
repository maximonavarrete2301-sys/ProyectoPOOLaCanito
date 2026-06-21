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
        List<Object>    objetos     = new ArrayList<>();
        List<Empresa>   empresas    = new ArrayList<>();
        List<Tripulante> tripulantes = new ArrayList<>();
        List<Terminal>  terminales  = new ArrayList<>();
        List<Bus>       buses       = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(ARCHIVO_DATOS_INICIALES), "UTF-8"))) {

            String linea;
            int seccion = 1;

            while ((linea = br.readLine()) != null) {

                if (linea.trim().equals("+")) {
                    seccion++;
                    continue;
                }
                if (linea.trim().isEmpty()) continue;

                String[] p = linea.split(";");

                switch (seccion) {
                    case 1: {
                        String tipo = p[0].trim();
                        Rut rut = Rut.of(p[1].trim());
                        Tratamiento trat = p[2].trim().equalsIgnoreCase("SRA")
                                ? Tratamiento.SRA : Tratamiento.SR;
                        Nombre nom = new Nombre(trat, p[3].trim(), p[4].trim(), p[5].trim());
                        String fono = p[6].trim();

                        if (tipo.equals("C") || tipo.equals("CP")) {
                            Cliente cli = new Cliente(rut, nom, p[7].trim());
                            cli.setTelefono(fono);
                            objetos.add(cli);
                        }

                        if (tipo.equals("P") || tipo.equals("CP")) {
                            int idx = tipo.equals("P") ? 7 : 8;
                            Tratamiento tratC = p[idx].trim().equalsIgnoreCase("SRA")
                                    ? Tratamiento.SRA : Tratamiento.SR;
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
                        Empresa emp = new Empresa(Rut.of(p[0].trim()), p[1].trim(), p[2].trim());
                        empresas.add(emp);
                        objetos.add(emp);
                        break;
                    }

                    case 3: {
                        String tipo = p[0].trim();
                        Rut rutT    = Rut.of(p[1].trim());
                        Tratamiento trat = p[2].trim().equalsIgnoreCase("SRA")
                                ? Tratamiento.SRA : Tratamiento.SR;
                        Nombre nomT  = new Nombre(trat, p[3].trim(), p[4].trim(), p[5].trim());
                        Direccion dir = new Direccion(p[6].trim(), p[7].trim(), p[8].trim());
                        Rut rutEmp   = Rut.of(p[9].trim());

                        Optional<Empresa> empOpt = findEmpresa(empresas, e -> e.getRut().equals(rutEmp));
                        if (empOpt.isEmpty()) break;

                        Empresa emp = empOpt.get();
                        int cantAntes = emp.getTripulantes().length;

                        if (tipo.equals("A")) {
                            emp.addAuxiliar(rutT, nomT, dir);
                        } else if (tipo.equals("C")) {
                            emp.addConductor(rutT, nomT, dir);
                        } else {
                            break;
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
                        Direccion dir = new Direccion(p[1].trim(), p[2].trim(), p[3].trim());
                        Terminal term = new Terminal(p[0].trim(), dir);
                        terminales.add(term);
                        objetos.add(term);
                        break;
                    }

                    case 5: {
                        Rut rutEmp = Rut.of(p[4].trim());
                        Optional<Empresa> empOpt = findEmpresa(empresas, e -> e.getRut().equals(rutEmp));
                        if (empOpt.isEmpty()) break;

                        Bus bus = new Bus(p[0].trim(), Integer.parseInt(p[3].trim()), empOpt.get());
                        bus.setMarca(p[1].trim());
                        bus.setModelo(p[2].trim());
                        buses.add(bus);
                        objetos.add(bus);
                        break;
                    }

                    case 6: {
                        LocalDate fecha = LocalDate.parse(p[0].trim(),
                                DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                        LocalTime hora  = LocalTime.parse(p[1].trim(),
                                DateTimeFormatter.ofPattern("HH:mm"));
                        int precio   = Integer.parseInt(p[2].trim());
                        int duracion = Integer.parseInt(p[3].trim());

                        String patente    = p[4].trim();
                        Rut    rutAux     = Rut.of(p[5].trim());
                        Rut    rutCond    = Rut.of(p[6].trim());
                        String nomSalida  = p[7].trim();
                        String nomLlegada = p[8].trim();

                        Optional<Bus>      busOpt  = findBus(buses, b -> b.getPatente().equalsIgnoreCase(patente));
                        Optional<Terminal> salOpt   = findTerminal(terminales, t -> t.getNombre().equalsIgnoreCase(nomSalida));
                        Optional<Terminal> llegOpt  = findTerminal(terminales, t -> t.getNombre().equalsIgnoreCase(nomLlegada));

                        if (busOpt.isEmpty() || salOpt.isEmpty() || llegOpt.isEmpty()) break;

                        Auxiliar aux = (Auxiliar) tripulantes.stream()
                                .filter(t -> t instanceof Auxiliar && t.getIdPersona().equals(rutAux))
                                .findFirst().orElse(null);

                        Conductor cond = (Conductor) tripulantes.stream()
                                .filter(t -> t instanceof Conductor && t.getIdPersona().equals(rutCond))
                                .findFirst().orElse(null);

                        if (aux == null || cond == null) break;

                        Viaje viaje = new Viaje(fecha, hora, precio, duracion,
                                busOpt.get(), aux, new Conductor[]{ cond },
                                salOpt.get(), llegOpt.get());
                        objetos.add(viaje);
                        break;
                    }
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