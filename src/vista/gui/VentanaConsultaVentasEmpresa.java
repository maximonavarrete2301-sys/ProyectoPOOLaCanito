package vista.gui;

import controlador.ControladorEmpresas;
import excepciones.SVPException;
import utilidades.Rut;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaConsultaVentasEmpresa extends JFrame {

    private JPanel panelPrincipal; // Panel raíz asociado al archivo .form de IntelliJ GUI Designer.

    private final ControladorEmpresas controlador;
    private final JTextField txtRutEmpresa;
    private final DefaultTableModel modelo;

    public VentanaConsultaVentasEmpresa(Frame owner, ControladorEmpresas controlador) {
        this.controlador = controlador;
        setTitle("Consulta de ventas por empresa");
        setSize(760, 420);
        setLocationRelativeTo(owner);

        txtRutEmpresa = new JTextField(14);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscar());

        JPanel superior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        superior.add(new JLabel("RUT empresa:"));
        superior.add(txtRutEmpresa);
        superior.add(btnBuscar);

        modelo = GUIUtil.modeloNoEditable(new String[]{"Fecha", "Tipo documento", "Monto pagado", "Tipo pago"});
        JTable tabla = new JTable(modelo);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(superior, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        setContentPane(panel);
    }

    private void buscar() {
        Rut rut = GUIUtil.leerRut(this, txtRutEmpresa, "RUT de la empresa");
        if (rut == null) {
            return;
        }

        try {
            String[][] datos = controlador.listVentasEmpresa(rut);
            GUIUtil.cargarTabla(modelo, datos);
            if (datos.length == 0) {
                GUIUtil.info(this, "La empresa no tiene ventas registradas.");
            }
        } catch (SVPException ex) {
            GUIUtil.error(this, ex.getMessage());
        }
    }
}
