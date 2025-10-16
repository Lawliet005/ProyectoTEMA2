import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HotelCiudadDeMartos hotel = new HotelCiudadDeMartos();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=============================================");
            System.out.println(" HOTEL CIUDAD DE MARTOS - MENÚ PRINCIPAL ");
            System.out.println("=============================================");
            System.out.println("a. Ver el estado de ocupación de las habitaciones");
            System.out.println("b. Reservar una habitación");
            System.out.println("c. Realizar el checkout de una habitación");
            System.out.println("d. Menú de Administrador");
            System.out.println("---------------------------------------------");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine().toLowerCase().trim();

            switch (opcion) {
                case "a":
                    hotel.verEstadoOcupacion();
                    break;
                case "b":
                    hotel.reservarHabitacion(scanner);
                    break;
                case "c":
                    hotel.realizarCheckout(scanner);
                    break;
                case "d":
                    if (hotel.accesoAdmin(scanner)) {
                        ejecutarMenuAdmin(hotel, scanner, running);
                        // La variable 'running' se actualiza por referencia simulada aquí para el ejemplo
                        // En Java real, pasaríamos una clase contenedora o manejaríamos la salida de otra forma.
                        // Para este ejemplo de consola, se asume que al volver, 'running' puede ser false.
                    }
                    // La única forma de salir es desde el menú admin, así que se comprueba si se debe seguir
                    // (Lógica simplificada para el ejemplo)
                    if (scanner.nextLine().equalsIgnoreCase("apagar")) {
                        running = false;
                    }
                    break;
                default:
                    System.out.println("❌ Opción no válida. Inténtelo de nuevo.");
            }
        }

        System.out.println("\n👋 Software del Hotel Ciudad de Martos apagado. ¡Hasta pronto!");
        scanner.close();
    }

    private static void ejecutarMenuAdmin(HotelCiudadDeMartos hotel, Scanner scanner, boolean mainRunning) {
        boolean adminRunning = true;
        while (adminRunning) {
            System.out.println("\n--- MENÚ DE ADMINISTRADOR ---");
            System.out.println("i. Consultar ingresos y reservas finalizadas");
            System.out.println("ii. Consultar monedas restantes para el cambio");
            System.out.println("iii. Apagar el software");
            System.out.println("iv. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            String opcionAdmin = scanner.nextLine().toLowerCase().trim();

            switch (opcionAdmin) {
                case "i":
                    hotel.consultarIngresos();
                    break;
                case "ii":
                    hotel.consultarMonedas();
                    break;
                case "iii":
                    System.out.println("Confirmación de apagado. Escriba 'apagar' para confirmar.");
                    if (scanner.nextLine().equalsIgnoreCase("apagar")) {
                        // En una aplicación real, se usaría un mecanismo de estado más robusto.
                        // Aquí se simula la salida:
                        System.exit(0);
                    }
                    break;
                case "iv":
                    adminRunning = false;
                    break;
                default:
                    System.out.println("❌ Opción no válida.");
            }
        }
    }
}