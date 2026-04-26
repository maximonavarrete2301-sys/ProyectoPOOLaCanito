public class Persona {
    private IdPersona idPersona;
    private Nombre nombreCompleto;
    private String telefono;

    public Persona(IdPersona idPersona, Nombre nombreCompleto) {
        this.idPersona=idPersona;
        this.nombreCompleto=nombreCompleto;
    }

    public IdPersona getIdPersona() {
        return idPersona;
    }

    public Nombre getNombreCompleto() {
        return nombreCompleto;
    }
    public void setNombreCompleto(Nombre nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return idPersona.toString() + " , " + nombreCompleto.toString() + " , " + telefono;
    }

    @Override
    public boolean equals(Object otro) {
        if (otro instanceof Persona) {
            Persona p = (Persona) otro;
            return this.idPersona.equals(p.idPersona);
        }
        return false;
    }
}