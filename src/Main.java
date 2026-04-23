import java.util.*;
public class Main{
  private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        menu();
    }

    private static void menu() {
        boolean salir = false;
        while (!salir) {
            System.out.println("============================");
            System.out.println("...::: Menú principal :::...");
            System.out.println("");
            System.out.println("1) Crear cliente");
            System.out.println("2) Crear bus");
            System.out.println("3) Crear viaje");
            System.out.println("4) Vender pasajes");
            System.out.println("5) Lista de pasajeros");
            System.out.println("6) Lista de ventas");
            System.out.println("7) Lista de viajes");
            System.out.println("8) Salir");
            System.out.println("-----------------------------");
            System.out.println("..:: Ingrese número de opción: ");

            int opcion = sc.nextInt();
            switch (opcion) {
                case 1:
                    createCliente();
                    break;
                case 2:
                    createBus();
                    break;
                case 3:
                    createViaje();
                    break;
                case 4:
                    venderPasajes();
                    break;
                case 5:
                    listPasajerosViaje();
                    break;
                case 6:
                    listVentas();
                    break;
                case 7:
                    listViajes();
                    break;
                case 8:
                    salir = true;
                    break;
                default:
                    System.out.println("Opcion no valida");
            }
        }
    }

    private static void createCliente(){
        System.out.println("Creando cliente...");
    }
    private static void createBus(){
        System.out.println("Creando bus...");
    }
    private static void createViaje(){
        System.out.println("Creando viaje...");
    }
    private static void venderPasajes(){
        System.out.println("Vendiendo pasaje...");
    }
    private static void listPasajerosViaje(){
        System.out.println("Lista de pasajeros...");
    }
    private static void listVentas(){
        System.out.println("Lista de ventas...");
    }
    private static void listViajes(){
        System.out.println("Lista de viajes...");
    }
}