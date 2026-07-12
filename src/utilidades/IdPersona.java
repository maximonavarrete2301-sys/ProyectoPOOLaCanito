//Rodrigo Henriquez
package utilidades;
import java.io.Serializable;
public interface IdPersona extends Serializable {
    @Override
    String toString();

    @Override
    boolean equals(Object otro);
}
