import java.time.LocalDate;

public class Habitacion {
    private final int numero;
    private final String tipo; // "Doble" o "Individual"
    private final double tarifa; // 60.00 o 45.00
    private boolean ocupada;

    // Detalles de la reserva
    private String clienteNombre;
    private int pax; // Huéspedes (1 o 2)
    private String numConfirmacion;
    private LocalDate fechaEntrada;

    // Constructor
    public Habitacion(int numero, String tipo, double tarifa) {
        this.numero = numero;
        this.tipo = tipo;
        this.tarifa = tarifa;
        this.ocupada = false;
    }

    // Método para reservar la habitación
    public void reservar(String clienteNombre, int pax, String numConfirmacion) {
        this.clienteNombre = clienteNombre;
        this.pax = pax;
        this.numConfirmacion = numConfirmacion;
        this.fechaEntrada = LocalDate.now(); // Día de entrada automático
        this.ocupada = true;
    }

    // Método para liberar la habitación después del checkout
    public void checkout() {
        this.ocupada = false;
        this.clienteNombre = null;
        this.pax = 0;
        this.numConfirmacion = null;
        this.fechaEntrada = null;
    }

    // Getters
    public int getNumero() { return numero; }
    public String getTipo() { return tipo; }
    public double getTarifa() { return tarifa; }
    public boolean isOcupada() { return ocupada; }
    public String getClienteNombre() { return clienteNombre; }
    public int getPax() { return pax; }
    public String getNumConfirmacion() { return numConfirmacion; }
    public LocalDate getFechaEntrada() { return fechaEntrada; }
}