public class Persona{
    private IdPersona idPersona ;
    private Nombre nombreCompleto ;
    private String telefono ;
}
public Persona (IdPersona id , Nombre nombre ) {
    this.IdPersona = id ;
    this.nombreCompleto = nombre ;
}
public IdPersona getIdPersona() {
    return idPersona ;
}
public Nombre getNombreCompleto() {
    return nombreCompleto ;
}
public void setNombreCompleto(Nombre nombreCompleto) {
    this.nombreCompleto = NombreCompleto ;
}
public String getTelefono() {
    return telefono ;
}
public void setTelefono (String telefono ) {
    this.telefono = telefono;
}
public String toString() {
    return idPersona.toString() + nombreCompleto.toString() +"," telefono.toString() ;
}
//despues termino boolean//
public boolean equals (Object otro) {
    if (this == otro)
        return true;
    }
}

