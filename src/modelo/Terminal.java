//Maximo Navarrete Fernandez
package modelo;

import utilidades.Direccion;

import java.util.ArrayList;

public class Terminal {
    private String nombre;
    private Direccion direccion;
    private ArrayList<Viaje> llegadas;
    private ArrayList<Viaje> salidas;

    public Terminal (String nombre , Direccion direccion) {
        this.nombre=nombre;
        this.direccion=direccion;
        llegadas=new ArrayList<>();
        salidas=new ArrayList<>();
    }
    public String getNombre() {
        return nombre;
    }
    public Direccion getDireccion(){
        return direccion;
    }
    public void setDireccion( Direccion direccion){
        this.direccion=direccion;
    }
    public void addLlegada(Viaje viaje){
        llegadas.add(viaje);
    }
    public void addSalida( Viaje viaje) {
        salidas.add(viaje);
    }
    public Viaje [] getLlegadas() {
        Viaje[] arreglo = new Viaje[llegadas.size()];
        for (int i = 0; i < llegadas.size(); i++) {
            arreglo[i] = llegadas.get(i);
        }
        return arreglo;
    }
    public Viaje[] getSalidas() {
        Viaje[] arreglo = new Viaje[salidas.size()];
        for (int i=0; i<salidas.size(); i++) {
            arreglo[i] = salidas.get(i);
        }
        return arreglo;
    }
}

