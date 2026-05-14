public class Direccion {
    private String calle ;
    private int numero;
    private String comuna;

    public Direccion(String calle, int numero, String comuna){

        this.calle=calle;
        this.numero=numero;
        this.comuna=comuna;


    }

    public String getCalle(){

        return calle;
    }

   public int getNumero(){
        return numero;
   }


    public String getComuna(){

        return  comuna;
    }

    @Override
    public String toString(){

        return  "calle: " + calle + "numero " + numero +  "comuna : " + comuna;
    }

    @Override
    public boolean equals(Object otro ){
        Direccion d = (Direccion) otro;

        return calle.equals(d.calle) && numero == d.numero && comuna.equals(d.comuna);

    }
}
