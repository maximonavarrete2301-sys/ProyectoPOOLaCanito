package vista;
//Victor Diaz, Rodrigo Henriquez, Maximo Navarrete
import controlador.ControladorEmpresas;
import controlador.SistemaVentaPasajes;
import excepciones.SVPException;
import modelo.*;
import persistencia.IOSVP;
import utilidades.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;

public class UISVP {

    private static UISVP instance;

    private Scanner sc;
    private SistemaVentaPasajes sistema;
    private ControladorEmpresas controlador;
    private IOSVP iosvp;

    private String idVentaActual;
    private TipoDocumento tipoVentaActual;

    private UISVP() {
        sc         = new Scanner(System.in);
        sistema    = SistemaVentaPasajes.getInstance();
        controlador = ControladorEmpresas.getInstance();
        iosvp      = IOSVP.getInstance();
    }

    public static UISVP getInstance() {
        if (instance == null) {
            instance = new UISVP();
        }
        return instance;
    }

    public void menu() {
        int op;
        do {
            System.out.println("\n...:::: Menu principal ::::...");
            System.out.println();
            System.out.println(" 1) Crear empresa");
            System.out.println(" 2) Contratar tripulante");
            System.out.println(" 3) Crear terminal");
            System.out.println(" 4) Crear cliente");
            System.out.println(" 5) Crear bus");
            System.out.println(" 6) Crear viaje");
            System.out.println(" 7) Vender pasajes");
            System.out.println(" 8) Listar ventas");
            System.out.println(" 9) Listar viajes");
            System.out.println("10) Listar pasajeros de viaje");
            System.out.println("11) Listar empresas");
            System.out.println("12) Listar llegadas/salidas de terminal");
            System.out.println("13) Listar ventas de empresa");
            System.out.println("14) Generar pasajes venta");
            System.out.println("15) Leer datos iniciales");
            System.out.println("16) Guardar datos del sistema");
            System.out.println("17) Leer datos del sistema");
            System.out.println("18) Salir");
            System.out.println("----------------------------------------");
            System.out.print("..:: Ingrese numero de opcion: ");

            try {
                op = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("*** Error: opcion no valida ***");
                op = 0;
                continue;
            }

            switch (op) {
                case 1:  createEmpresa();               break;
                case 2:  contrataTripulante();           break;
                case 3:  createTerminal();               break;
                case 4:  createCliente();                break;
                case 5:  createBus();                    break;
                case 6:  createViaje();                  break;
                case 7:  vendePasajes();                 break;
                case 8:  listVentas();                   break;
                case 9:  listViajes();                   break;
                case 10: listPasajerosViaje();           break;
                case 11: listEmpresas();                 break;
                case 12: listLlegadasSalidasTerminal();  break;
                case 13: listVentasEmpresa();            break;
                case 14: generarPasajesVenta();          break;
                case 15: leerDatosIniciales();           break;
                case 16: guardarDatosSistema();          break;
                case 17: leerDatosSistema();             break;
                case 18: System.out.println("Fin del programa."); break;
                default: System.out.println("*** Error: opcion no valida ***");
            }

        } while (op != 18);
    }

    private void createEmpresa() {
        try {
            System.out.println("\n...::::: Creando una nueva Empresa ::::....");
            System.out.print("             R.U.T (Con puntos y guion): ");
            String rut = sc.nextLine().trim();
            if (rut.isEmpty()) { System.out.println("*** Error: RUT no puede estar vacio ***"); return; }
            System.out.print("          Nombre : ");
            String nombre = sc.nextLine().trim();
            if (nombre.isEmpty()) { System.out.println("*** Error: nombre no puede estar vacio ***"); return; }
            System.out.print("             url : ");
            String url = sc.nextLine().trim();

            controlador.createEmpresa(Rut.of(rut), nombre, url);
            System.out.println("\n...::::: Empresa guardada exitosamente ::::....");

        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private void contrataTripulante() {
        try {
            System.out.println("\n...:::::: Contratando un nuevo Tripulante ::::....");
            System.out.println("\n:::: Dato de la Empresa");
            System.out.print("            R.U.T (Con puntos y guion): ");
            String rutEmp = sc.nextLine().trim();
            if (rutEmp.isEmpty()) { System.out.println("*** Error: RUT no puede estar vacio ***"); return; }

            System.out.println("\n:::: Datos tripulante");
            System.out.print("Auxiliar[1] o Conductor[2] : ");
            int tipo;
            try { tipo = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("*** Error: opcion invalida ***"); return; }
            if (tipo != 1 && tipo != 2) { System.out.println("*** Error: opcion invalida ***"); return; }

            System.out.print("   Rut[1] o Pasaporte[2] : ");
            IdPersona id = leerIdPersona();
            if (id == null) return;

            Nombre nombre = leerNombre();
            if (nombre == null) return;

            Direccion dir = leerDireccion();

            if (tipo == 1) {
                controlador.hireAuxiliarForEmpresa(Rut.of(rutEmp), id, nombre, dir);
                System.out.println("\n...::::: Auxiliar contratado exitosamente ::::....");
            } else {
                controlador.hireConductorForEmpresa(Rut.of(rutEmp), id, nombre, dir);
                System.out.println("\n...::::: Conductor contratado exitosamente ::::....");
            }

        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private void createTerminal() {
        try {
            System.out.println("\n...::::: Creando un nuevo Terminal ::::....");
            System.out.print("  Nombre : ");
            String nombre = sc.nextLine().trim();
            if (nombre.isEmpty()) { System.out.println("*** Error: nombre no puede estar vacio ***"); return; }

            Direccion dir = leerDireccion();

            controlador.createTerminal(nombre, dir);
            System.out.println("\n...::::: Terminal guardado exitosamente ::::....");

        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private void createCliente() {
        try {
            System.out.println("\n...::::: Creando un nuevo Cliente ::::....");
            System.out.print("Rut[1] o Pasaporte[2] : ");
            IdPersona id = leerIdPersona();
            if (id == null) return;

            Nombre nom = leerNombre();
            if (nom == null) return;

            System.out.print("      Telefono : ");
            String fono = sc.nextLine().trim();
            System.out.print("        Email : ");
            String email = sc.nextLine().trim();
            if (email.isEmpty()) { System.out.println("*** Error: email no puede estar vacio ***"); return; }

            sistema.createCliente(id, nom, fono, email);
            System.out.println("\n...::::: Cliente guardado exitosamente ::::....");

        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private void createBus() {
        try {
            System.out.println("\n...::::: Creando un nuevo Bus ::::....");
            System.out.print("          Patente : ");
            String patente = sc.nextLine().trim();
            if (patente.isEmpty()) { System.out.println("*** Error: patente no puede estar vacia ***"); return; }
            System.out.print("            Marca : ");
            String marca = sc.nextLine().trim();
            System.out.print("           Modelo : ");
            String modelo = sc.nextLine().trim();
            System.out.print("Numero de asientos : ");
            int asientos;
            try { asientos = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("*** Error: numero de asientos invalido ***"); return; }
            if (asientos <= 0) { System.out.println("*** Error: numero de asientos debe ser mayor a 0 ***"); return; }

            System.out.println("\n:::: Dato de la empresa");
            System.out.print("            R.U.T (Con puntos y guion): ");
            String rut = sc.nextLine().trim();
            if (rut.isEmpty()) { System.out.println("*** Error: RUT no puede estar vacio ***"); return; }

            controlador.createBus(patente, marca, modelo, asientos, Rut.of(rut));
            System.out.println("\n...::::: Bus guardado exitosamente ::::....");

        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private void createViaje() {
        try {
            System.out.println("\n...::::: Creando un nuevo Viaje ::::....");
            System.out.print("   Fecha[dd/mm/yyyy] : ");
            LocalDate fecha = leerFecha();
            if (fecha == null) return;

            System.out.print("     Hora[hh:mm] : ");
            LocalTime hora = leerHora();
            if (hora == null) return;

            System.out.print("          Precio : ");
            int precio;
            try { precio = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("*** Error: precio invalido ***"); return; }
            if (precio <= 0) { System.out.println("*** Error: precio debe ser mayor a 0 ***"); return; }

            System.out.print("Duracion (minutos) : ");
            int duracion;
            try { duracion = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("*** Error: duracion invalida ***"); return; }
            if (duracion <= 0) { System.out.println("*** Error: duracion debe ser mayor a 0 ***"); return; }

            System.out.print("     Patente Bus : ");
            String patente = sc.nextLine().trim();
            if (patente.isEmpty()) { System.out.println("*** Error: patente no puede estar vacia ***"); return; }

            System.out.print("Nro. de conductores : ");
            int nroCond;
            try { nroCond = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("*** Error: numero invalido ***"); return; }
            if (nroCond < 1 || nroCond > 2) { System.out.println("*** Error: numero de conductores debe ser 1 o 2 ***"); return; }

            IdPersona[] tripulantes = new IdPersona[1 + nroCond];

            System.out.println(":: Id Auxiliar ::");
            System.out.print("Rut[1] o Pasaporte[2] : ");
            tripulantes[0] = leerIdPersona();
            if (tripulantes[0] == null) return;

            for (int i = 1; i <= nroCond; i++) {
                System.out.println(":: Id Conductor " + i + " ::");
                System.out.print("Rut[1] o Pasaporte[2] : ");
                tripulantes[i] = leerIdPersona();
                if (tripulantes[i] == null) return;
            }

            System.out.print("Nombre comuna salida : ");
            String comSalida = sc.nextLine().trim();
            System.out.print("Nombre comuna llegada : ");
            String comLlegada = sc.nextLine().trim();
            if (comSalida.isEmpty() || comLlegada.isEmpty()) {
                System.out.println("*** Error: comunas no pueden estar vacias ***"); return;
            }

            sistema.createViaje(fecha, hora, precio, duracion, patente,
                    tripulantes, new String[]{ comSalida, comLlegada });
            System.out.println("\n...::::: Viaje guardado exitosamente ::::....");

        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private void vendePasajes() {
        try {
            System.out.println("\n...::::: Venta de pasajes ::::....");
            System.out.println("\n:::: Datos de la Venta");
            System.out.print("         ID Documento : ");
            idVentaActual = sc.nextLine().trim();
            if (idVentaActual.isEmpty()) { System.out.println("*** Error: ID no puede estar vacio ***"); return; }

            System.out.print("Tipo documento: [1] Boleta [2] Factura : ");
            int tipoDoc;
            try { tipoDoc = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("*** Error: opcion invalida ***"); return; }
            if (tipoDoc != 1 && tipoDoc != 2) { System.out.println("*** Error: opcion invalida ***"); return; }
            tipoVentaActual = (tipoDoc == 1) ? TipoDocumento.BOLETA : TipoDocumento.FACTURA;

            System.out.print("Fecha de viaje[dd/mm/yyyy] : ");
            LocalDate fecha = leerFecha();
            if (fecha == null) return;

            System.out.print("      Origen (comuna) : ");
            String salida = sc.nextLine().trim();
            System.out.print("     Destino (comuna) : ");
            String llegada = sc.nextLine().trim();
            if (salida.isEmpty() || llegada.isEmpty()) {
                System.out.println("*** Error: comunas no pueden estar vacias ***"); return;
            }

            System.out.println("\n:::: Datos del cliente");
            System.out.print("Rut[1] o Pasaporte[2] : ");
            IdPersona idCliente = leerIdPersona();
            if (idCliente == null) return;

            System.out.println("\n:::: Pasajes a vender");
            System.out.print("Cantidad de pasajes : ");
            int nroPasajes;
            try { nroPasajes = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("*** Error: cantidad invalida ***"); return; }
            if (nroPasajes <= 0) { System.out.println("*** Error: cantidad debe ser mayor a 0 ***"); return; }

            sistema.iniciaVenta(idVentaActual, tipoVentaActual, fecha, salida, llegada, nroPasajes, idCliente);

            String[][] horarios = sistema.getHorariosDisponibles(fecha, salida, llegada, nroPasajes);
            System.out.println("\n:::: Listado de horarios disponibles");
            System.out.println("   *----------*----------*----------*----------*");
            System.out.printf("   | %-8s | %-8s | %-8s | %-8s |\n", "BUS", "SALIDA", "VALOR", "ASIENTOS");
            System.out.println("   |----------+----------+----------+----------|");
            for (int i = 0; i < horarios.length; i++) {
                System.out.printf("%2d | %-8s | %-8s | $%-7s | %-8s |\n",
                        i + 1, horarios[i][0], horarios[i][1], horarios[i][2], horarios[i][3]);
                System.out.println("   |----------+----------+----------+----------|");
            }
            System.out.println("   *----------*----------*----------*----------*");

            System.out.print("Seleccione viaje [1.." + horarios.length + "] : ");
            int selViaje;
            try { selViaje = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("*** Error: seleccion invalida ***"); return; }
            if (selViaje < 1 || selViaje > horarios.length) {
                System.out.println("*** Error: seleccion fuera de rango ***"); return;
            }

            String patBus = horarios[selViaje - 1][0];
            LocalTime hora = LocalTime.parse(horarios[selViaje - 1][1]);

            String[] asientosStr = sistema.listAsientosDeViaje(fecha, hora, patBus);
            System.out.println("\n:::: Asientos disponibles para el viaje seleccionado");
            System.out.print("*---*---*---*---*\n");
            for (int i = 0; i < asientosStr.length; i++) {
                System.out.printf("| %2s ", asientosStr[i]);
                if ((i + 1) % 4 == 0) System.out.print("|\n|---+---+---+---|\n");
            }
            if (asientosStr.length % 4 != 0) System.out.println();
            System.out.println("*---*---*---*---*");

            for (int i = 0; i < nroPasajes; i++) {
                System.out.println("\n:::: Datos pasajeros " + (i + 1));
                System.out.print("Rut[1] o Pasaporte[2] : ");
                IdPersona idPas = leerIdPersona();
                if (idPas == null) return;

                Optional<String> nombrePas = sistema.getNombrePasajero(idPas);
                if (nombrePas.isEmpty()) {
                    System.out.println("Pasajero no registrado. Ingrese sus datos:");
                    Nombre nom = leerNombre();
                    if (nom == null) return;
                    System.out.print("Telefono pasajero : ");
                    String fono = sc.nextLine().trim();
                    System.out.println("--- Contacto de emergencia ---");
                    Nombre nomEmerg = leerNombre();
                    if (nomEmerg == null) return;
                    System.out.print("Fono contacto : ");
                    String fc = sc.nextLine().trim();
                    sistema.createPasajero(idPas, nom, fono, nomEmerg, fc);
                }

                System.out.print("Numero de Asiento : ");
                int asiento;
                try { asiento = Integer.parseInt(sc.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("*** Error: asiento invalido ***"); return; }

                sistema.vendePasaje(idVentaActual, tipoVentaActual, fecha, hora, patBus, asiento, idPas);
                System.out.println(":::: Pasaje agregado exitosamente");
            }

            Optional<Integer> monto = sistema.getMontoVenta(idVentaActual, tipoVentaActual);
            System.out.println("\n:::: Monto total de la venta: $" + monto.get());

            System.out.println("\n:::: Pago de la venta");
            System.out.print("Efectivo[1] o Tarjeta[2] : ");
            int opPago;
            try { opPago = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("*** Error: opcion invalida ***"); return; }

            if (opPago == 1) {
                sistema.pagaVenta(idVentaActual, tipoVentaActual);
            } else if (opPago == 2) {
                System.out.print("Numero de tarjeta : ");
                long nroTarjeta;
                try { nroTarjeta = Long.parseLong(sc.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("*** Error: numero de tarjeta invalido ***"); return; }
                sistema.pagaVenta(idVentaActual, tipoVentaActual, nroTarjeta);
            } else {
                System.out.println("*** Error: opcion invalida ***"); return;
            }

            System.out.println("\n...:::::: Venta realizada exitosamente ::::....");

        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private void listVentas() {
        String[][] datos = sistema.listVentas();
        System.out.println("\n...::::: Listado de ventas ::::....");
        if (datos.length == 0) { System.out.println("No hay ventas registradas."); return; }
        System.out.printf("%-12s | %-8s | %-12s | %-15s | %-25s | %-5s | %-10s\n",
                "ID DOC", "TIPO", "FECHA", "ID CLIENTE", "NOMBRE CLIENTE", "CANT", "TOTAL");
        System.out.println("-".repeat(100));
        for (String[] fila : datos) {
            System.out.printf("%-12s | %-8s | %-12s | %-15s | %-25s | %-5s | $%-9s\n",
                    fila[0], fila[1], fila[2], fila[3], fila[4], fila[5], fila[6]);
        }
    }

    private void listViajes() {
        String[][] datos = sistema.listViajes();
        System.out.println("\n...::::: Listado de viajes ::::....");
        if (datos.length == 0) { System.out.println("No hay viajes registrados."); return; }
        System.out.printf("*%-12s*%-10s*%-10s*%-10s*%-6s*%-10s*%-17s*\n",
                "FECHA", "HORA SALE", "HORA LLEGA", "PRECIO", "DISP", "PATENTE", "ORIGEN");
        System.out.println("-".repeat(85));
        for (String[] fila : datos) {
            System.out.printf(" %-12s| %-10s| %-10s| $%-8s| %-6s| %-10s| %-17s\n",
                    fila[0], fila[1], fila[2], fila[3], fila[4], fila[5], fila[6]);
            System.out.println("-".repeat(85));
        }
    }

    private void listPasajerosViaje() {
        try {
            System.out.println("\n...::::: Listado de pasajeros de viaje ::::....");
            System.out.print("Fecha[dd/mm/yyyy] : ");
            LocalDate fecha = leerFecha();
            if (fecha == null) return;
            System.out.print("Hora[hh:mm] : ");
            LocalTime hora = leerHora();
            if (hora == null) return;
            System.out.print("Patente bus : ");
            String pat = sc.nextLine().trim();

            String[][] datos = sistema.listPasajerosViaje(fecha, hora, pat);
            if (datos.length == 0) { System.out.println("No hay pasajeros en este viaje."); return; }
            System.out.printf("%-15s | %-25s | %-20s | %-12s\n", "ID PASAJERO", "NOMBRE", "CONTACTO", "FONO CONT");
            System.out.println("-".repeat(80));
            for (String[] fila : datos) {
                System.out.printf("%-15s | %-25s | %-20s | %-12s\n", fila[0], fila[1], fila[2], fila[3]);
            }

        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private void listEmpresas() {
        String[][] datos = controlador.listEmpresas();
        System.out.println("\n...::::: Listado de empresas ::::....");
        if (datos.length == 0) { System.out.println("No hay empresas registradas."); return; }
        System.out.printf("*%-14s*%-22s*%-30s*%-18s*%-12s*\n",
                "RUT EMPRESA", "NOMBRE", "URL", "NRO. TRIPULANTES", "NRO. BUSES");
        System.out.println("-".repeat(102));
        for (String[] fila : datos) {
            System.out.printf(" %-14s| %-22s| %-30s| %-18s| %-12s\n",
                    fila[0], fila[1], fila[2], fila[3], fila[4]);
            System.out.println("-".repeat(102));
        }
    }

    private void listLlegadasSalidasTerminal() {
        try {
            System.out.println("\n...::::: Listado de llegadas y salidas de un terminal ::::....");
            System.out.print("   Nombre terminal : ");
            String nombre = sc.nextLine().trim();
            if (nombre.isEmpty()) { System.out.println("*** Error: nombre no puede estar vacio ***"); return; }
            System.out.print("Fecha[dd/mm/yyyy] : ");
            LocalDate fecha = leerFecha();
            if (fecha == null) return;

            String[][] datos = controlador.listLlegadasSalidasTerminal(nombre, fecha);
            if (datos.length == 0) { System.out.println("No hay llegadas ni salidas para esa fecha."); return; }
            System.out.printf("*%-16s*%-8s*%-12s*%-22s*%-16s*\n",
                    "LLEGADA/SALIDA", "HORA", "PATENTE BUS", "NOMBRE EMPRESA", "NRO. PASAJEROS");
            System.out.println("-".repeat(82));
            for (String[] fila : datos) {
                System.out.printf(" %-16s| %-8s| %-12s| %-22s| %-16s\n",
                        fila[0], fila[1], fila[2], fila[3], fila[4]);
                System.out.println("-".repeat(82));
            }

        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private void listVentasEmpresa() {
        try {
            System.out.println("\n...::::: Listado de ventas de empresa ::::....");
            System.out.print("R.U.T (Con puntos y guion): ");
            String rut = sc.nextLine().trim();
            if (rut.isEmpty()) { System.out.println("*** Error: RUT no puede estar vacio ***"); return; }

            String[][] datos = controlador.listVentasEmpresa(Rut.of(rut));
            if (datos.length == 0) { System.out.println("La empresa no tiene ventas registradas."); return; }
            System.out.printf("*%-12s*%-10s*%-14s*%-15s*\n", "FECHA", "TIPO", "MONTO PAGADO", "TIPO PAGO");
            System.out.println("-".repeat(56));
            for (String[] fila : datos) {
                System.out.printf(" %-12s| %-10s| $%-12s| %-15s\n",
                        fila[0], fila[1], fila[2], fila[3]);
                System.out.println("-".repeat(56));
            }

        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private void generarPasajesVenta() {
        try {
            System.out.println("\n...::::: Generar pasajes de venta ::::....");
            System.out.print("ID Documento : ");
            String idDoc = sc.nextLine().trim();
            if (idDoc.isEmpty()) { System.out.println("*** Error: ID no puede estar vacio ***"); return; }

            System.out.print("Tipo documento: [1] Boleta [2] Factura : ");
            int tipoDoc;
            try { tipoDoc = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("*** Error: opcion invalida ***"); return; }
            if (tipoDoc != 1 && tipoDoc != 2) { System.out.println("*** Error: opcion invalida ***"); return; }
            TipoDocumento tipo = (tipoDoc == 1) ? TipoDocumento.BOLETA : TipoDocumento.FACTURA;

            sistema.generatePasajesVenta(idDoc, tipo);
            System.out.println("\n...::::: Pasajes generados exitosamente ::::....");

        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private void leerDatosIniciales() {
        try {
            sistema.readDatosIniciales();
            System.out.println("\n...::::: Datos iniciales cargados exitosamente ::::....");
        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private void guardarDatosSistema() {
        try {
            sistema.saveDatosSistema();
            System.out.println("\n...::::: Datos del sistema guardados exitosamente ::::....");
        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private void leerDatosSistema() {
        try {
            sistema.readDatosSistema();
            System.out.println("\n...::::: Datos del sistema cargados exitosamente ::::....");
        } catch (SVPException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }

    private LocalDate leerFecha() {
        try {
            return LocalDate.parse(sc.nextLine().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            System.out.println("*** Error: formato de fecha invalido. Use dd/mm/yyyy ***");
            return null;
        }
    }

    private LocalTime leerHora() {
        try {
            return LocalTime.parse(sc.nextLine().trim(), DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            System.out.println("*** Error: formato de hora invalido. Use hh:mm ***");
            return null;
        }
    }

    private IdPersona leerIdPersona() {
        try {
            int tipo = Integer.parseInt(sc.nextLine().trim());
            if (tipo == 1) {
                System.out.print("         R.U.T (Con puntos y guion): ");
                String rut = sc.nextLine().trim();
                if (rut.isEmpty()) { System.out.println("*** Error: RUT no puede estar vacio ***"); return null; }
                return Rut.of(rut);
            } else if (tipo == 2) {
                System.out.print("Pasaporte Nro : ");
                String nro = sc.nextLine().trim();
                System.out.print("Nacionalidad : ");
                String nac = sc.nextLine().trim();
                return Pasaporte.of(nro, nac);
            } else {
                System.out.println("*** Error: opcion invalida ***");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("*** Error: opcion invalida ***");
            return null;
        }
    }

    private Nombre leerNombre() {
        try {
            System.out.print("        Sr.[1] o Sra.[2] : ");
            int op = Integer.parseInt(sc.nextLine().trim());
            if (op != 1 && op != 2) { System.out.println("*** Error: opcion invalida ***"); return null; }
            Tratamiento t = (op == 1) ? Tratamiento.SR : Tratamiento.SRA;
            System.out.print("             Nombres : ");
            String nom = sc.nextLine().trim();
            System.out.print("    Apellido Paterno : ");
            String apeP = sc.nextLine().trim();
            System.out.print("    Apellido Materno : ");
            String apeM = sc.nextLine().trim();
            if (nom.isEmpty() || apeP.isEmpty() || apeM.isEmpty()) {
                System.out.println("*** Error: nombre no puede tener campos vacios ***"); return null;
            }
            return new Nombre(t, nom, apeP, apeM);
        } catch (NumberFormatException e) {
            System.out.println("*** Error: opcion invalida ***");
            return null;
        }
    }

    private Direccion leerDireccion() {
        System.out.print("   Calle : ");
        String calle = sc.nextLine().trim();
        System.out.print("  Numero : ");
        String numero = sc.nextLine().trim();
        System.out.print("  Comuna : ");
        String comuna = sc.nextLine().trim();
        return new Direccion(calle, numero, comuna);
    }
}