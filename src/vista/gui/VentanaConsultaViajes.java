package vista.gui;

import controlador.SistemaVentaPasajes;
import excepciones.SVPException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaConsultaViajes extends JFrame {

    private JPanel panelPrincipal; // Panel raíz asociado al archivo .form de IntelliJ GUI Designer.

    private final SistemaVentaPasajes sistema;
    private final DefaultTableModel modelo;

    public VentanaConsultaViajes(Frame owner, SistemaVentaPasajes sistema) {
        this.sistema = sistema;
        setTitle("Consulta de viajes");
        setSize(850, 420);
        setLocationRelativeTo(owner);

        modelo = GUIUtil.modeloNoEditable(new String[]{
                "Fecha", "Hora salida", "Hora llegada", "Precio", "Asientos disponibles", "Bus", "Comuna salida", "Comuna llegada"
        });
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
            String[][] datos = sistema.listViajes();
            GUIUtil.cargarTabla(modelo, datos);
            if (datos.length == 0) {
                GUIUtil.info(this, "No hay viajes registrados.");
            }
        } catch (SVPException ex) {
            GUIUtil.error(this, ex.getMessage());
        }
    }
}
