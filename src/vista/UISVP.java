package vista;

//Victor Diaz, Maximo Navarrete y Rodrigo Henriquez

import controlador.*;
import excepciones.*;
import modelo.*;
import utilidades.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;
import excepciones.*;

public class UISVP {

    private static UISVP instance;

    private Scanner sc;

    private SistemaVentaPasajes sistema;
    private ControladorEmpresas controlador;

    private String idVentaActual;
    private TipoDocumento tipoVentaActual;

    private UISVP() {
        sc = new Scanner(System.in);
        sistema = SistemaVentaPasajes.getInstance();
        controlador = ControladorEmpresas.getInstance();
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
            System.out.println("\n===== SISTEMA VENTA PASAJES =====");
            System.out.println("1. Crear empresa");
            System.out.println("2. Contratar tripulante");
            System.out.println("3. Crear terminal");
            System.out.println("4. Crear cliente");
            System.out.println("5. Crear bus");
            System.out.println("6. Crear viaje");
            System.out.println("7. Vender pasajes");
            System.out.println("8. Listar ventas");
            System.out.println("9. Listar viajes");
            System.out.println("10. Listar pasajeros viaje");
            System.out.println("11. Listar empresas");
            System.out.println("12. Listar llegadas/salidas terminal");
            System.out.println("13. Listar ventas empresa");
            System.out.println("14. Consulta viajes disponibles por fecha");
            System.out.println("15. Salir");
            System.out.print("Opcion: ");

            op = Integer.parseInt(sc.nextLine());

            switch (op) {
                case 1: createEmpresa(); break;
                case 2: contrataTripulante(); break;
                case 3: createTerminal(); break;
                case 4: createCliente(); break;
                case 5: createBus(); break;
                case 6: createViaje(); break;
                case 7: vendePasajes(); break;
                case 8: listVentas(); break;
                case 9: listViajes(); break;
                case 10: listPasajerosViaje(); break;
                case 11: listEmpresas(); break;
                case 12: listLlegadasSalidasTerminal(); break;
                case 13: listVentasEmpresa(); break;
                case 14: consultaViajesFecha(); break;
                case 15: System.out.println("Fin del programa"); break;
                default: System.out.println("error opcion no valida");
            }
        } while (op != 15);
    }

    private void createEmpresa() {
        try {
            System.out.print("R.u.t: ");
            String rut = sc.nextLine();
            System.out.print("Nombre: ");
            String nombre = sc.nextLine();
            System.out.print("URL: ");
            String url = sc.nextLine();

            controlador.createEmpresa(Rut.of(rut), nombre, url);
            System.out.println("Empresa creada");

        } catch (SistemaVentaPasajesException e) {
            System.out.println(e.getMessage());
        }
    }

    private void contrataTripulante() {
        try {
            System.out.print("Rut empresa: ");
            String rut = sc.nextLine();

            System.out.println("1. Conductor");
            System.out.println("2. Auxiliar");
            int tipo = Integer.parseInt(sc.nextLine());

            System.out.println("--- Datos del Tripulante ---");
            IdPersona id = leerIdPersona();
            Nombre nombre = leerNombre();
            Direccion dir = leerDireccion();

            if (tipo == 1) {
                controlador.hireConductorForEmpresa(Rut.of(rut), id, nombre, dir);
                System.out.println("Conductor contratado exitosamente");
            } else {
                controlador.hireAuxiliarForEmpresa(Rut.of(rut), id, nombre, dir);
            }

            System.out.println("Tripulante contratado exitosamente");

        } catch (SistemaVentaPasajesException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createTerminal() {
        try {
            System.out.print("Nombre: ");
            String nombre = sc.nextLine();

            System.out.println("--- Direccion del Terminal ---");
            Direccion dir = leerDireccion();

            controlador.createTerminal(nombre, dir);
            System.out.println("Terminal creado");

        } catch (SistemaVentaPasajesException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createCliente() {
        try {
            System.out.println("--- Datos del Cliente ---");
            IdPersona id = leerIdPersona();
            Nombre nom = leerNombre();

            System.out.print("Telefono: ");
            String fono = sc.nextLine();
            System.out.print("Email: ");
            String email = sc.nextLine();

            sistema.createCliente(id, nom, fono, email);
            System.out.println("Cliente creado");

        } catch (SistemaVentaPasajesException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createBus() {
        try {
            System.out.print("Patente: ");
            String patente = sc.nextLine();
            System.out.print("Marca: ");
            String marca = sc.nextLine();
            System.out.print("Modelo: ");
            String modelo = sc.nextLine();
            System.out.print("Cantidad asientos: ");
            int asientos = Integer.parseInt(sc.nextLine());
            System.out.print("Rut empresa: ");
            String rut = sc.nextLine();

            controlador.createBus(patente, marca, modelo, asientos, Rut.of(rut));
            System.out.println("Bus creado");

        } catch (SistemaVentaPasajesException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createViaje() {
        try {
            System.out.print("Fecha [dd/mm/yyyy]: ");
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fecha = LocalDate.parse(sc.nextLine(), formatoFecha);
            System.out.print("Hora [hh:mm]: ");
            LocalTime hora = LocalTime.parse(sc.nextLine());
            System.out.print("Precio: ");
            int precio = Integer.parseInt(sc.nextLine());
            System.out.print("Duracion minutos: ");
            int duracion = Integer.parseInt(sc.nextLine());
            System.out.print("Patente bus: ");
            String patente = sc.nextLine();
            System.out.print("Cantidad tripulantes : ");
            int cant = Integer.parseInt(sc.nextLine());

            IdPersona[] trip = new IdPersona[cant];
            for (int i = 0; i < cant; i++) {
                System.out.println("Tripulante " + (i + 1));
                trip[i] = leerIdPersona();
            }

            String[] comunas = new String[2];
            System.out.print("Comuna salida: ");
            comunas[0] = sc.nextLine();
            System.out.print("Comuna llegada: ");
            comunas[1] = sc.nextLine();

            sistema.createViaje(fecha, hora, precio, duracion, patente, trip, comunas);
            System.out.println("Viaje creado");

        } catch (SistemaVentaPasajesException e) {
            System.out.println(e.getMessage());
        }
    }

    private void vendePasajes() {
        try {
            System.out.print("ID documento venta: ");
            idVentaActual = sc.nextLine();

            System.out.print("Tipo documento [1] Boleta [2] Factura: ");
            tipoVentaActual = (Integer.parseInt(sc.nextLine()) == 1) ? TipoDocumento.BOLETA : TipoDocumento.FACTURA;

            System.out.print("Fecha viaje [dd/mm/yyyy]: ");

            String fechaTexto = sc.nextLine();

            String[] partesFecha = fechaTexto.split("/");

            LocalDate fecha = LocalDate.of(Integer.parseInt(partesFecha[2]), Integer.parseInt(partesFecha[1]), Integer.parseInt(partesFecha[0]));
            System.out.print("Comuna salida: ");
            String salida = sc.nextLine();
            System.out.print("Comuna llegada: ");
            String llegada = sc.nextLine();
            System.out.print("Cantidad pasajes: ");
            int nroPasajes = Integer.parseInt(sc.nextLine());

            System.out.println("--- Datos del Cliente que compra ---");
            IdPersona idCliente = leerIdPersona();

            sistema.iniciaVenta(idVentaActual, tipoVentaActual, fecha, salida, llegada, nroPasajes, idCliente);
            String[][] horarios = sistema.getHorariosDisponibles(fecha, salida, llegada, nroPasajes);
            System.out.println("\n--- VIAJES DISPONIBLES ---");
            System.out.printf("%-10s | %-8s | %-10s | %-10s\n", "PATENTE", "HORA", "PRECIO", "DISPONIBLES");
            for (int i = 0; i < horarios.length; i++) {
                System.out.printf("%-10s | %-8s | $%-9s | %-10s\n", horarios[i][0], horarios[i][1], horarios[i][2], horarios[i][3]);
            }

            System.out.print("\nPatente bus a elegir: ");
            String patBus = sc.nextLine();
            System.out.print("Hora viaje elegida [hh:mm]: ");
            LocalTime hora = LocalTime.parse(sc.nextLine());

            String[] asientosStr = sistema.listAsientosDeViaje(fecha, hora, patBus);
            System.out.println("\n--- MAPA DE ASIENTOS ---");
            for (int i = 0; i < asientosStr.length; i++) {

                System.out.printf("[%2s] ", asientosStr[i]);

                if ((i + 1) % 4 == 0) {
                    System.out.println();
                }
            }
            System.out.println();

            for (int i = 0; i < nroPasajes; i++) {
                System.out.print("\nNumero de Asiento: ");
                int asiento = Integer.parseInt(sc.nextLine());

                System.out.println("--- Identificacion del Pasajero ---");
                IdPersona idPas = leerIdPersona();

                Optional<String> pasajero = sistema.getNombrePasajero(idPas);

                if (pasajero.isEmpty()) {
                    System.out.println("Pasajero nuevo. Ingrese datos:");
                    Nombre nom = leerNombre();
                    System.out.print("Telefono pasajero: ");
                    String fono = sc.nextLine();

                    System.out.println("--- Contacto de Emergencia ---");
                    Nombre nomEmergencia = leerNombre();
                    System.out.print("Fono contacto: ");
                    String fc = sc.nextLine();

                    sistema.createPasajero(idPas, nom, fono, nomEmergencia, fc);
                }

                sistema.vendePasaje(idVentaActual, tipoVentaActual, fecha, hora, patBus, asiento, idPas);
            }

            Optional<Integer> monto = sistema.getMontoVenta(idVentaActual, tipoVentaActual);
            System.out.println("\n=================================");
            System.out.println("Monto total de la venta: $" + monto.get());
            System.out.println("=================================");

            System.out.println("\n--- PROCESAR PAGO DE LA VENTA ---");
            System.out.println("1. Efectivo");
            System.out.println("2. Tarjeta");
            int opPago = Integer.parseInt(sc.nextLine());

            if (opPago == 1) {
                sistema.pagaVenta(idVentaActual, tipoVentaActual);
            } else {
                System.out.print("Numero de tarjeta (sin espacios): ");
                long nroTarjeta = Long.parseLong(sc.nextLine());
                sistema.pagaVenta(idVentaActual, tipoVentaActual, nroTarjeta);
            }

            System.out.println("Venta procesada y pagada exitosamente.");

        } catch (SistemaVentaPasajesException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Formato de numero invalido.");
        }
    }

    private void listVentas() {
        String[][] datos = sistema.listVentas();
        System.out.println("\n--- VENTAS ---");
        System.out.printf("%-12s | %-8s | %-12s | %-15s | %-20s | %-5s | %-10s\n", "ID DOC", "TIPO", "FECHA", "ID CLIENTE", "NOMBRE CLIENTE", "CANT", "TOTAL");
        System.out.println("-----------------------------------------------------------------------------------------------------");
        for (int i = 0; i < datos.length; i++) {
            System.out.printf("%-12s | %-8s | %-12s | %-15s | %-20s | %-5s | %-10s\n",
                    datos[i][0], datos[i][1], datos[i][2], datos[i][3], datos[i][4], datos[i][5], datos[i][6]);
        }
    }

    private void listViajes() {
        String[][] datos = sistema.listViajes();
        System.out.println("\n--- VIAJES ---");
        System.out.printf("%-12s | %-8s | %-8s | %-10s | %-5s | %-8s | %-15s\n", "FECHA", "SALIDA", "LLEGADA", "PRECIO", "DISP", "PATENTE", "ORIGEN");
        System.out.println("---------------------------------------------------------------------------------------");
        for (int i = 0; i < datos.length; i++) {
            System.out.printf("%-12s | %-8s | %-8s | %-10s | %-5s | %-8s | %-15s\n",
                    datos[i][0], datos[i][1], datos[i][2], datos[i][3], datos[i][4], datos[i][5], datos[i][6]);
        }
    }

    private void listPasajerosViaje() {
        try {
            System.out.print("Fecha [dd/mm/yyyy]: ");

            String fechaTexto = sc.nextLine();

            String[] partesFecha = fechaTexto.split("/");

            LocalDate fecha = LocalDate.of(Integer.parseInt(partesFecha[2]), Integer.parseInt(partesFecha[1]), Integer.parseInt(partesFecha[0]));
            System.out.print("Hora [hh:mm]: ");
            LocalTime hora = LocalTime.parse(sc.nextLine());
            System.out.print("Patente bus: ");
            String pat = sc.nextLine();

            String[][] datos = sistema.listPasajerosViaje(fecha, hora, pat);
            System.out.println("\n--- PASAJEROS VIAJE ---");
            System.out.printf("%-15s | %-25s | %-20s | %-12s\n", "ID PASAJERO", "NOMBRE", "CONTACTO EMERG", "FONO CONT");
            System.out.println("---------------------------------------------------------------------------------");
            for (int i = 0; i < datos.length; i++) {
                System.out.printf("%-15s | %-25s | %-20s | %-12s\n", datos[i][0], datos[i][1], datos[i][2], datos[i][3]);
            }

        } catch (SistemaVentaPasajesException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listEmpresas() {
        String[][] datos = controlador.listEmpresas();
        System.out.printf("%-12s | %-20s | %-25s | %-10s | %-10s\n", "RUT EMPRESA", "NOMBRE", "URL", "NRO. TRIPULANTES", "NRO. BUSES");
        System.out.println("--------------------------------------------------------------------------------------");
        for (int i = 0; i < datos.length; i++) {
            System.out.printf("%-12s | %-20s | %-25s | %-10s | %-10s\n", datos[i][0], datos[i][1], datos[i][2], datos[i][3], datos[i][4]);
        }
    }

    private void listLlegadasSalidasTerminal() {
        try {
            System.out.print("Nombre terminal: ");
            String nombre = sc.nextLine();
            System.out.print("Fecha [dd/mm/yyyy]: ");

            String fechaTexto = sc.nextLine();
            String[] partesFecha = fechaTexto.split("/");
            LocalDate fecha = LocalDate.of(Integer.parseInt(partesFecha[2]), Integer.parseInt(partesFecha[1]), Integer.parseInt(partesFecha[0]));
            String[][] datos = controlador.listLlegadasSalidasTerminal(nombre, fecha);
            System.out.println("\n--- LLEGADAS/SALIDAS ---");
            System.out.printf("%-10s | %-8s | %-10s | %-20s | %-10s\n", "LLEGADA /SALIDA ", "HORA", "PATENTE BUS", "NOMBRE EMPRESA", "NRO PASAJEROS");
            System.out.println("-----------------------------------------------------------------------");
            for (int i = 0; i < datos.length; i++) {
                System.out.printf("%-10s | %-8s | %-10s | %-20s | %-10s\n", datos[i][0], datos[i][1], datos[i][2], datos[i][3], datos[i][4]);
            }

        } catch (SistemaVentaPasajesException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listVentasEmpresa() {
        try {
            System.out.print("Rut empresa: ");
            String rut = sc.nextLine();

            String[][] datos = controlador.listVentasEmpresa(Rut.of(rut));
            System.out.println("\n--- VENTAS EMPRESA ---");
            System.out.printf("%-12s | %-8s | %-10s | %-15s\n", "FECHA", "TIPO", "MONTO PAGADO", "TIPO PAGO");
            System.out.println("-----------------------------------------------------");
            for (int i = 0; i < datos.length; i++) {
                System.out.printf("%-12s | %-8s | $%-9s | %-15s\n", datos[i][0], datos[i][1], datos[i][2], datos[i][3]);
            }

        } catch (SistemaVentaPasajesException e) {
            System.out.println(e.getMessage());
        }
    }

    private void consultaViajesFecha() {
        System.out.print("Fecha [dd/mm/yyyy]: ");
        String fechaTexto = sc.nextLine();
        String[] partesFecha = fechaTexto.split("/");
        LocalDate fecha = LocalDate.of(Integer.parseInt(partesFecha[2]), Integer.parseInt(partesFecha[1]), Integer.parseInt(partesFecha[0]));
        System.out.print("Origen (comuna): ");
        String salida = sc.nextLine();
        System.out.print("Destino (comuna): ");
        String llegada = sc.nextLine();
        System.out.print("Pasajes a vender");
        System.out.print("Cantidad de pasajes: ");
        int pasajes = Integer.parseInt(sc.nextLine());

        String[][] datos = sistema.getHorariosDisponibles(fecha, salida, llegada, pasajes);

        System.out.println("\n--- VIAJES DISPONIBLES ---");
        if (datos.length > 0) {
            System.out.printf("%-10s | %-8s | %-10s | %-10s\n", "PATENTE", "SALIDA", "PRECIO", "DISPONIBLES");
            System.out.println("-------------------------------------------------------");
            for (int i = 0; i < datos.length; i++) {
                System.out.printf("%-10s | %-8s | $%-9s | %-10s\n", datos[i][0], datos[i][1], datos[i][2], datos[i][3]);
            }
        } else {
            System.out.println("No se encontraron viajes.");
        }
    }

    private IdPersona leerIdPersona() {
        System.out.print("Rut[1] o Pasaporte[2]: ");
        int tipo = Integer.parseInt(sc.nextLine());
        if (tipo == 1) {
            System.out.print("R.U.T: ");
            return Rut.of(sc.nextLine());
        } else {
            System.out.print("Pasaporte Nro: ");
            String nro = sc.nextLine();
            System.out.print("Nacionalidad: ");
            return Pasaporte.of(nro, sc.nextLine());
        }
    }

    private Nombre leerNombre() {
        System.out.print("Sr.[1] o Sra.[2]: ");
        Tratamiento t = (Integer.parseInt(sc.nextLine()) == 1) ? Tratamiento.SR : Tratamiento.SRA;
        System.out.print("Nombres: ");
        String nom = sc.nextLine();
        System.out.print("Apellido Paterno: ");
        String apeP = sc.nextLine();
        System.out.print("Apellido Materno: ");
        String apeM = sc.nextLine();
        return new Nombre(t, nom, apeP, apeM);
    }

    private Direccion leerDireccion() {
        System.out.print("Calle: ");
        String calle = sc.nextLine();
        System.out.print("Numero: ");
        String numero = sc.nextLine();
        System.out.print("Comuna: ");
        String comuna = sc.nextLine();
        return new Direccion(calle, numero, comuna);
    }
}
