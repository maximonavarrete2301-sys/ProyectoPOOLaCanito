package vista.gui;

import utilidades.Nombre;
import utilidades.Tratamiento;

import javax.swing.*;
import java.awt.*;

public final class DialogosDatos {

    private DialogosDatos() {}

    public static Nombre pedirNombre(Component padre, String titulo) {
        JComboBox<Tratamiento> cmbTratamiento = new JComboBox<>(Tratamiento.values());
        JTextField txtNombres = new JTextField(18);
        JTextField txtPaterno = new JTextField(18);
        JTextField txtMaterno = new JTextField(18);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(GUIUtil.fila("Tratamiento:", cmbTratamiento));
        panel.add(GUIUtil.fila("Nombres:", txtNombres));
        panel.add(GUIUtil.fila("Apellido paterno:", txtPaterno));
        panel.add(GUIUtil.fila("Apellido materno:", txtMaterno));

        int opcion = JOptionPane.showConfirmDialog(padre, panel, titulo, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return null;
        }

        if (txtNombres.getText().trim().isEmpty() || txtPaterno.getText().trim().isEmpty() || txtMaterno.getText().trim().isEmpty()) {
            GUIUtil.error(padre, "Debe completar todos los datos del nombre.");
            return null;
        }

        return new Nombre(
                (Tratamiento) cmbTratamiento.getSelectedItem(),
                txtNombres.getText().trim(),
                txtPaterno.getText().trim(),
                txtMaterno.getText().trim()
        );
    }
}
