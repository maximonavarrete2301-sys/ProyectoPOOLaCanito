package vista.gui;

public class ComboItem {
    private final String valor;
    private final String texto;

    public ComboItem(String valor, String texto) {
        this.valor = valor;
        this.texto = texto;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return texto;
    }
}
