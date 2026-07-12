package vista.gui;

import utilidades.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class GUIUtil {

    private static final DateTimeFormatter[] FORMATOS_FECHA = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ISO_LOCAL_DATE
    };

    private GUIUtil() {}

    public static LocalDate leerFecha(Component padre, JTextField campo, String nombreCampo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            error(padre, "Debe ingresar " + nombreCampo + ".");
            return null;
        }

        for (DateTimeFormatter formato : FORMATOS_FECHA) {
            try {
                return LocalDate.parse(texto, formato);
            } catch (DateTimeParseException ignored) {
            }
        }

        error(padre, nombreCampo + " debe tener formato dd/MM/yyyy.");
        return null;
    }

    public static LocalTime leerHora(Component padre, JTextField campo, String nombreCampo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            error(padre, "Debe ingresar " + nombreCampo + ".");
            return null;
        }

        try {
            return LocalTime.parse(texto, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            error(padre, nombreCampo + " debe tener formato HH:mm.");
            return null;
        }
    }

    public static Integer leerEntero(Component padre, JTextField campo, String nombreCampo, int minimo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            error(padre, "Debe ingresar " + nombreCampo + ".");
            return null;
        }

        try {
            int valor = Integer.parseInt(texto);
            if (valor < minimo) {
                error(padre, nombreCampo + " debe ser mayor o igual que " + minimo + ".");
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            error(padre, nombreCampo + " debe ser numérico.");
            return null;
        }
    }

    public static Long leerLong(Component padre, JTextField campo, String nombreCampo, long minimo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            error(padre, "Debe ingresar " + nombreCampo + ".");
            return null;
        }

        try {
            long valor = Long.parseLong(texto);
            if (valor < minimo) {
                error(padre, nombreCampo + " debe ser mayor o igual que " + minimo + ".");
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            error(padre, nombreCampo + " debe ser numérico.");
            return null;
        }
    }

    public static String leerTexto(Component padre, JTextField campo, String nombreCampo) {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            error(padre, "Debe ingresar " + nombreCampo + ".");
            return null;
        }
        return texto;
    }

    public static IdPersona leerIdPersona(Component padre, JComboBox<String> tipo, JTextField id, JTextField nacionalidad) {
        String textoId = leerTexto(padre, id, "identificador");
        if (textoId == null) {
            return null;
        }

        if ("RUT".equals(tipo.getSelectedItem())) {
            try {
                return Rut.of(textoId);
            } catch (RuntimeException e) {
                error(padre, "El RUT no es válido. Use formato 11.111.111-1 o 11111111-1.");
                return null;
            }
        }

        String nac = leerTexto(padre, nacionalidad, "nacionalidad del pasaporte");
        if (nac == null) {
            return null;
        }
        return Pasaporte.of(textoId, nac);
    }

    public static Rut leerRut(Component padre, JTextField campo, String nombreCampo) {
        String texto = leerTexto(padre, campo, nombreCampo);
        if (texto == null) {
            return null;
        }
        try {
            return Rut.of(texto);
        } catch (RuntimeException e) {
            error(padre, "El RUT ingresado no es válido.");
            return null;
        }
    }

    public static IdPersona parseIdDesdeTexto(String texto) {
        if (texto == null) {
            return null;
        }
        return Rut.of(texto.trim());
    }

    public static DefaultTableModel modeloNoEditable(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static void cargarTabla(DefaultTableModel modelo, String[][] datos) {
        modelo.setRowCount(0);
        if (datos == null) {
            return;
        }
        for (String[] fila : datos) {
            modelo.addRow(fila);
        }
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static JPanel fila(String etiqueta, JComponent componente) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        JLabel label = new JLabel(etiqueta);
        label.setPreferredSize(new Dimension(140, 25));
        panel.add(label, BorderLayout.WEST);
        panel.add(componente, BorderLayout.CENTER);
        return panel;
    }
}
