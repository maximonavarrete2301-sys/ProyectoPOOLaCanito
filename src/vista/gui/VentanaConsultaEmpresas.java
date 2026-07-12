package vista.gui;

import controlador.ControladorEmpresas;
import excepciones.SVPException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaConsultaEmpresas extends JFrame {

    private JPanel panelPrincipal; // Panel raíz asociado al archivo .form de IntelliJ GUI Designer.

    private final ControladorEmpresas controlador;
    private final DefaultTableModel modelo;

    public VentanaConsultaEmpresas(Frame owner, ControladorEmpresas controlador) {
        this.controlador = controlador;
        setTitle("Consulta de empresas");
        setSize(760, 420);
        setLocationRelativeTo(owner);

        modelo = GUIUtil.modeloNoEditable(new String[]{"RUT", "Nombre", "URL", "Tripulantes", "Buses"});
        JTable tabla = new JTable(modelo);
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarDatos());

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(btnActualizar, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        setContentPane(panel);

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            String[][] datos = controlador.listEmpresas();
            GUIUtil.cargarTabla(modelo, datos);
            if (datos.length == 0) {
                GUIUtil.info(this, "No hay empresas registradas.");
            }
        } catch (SVPException ex) {
            GUIUtil.error(this, ex.getMessage());
        }
    }
}
