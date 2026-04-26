import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static SistemaVentaPasajes sistema = new SistemaVentaPasajes();
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        menu();
    }

    private static void menu() {
        int opcion;
        do {
            System.out.println("\n============================");
            System.out.println("...::: Menú principal :::...");
            System.out.println("1) Crear cliente");
            System.out.println("2) Crear bus");
            System.out.println("3) Crear viaje");
            System.out.println("4) Vender pasaje");
            System.out.println("5) Lista de pasajeros");
            System.out.println("6) Lista de ventas");
            System.out.println("7) Lista de viajes");
            System.out.println("8) Consulta Viajes disponibles por fecha");
            System.out.println("9) Salir");
            System.out.println("-----------------------------");
            System.out.print("..:: Ingrese número de opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1: createCliente(); break;
                case 2: createBus(); break;
                case 3: createViaje(); break;
                case 4: vendePasajes(); break;
                case 5: listPasajerosViaje(); break;
                case 6: listVentas(); break;
                case 7: listViajes(); break;
                case 8: consultaViajesPorFecha(); break;
                case 9: System.out.println("Saliendo del sistema..."); break;
                default: System.out.println("Opción no válida");
            }
        } while (opcion != 9);
    }

    private static void createCliente() {
        System.out.println("\n...:::: Crear un nuevo Cliente ::::....");
        IdPersona id = leerIdPersona();
        Nombre nombre = leerNombre();
        System.out.print("Teléfono móvil: ");
        String fono = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();

        if (sistema.createCliente(id, nombre, fono, email)) {
            System.out.println(".:::: Cliente guardado exitosamente ::::...");
        } else {
            System.out.println("Error: Ya existe un cliente con ese ID.");
        }
    }

    private static void createBus() {
        System.out.println("\n...:::: Creacion de un nuevo bus ::::..");
        System.out.print("Patente: ");
        String patente = sc.nextLine();
        System.out.print("Marca: ");
        String marca = sc.nextLine();
        System.out.print("Modelo: ");
        String modelo = sc.nextLine();
        System.out.print("Numero de asientos: ");
        int asientos = sc.nextInt();
        sc.nextLine();

        if (sistema.createBus(patente, marca, modelo, asientos)) {
            System.out.println("...:::: Bus guardado exitosamente ::::....");
        } else {
            System.out.println("Error: Patente duplicada.");
        }
    }

    private static void createViaje() {
        System.out.println("\n...:::: Creacion de un nuevo viaje ::::....");
        System.out.print("Fecha [dd/mm/yyyy]: ");
        LocalDate fecha = LocalDate.parse(sc.nextLine(), dateFormatter);
        System.out.print("Hora [hh:mm]: ");
        LocalTime hora = LocalTime.parse(sc.nextLine(), timeFormatter);
        System.out.print("Precio: ");
        int precio = sc.nextInt();
        sc.nextLine();
        System.out.print("Patente Bus: ");
        String patente = sc.nextLine();

        if (sistema.createViaje(fecha, hora, precio, patente)) {
            System.out.println("..:::: Viaje guardado exitosamente ::::....");
        } else {
            System.out.println("Error: El bus no existe o el horario está ocupado.");
        }
    }

    private static void vendePasajes() {
        System.out.println("\n...:::: Venta de pasajes ::::....");
        System.out.print("ID Documento: ");
        String idDoc = sc.nextLine();
        System.out.print("Tipo documento: [1] Boleta [2] Factura: ");
        TipoDocumento tipo = (sc.nextInt() == 1) ? TipoDocumento.BOLETA : TipoDocumento.FACTURA;
        sc.nextLine();
        System.out.print("Fecha de venta [dd/mm/yyyy]: ");
        LocalDate fechaVenta = LocalDate.parse(sc.nextLine(), dateFormatter);

        System.out.println(":::: Datos del cliente");
        IdPersona idCli = leerIdPersona();

        if (!sistema.iniciaVenta(idDoc, tipo, fechaVenta, idCli)) {
            System.out.println("\nLa venta no se puede hacer, Intentelo mas tarde.");
            return;
        }

        System.out.print("\nIngrese cantidad de pasajes: ");
        int cantidad = sc.nextInt();
        sc.nextLine();
        System.out.print("Fecha del viaje [dd/mm/yyyy]: ");
        LocalDate fViaje = LocalDate.parse(sc.nextLine(), dateFormatter);

        String[][] disp = sistema.getHorariosDisponibles(fViaje);
        if (disp.length == 0) {
            System.out.println("No existen viajes para esa fecha. Venta terminada.");
            return;
        }

        System.out.println("\n--- Viajes Disponibles ---");
        System.out.printf("%-10s | %-8s | %-10s | %-10s\n", "PATENTE", "HORA", "PRECIO", "DISPONIBLES");
        for (String[] v : disp) {
            System.out.printf("%-10s | %-8s | $%-9s | %-10s\n", v[0], v[1], v[2], v[3]);
        }

        System.out.print("\nIngrese patente del bus: ");
        String pat = sc.nextLine();
        System.out.print("Ingrese hora [hh:mm]: ");
        LocalTime hor = LocalTime.parse(sc.nextLine(), timeFormatter);

        String[] seats = sistema.listAsientosDeViaje(fViaje, hor, pat);
        if (seats.length == 0) {
            System.out.println("Error: Viaje no encontrado.");
            return;
        }

        int disponibles = 0;
        for(String s : seats) if(s.equals("Libre")) disponibles++;
        if (cantidad > disponibles) {
            System.out.println("No hay asientos suficientes (" + disponibles + " disponibles). Venta terminada.");
            return;
        }

        System.out.println("\n--- Mapa de Asientos ---");
        for (int i = 0; i < seats.length; i++) {
            String estado = seats[i].equals("Libre") ? String.valueOf(i + 1) : "*";
            System.out.printf("[%2s] ", estado);
            if ((i + 1) % 4 == 0) System.out.println();
        }
        System.out.println();

        int[] elegidos = new int[cantidad];
        IdPersona[] pids = new IdPersona[cantidad];

        for (int i = 0; i < cantidad; i++) {
            System.out.println("\n--- Pasaje " + (i + 1) + " de " + cantidad + " ---");
            System.out.print("Número de asiento libre: ");
            int n = sc.nextInt();
            sc.nextLine();

            while (n < 1 || n > seats.length || !seats[n - 1].equals("Libre")) {
                System.out.print("Invalido u ocupado. Intente otro: ");
                n = sc.nextInt();
                sc.nextLine();
            }

            elegidos[i] = n;
            seats[n - 1] = "Ocupado";

            System.out.println(":::: Identificación del Pasajero");
            IdPersona pid = leerIdPersona();
            if (sistema.getNombrePasajero(pid) == null) {
                System.out.println("Pasajero nuevo. Ingrese datos:");
                Nombre nP = leerNombre();
                System.out.print("Fono: ");
                String fP = sc.nextLine();
                System.out.println(":::: Contacto Emergencia");
                Nombre nC = leerNombre();
                System.out.print("Fono Contacto: ");
                String fC = sc.nextLine();
                sistema.createPasajero(pid, nP, fP, nC, fC);
            }
            pids[i] = pid;
            sistema.vendePasaje(idDoc, fViaje, hor, pat, n, pid);
        }

        System.out.println("\n=================================");
        System.out.println("TOTAL A PAGAR: $" + sistema.getMontoVenta(idDoc, tipo));
        System.out.println("=================================");

        System.out.println("\n...:::: BOLETOS GENERADOS ::::...");
        for (int i = 0; i < cantidad; i++) {
            System.out.println("---------------------------------");
            System.out.println("Boleto N° " + (i + 1));
            System.out.println("Viaje: " + fViaje.format(dateFormatter) + " " + hor.format(timeFormatter));
            System.out.println("Asiento: " + elegidos[i] + " | Bus: " + pat);
            System.out.println("Pasajero: " + sistema.getNombrePasajero(pids[i]));
            System.out.println("---------------------------------");
        }
    }

    private static void listVentas() {
        System.out.println("\n...:::: Listado de ventas ::::..");
        String[][] datos = sistema.listVentas();
        if (datos.length == 0) {
            System.out.println("No hay ventas registradas.");
            return;
        }
        System.out.printf("%-12s | %-8s | %-12s | %-15s | %-5s | %-10s\n", "ID DOC", "TIPO", "FECHA", "CLIENTE", "CANT", "TOTAL");
        System.out.println("-------------------------------------------------------------------------------");
        for (String[] fila : datos) {
            System.out.printf("%-12s | %-8s | %-12s | %-15s | %-5s | %-10s\n", fila[0], fila[1], fila[2], fila[3], fila[5], fila[6]);
        }
    }

    private static void listViajes() {
        System.out.println("\n...:::: Listado de viajes ::::....");
        String[][] datos = sistema.listViajes();
        if (datos.length == 0) {
            System.out.println("No hay viajes registrados.");
            return;
        }
        System.out.printf("%-12s | %-8s | %-10s | %-10s | %-10s\n", "FECHA", "HORA", "PRECIO", "DISP", "PATENTE");
        System.out.println("-----------------------------------------------------------------");
        for (String[] fila : datos) {
            System.out.printf("%-12s | %-8s | %-10s | %-10s | %-10s\n", fila[0], fila[1], fila[2], fila[3], fila[4]);
        }
    }

    private static void listPasajerosViaje() {
        System.out.print("Fecha [dd/mm/yyyy]: ");
        LocalDate f = LocalDate.parse(sc.nextLine(), dateFormatter);
        System.out.print("Hora [hh:mm]: ");
        LocalTime h = LocalTime.parse(sc.nextLine(), timeFormatter);
        System.out.print("Patente: ");
        String p = sc.nextLine();

        String[][] lista = sistema.listPasajeros(f, h, p);
        if (lista.length == 0) {
            System.out.println("No hay pasajeros en este viaje.");
        } else {
            System.out.printf("%-15s | %-25s | %-20s | %-12s\n", "ID", "NOMBRE", "CONTACTO", "FONO CONT");
            System.out.println("-------------------------------------------------------------------------------");
            for (String[] fila : lista) {
                System.out.printf("%-15s | %-25s | %-20s | %-12s\n", fila[0], fila[1], fila[2], fila[3]);
            }
        }
    }

    private static void consultaViajesPorFecha() {
        System.out.println("\n...:::: Consulta Viajes disponibles por fecha ::::....");
        System.out.print("Ingrese la fecha [dd/mm/yyyy]: ");
        LocalDate fecha = LocalDate.parse(sc.nextLine(), dateFormatter);
        String[][] disp = sistema.getHorariosDisponibles(fecha);

        System.out.println("\nViajes encontrados: " + disp.length);
        if (disp.length > 0) {
            System.out.printf("%-10s | %-8s | %-10s | %-10s\n", "PATENTE", "SALIDA", "PRECIO", "DISPONIBLES");
            System.out.println("-------------------------------------------------------");
            for (String[] fila : disp) {
                System.out.printf("%-10s | %-8s | $%-9s | %-10s\n", fila[0], fila[1], fila[2], fila[3]);
            }
        }
    }

    private static IdPersona leerIdPersona() {
        System.out.print("Rut[1] o Pasaporte[2]: ");
        int tipo = sc.nextInt();
        sc.nextLine();
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

    private static Nombre leerNombre() {
        System.out.print("Sr.[1] o Sra.[2]: ");
        Tratamiento t = (sc.nextInt() == 1) ? Tratamiento.SR : Tratamiento.SRA;
        sc.nextLine();
        System.out.print("Nombres: ");
        String nom = sc.nextLine();
        System.out.print("Apellido Paterno: ");
        String apeP = sc.nextLine();
        System.out.print("Apellido Materno: ");
        String apeM = sc.nextLine();
        return new Nombre(t, nom, apeP, apeM);
    }
}