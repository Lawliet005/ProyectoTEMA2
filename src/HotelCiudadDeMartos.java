import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class HotelCiudadDeMartos {
    private final List<Habitacion> habitaciones;
    private final List<Map<String, Object>> reservasFinalizadas; // Histórico de facturas
    private final Map<Double, Integer> caja; // Denominación (en €) -> Cantidad
    private final String ADMIN_USER = "admin";
    private final String ADMIN_PASS = "1234";

    public HotelCiudadDeMartos() {
        this.habitaciones = new ArrayList<>();
        this.reservasFinalizadas = new ArrayList<>();
        this.caja = new LinkedHashMap<>(); // Mantiene el orden
        inicializarHabitaciones();
        inicializarCaja();
    }

    // --- Inicialización ---

    private void inicializarHabitaciones() {
        // 8 Dobles (60€)
        for (int i = 1; i <= 8; i++) {
            habitaciones.add(new Habitacion(i, "Doble", 60.00));
        }
        // 2 Individuales (45€)
        for (int i = 9; i <= 10; i++) {
            habitaciones.add(new Habitacion(i, "Individual", 45.00));
        }
    }

    private void inicializarCaja() {
        // Monedas y billetes comunes (ejemplo de stock inicial)
        caja.put(500.0, 0); // 500€
        caja.put(200.0, 0); // 200€
        caja.put(100.0, 5); // 100€
        caja.put(50.0, 10); // 50€
        caja.put(20.0, 20); // 20€
        caja.put(10.0, 50); // 10€
        caja.put(5.0, 100); // 5€
        caja.put(2.0, 50); // 2€ (Moneda)
        caja.put(1.0, 100); // 1€ (Moneda)
        caja.put(0.5, 50); // 0.50€
        caja.put(0.2, 50); // 0.20€
        caja.put(0.1, 50); // 0.10€
        caja.put(0.05, 50); // 0.05€
        caja.put(0.02, 50); // 0.02€
        caja.put(0.01, 50); // 0.01€
    }

    // --- Opciones del Menú Principal ---

    // a. Ver el estado de ocupación
    public void verEstadoOcupacion() {
        System.out.println("\n--- ESTADO DE OCUPACIÓN ---");
        for (Habitacion h : habitaciones) {
            String estado = h.isOcupada() ?
                    String.format("OCUPADA (Cliente: %s, Entrada: %s)", h.getClienteNombre(), h.getFechaEntrada()) :
                    "LIBRE";
            System.out.printf("Hab. %d (%s, %.2f€/noche): %s\n", h.getNumero(), h.getTipo(), h.getTarifa(), estado);
        }
    }

    // b. Reservar una habitación
    public void reservarHabitacion(Scanner scanner) {
        System.out.println("\n--- NUEVA RESERVA ---");
        System.out.print("¿Qué tipo de habitación desea (Doble/Individual)? ");
        String tipoDeseado = scanner.nextLine().trim();

        if (!tipoDeseado.equalsIgnoreCase("Doble") && !tipoDeseado.equalsIgnoreCase("Individual")) {
            System.out.println("❌ Tipo de habitación no válido.");
            return;
        }

        Habitacion habDisponible = null;
        for (Habitacion h : habitaciones) {
            if (!h.isOcupada() && h.getTipo().equalsIgnoreCase(tipoDeseado)) {
                habDisponible = h;
                break;
            }
        }

        if (habDisponible == null) {
            System.out.println("⚠️ Lo sentimos, no hay habitaciones " + tipoDeseado.toLowerCase() + " disponibles.");
            return;
        }

        System.out.printf("✅ Habitación %d (%s) disponible. Tarifa: %.2f€/noche.\n",
                habDisponible.getNumero(), habDisponible.getTipo(), habDisponible.getTarifa());

        System.out.print("Nombre del cliente: ");
        String nombre = scanner.nextLine();

        int pax = 0;
        if (tipoDeseado.equalsIgnoreCase("Doble")) {
            System.out.print("Número de huéspedes (1 o 2): ");
            pax = Integer.parseInt(scanner.nextLine());
            if (pax < 1 || pax > 2) {
                System.out.println("❌ Número de huéspedes inválido para una habitación doble.");
                return;
            }
        } else { // Individual
            pax = 1;
        }

        String confir = LocalDate.now().toString().replace("-", "") + "-" + habDisponible.getNumero();

        habDisponible.reservar(nombre, pax, confir);
        System.out.println("🎉 ¡Reserva realizada con éxito!");
        System.out.printf("Habitación: %d | Confirmación: %s | Entrada: %s\n",
                habDisponible.getNumero(), confir, habDisponible.getFechaEntrada());
    }

    // c. Realizar el checkout de una habitación
    public void realizarCheckout(Scanner scanner) {
        System.out.println("\n--- CHECKOUT ---");
        System.out.print("Introduzca el número de habitación para el checkout: ");
        int numHab;
        try {
            numHab = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Número de habitación no válido.");
            return;
        }

        if (numHab < 1 || numHab > habitaciones.size()) {
            System.out.println("❌ Habitación inexistente.");
            return;
        }

        Habitacion habitacion = habitaciones.get(numHab - 1);
        if (!habitacion.isOcupada()) {
            System.out.println("⚠️ La habitación " + numHab + " está libre. No se puede hacer checkout.");
            return;
        }

        // Simular día de salida
        System.out.printf("Día de entrada: %s. Introduzca el día de salida (AAAA-MM-DD): ", habitacion.getFechaEntrada());
        LocalDate fechaSalida;
        try {
            fechaSalida = LocalDate.parse(scanner.nextLine());
            if (fechaSalida.isBefore(habitacion.getFechaEntrada()) || fechaSalida.isEqual(habitacion.getFechaEntrada())) {
                System.out.println("❌ La fecha de salida debe ser posterior a la de entrada.");
                return;
            }
        } catch (Exception e) {
            System.out.println("❌ Formato de fecha no válido. Use AAAA-MM-DD.");
            return;
        }

        // Cálculo de la Factura
        long noches = ChronoUnit.DAYS.between(habitacion.getFechaEntrada(), fechaSalida);
        double tarifa = habitacion.getTarifa();
        double monto = noches * tarifa;
        double iva = monto * 0.21;
        double total = monto + iva;

        // Generar y mostrar Factura
        Map<String, Object> factura = new LinkedHashMap<>();
        factura.put("Descripción", habitacion.getClienteNombre());
        factura.put("Confir", habitacion.getNumConfirmacion());
        factura.put("Entrada", habitacion.getFechaEntrada());
        factura.put("Salida", fechaSalida);
        factura.put("Pax", habitacion.getPax());
        factura.put("Noches", (int) noches);
        factura.put("Tarifa", String.format("%.2f€", tarifa));
        factura.put("Monto (SIN IVA)", String.format("%.2f€", monto));
        factura.put("Subtotal", String.format("%.2f€", monto));
        factura.put("IVA (21%)", String.format("%.2f€", iva));
        factura.put("TOTAL", String.format("%.2f€", total));

        System.out.println("\n--- FACTURA HABITACIÓN " + numHab + " ---");
        factura.forEach((k, v) -> System.out.println(String.format("%-18s: %s", k, v)));
        System.out.println("--------------------------------------");

        // Proceso de Pago
        System.out.print("TOTAL A PAGAR: " + String.format("%.2f€", total) + ". Introduzca el dinero entregado (solo contado): ");
        double dineroEntregado;
        try {
            dineroEntregado = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Dinero entregado no válido.");
            return;
        }

        if (dineroEntregado < total) {
            System.out.println("❌ El dinero es insuficiente. La operación ha sido cancelada.");
            return;
        }

        double cambio = dineroEntregado - total;
        System.out.println("Dinero entregado: " + String.format("%.2f€", dineroEntregado));
        System.out.println("Cambio a devolver: " + String.format("%.2f€", cambio));

        // Gestión de Cambio
        if (cambio > 0.01) {
            System.out.println("\nDevolviendo cambio:");
            Map<Double, Integer> cambioDevuelto = calcularCambio(cambio);
            if (cambioDevuelto == null) {
                System.out.println("⚠️ ¡ADVERTENCIA! No hay suficientes monedas/billetes para dar el cambio exacto. Proceso de pago abortado.");
                return;
            }
            cambioDevuelto.forEach((den, cant) -> {
                if (cant > 0) System.out.printf("  - %d x %.2f€\n", cant, den);
            });
        }

        // Finalizar y almacenar
        reservasFinalizadas.add(factura);
        habitacion.checkout();
        System.out.println("\n✅ Checkout completado. Habitación " + numHab + " LIBRE.");
    }

    // --- Lógica Auxiliar de Cambio (Requisito 6) ---

    private Map<Double, Integer> calcularCambio(double cambio) {
        // Usar un TreeMap con Comparator inverso para empezar por la denominación más grande
        List<Double> denominaciones = new ArrayList<>(caja.keySet());
        denominaciones.sort(Collections.reverseOrder());

        double restante = cambio;
        Map<Double, Integer> cambioADevolver = new LinkedHashMap<>();
        Map<Double, Integer> cajaTemporal = new HashMap<>(caja); // Copia para simular

        // Asegurar la precisión con un factor multiplicador
        long restanteCents = Math.round(restante * 100);

        for (double den : denominaciones) {
            long denCents = Math.round(den * 100);
            if (denCents == 0) continue; // Evitar divisiones por cero

            int disponibles = cajaTemporal.getOrDefault(den, 0);
            int cantidad = 0;

            // Calcular cuántas unidades de esta denominación necesitamos/podemos dar
            if (restanteCents >= denCents) {
                cantidad = (int) (restanteCents / denCents);
                if (cantidad > disponibles) {
                    cantidad = disponibles; // No podemos dar más de lo que tenemos
                }
            }

            if (cantidad > 0) {
                cambioADevolver.put(den, cantidad);
                restanteCents -= (long) cantidad * denCents;
                cajaTemporal.put(den, disponibles - cantidad);
            }
        }

        if (restanteCents > 1) { // Si queda más de 1 céntimo (por seguridad de redondeo)
            return null; // Fallo en el cambio (monedas insuficientes)
        }

        // Si el cambio es exitoso, actualizamos la caja real
        this.caja.clear();
        this.caja.putAll(cajaTemporal);
        return cambioADevolver;
    }

    // --- Menú de Administrador ---

    public boolean accesoAdmin(Scanner scanner) {
        System.out.println("\n--- ACCESO DE ADMINISTRADOR ---");
        System.out.print("Usuario: ");
        String user = scanner.nextLine();
        System.out.print("Contraseña: ");
        String pass = scanner.nextLine();

        if (user.equals(ADMIN_USER) && pass.equals(ADMIN_PASS)) {
            return true;
        } else {
            System.out.println("❌ Acceso denegado. Credenciales incorrectas.");
            return false;
        }
    }

    // i. Consultar ingresos totales y reservas finalizadas
    public void consultarIngresos() {
        double ingresosTotales = 0.0;
        int numReservas = reservasFinalizadas.size();

        for (Map<String, Object> factura : reservasFinalizadas) {
            // Extraer el String y parsear el valor TOTAL
            String totalStr = (String) factura.get("TOTAL");
            if (totalStr != null) {
                try {
                    ingresosTotales += Double.parseDouble(totalStr.replace("€", ""));
                } catch (NumberFormatException ignored) {}
            }
        }

        System.out.println("\n--- INFORME FINANCIERO ---");
        System.out.printf("Ingresos Totales (incl. IVA): %.2f€\n", ingresosTotales);
        System.out.printf("Número de reservas finalizadas: %d\n", numReservas);
    }

    // ii. Consultar las monedas restantes para el cambio
    public void consultarMonedas() {
        System.out.println("\n--- CAJA DISPONIBLE PARA CAMBIO ---");
        caja.forEach((den, cant) -> System.out.printf("%s%-7s: %d unidades\n",
                den >= 5.0 ? "Billetes" : "Monedas",
                String.format("%.2f€", den),
                cant));
    }
}