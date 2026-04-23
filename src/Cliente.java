public class Cliente extends SistemaVentaPasajes {
    private String email;

    public Cliente(IdPersona id, Nombre nom, String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addVenta(Venta venta) {
    }

    public Venta[] getVentas() {
        return null;
    }
}