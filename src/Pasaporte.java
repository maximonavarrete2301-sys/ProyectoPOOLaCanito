public class Pasaporte{
    private String numero;
    private String nacionalidad;

    //secuencia alfanumerica

    private Pasaporte(String num, String nacionalidad){
        this.nacionalidad = nacionalidad;
        this.numero = num;
    }
    public String getNumero(){
        return numero;
    }

    public String getNacionalidad(){
        return nacionalidad;
    }

   /* public Pasaporte of(String num, String nacionalidad){

    }
*/
}
