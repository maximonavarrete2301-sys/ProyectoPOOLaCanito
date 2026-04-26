public class Rut implements IdPersona {
    private int numero;
    private char dv;

    public Rut(int num, char dv) {
        this.numero = num;
        this.dv = dv;
    }

    public int getNumero() {
        return numero;
    }

    public char getDv() {
        return dv;
    }

    public static Rut of(String rutConDv) {
        int num = 0;
        int i = 0;
        while (i < rutConDv.length() && rutConDv.charAt(i) != '-') {
            char c = rutConDv.charAt(i);
            if (c >= '0' && c <= '9') {
                num = num * 10 + (c - '0');
            }
            i++;
        }
        char dv = rutConDv.charAt(rutConDv.length() - 1);
        return new Rut(num, dv);
    }

    @Override
    public String toString() {
        return numero + "-" + dv;
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Rut)) return false;
        Rut that = (Rut) otro;
        return this.numero == that.numero && this.dv == that.dv;
    }
}
