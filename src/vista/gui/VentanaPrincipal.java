package vista.gui;

import controlador.ControladorEmpresas;
import controlador.SistemaVentaPasajes;
import excepciones.SVPException;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private JPanel panelPrincipal; // Panel raíz asociado al archivo .form de IntelliJ GUI Designer.

    private SistemaVentaPasajes sistema;
    private ControladorEmpresas controladorEmpresas;
    private JLabel lblEstado;

    public VentanaPrincipal() {
        sistema = SistemaVentaPasajes.getInstance();
        controladorEmpresas = ControladorEmpresas.getInstance();
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("Sistema Venta de Pasajes - GUI");
        setSize(560, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void inicializarComponentes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel("Sistema de Venta de Pasajes", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(titulo, BorderLayout.NORTH);

        JPanel botones = new JPanel(new GridLayout(0, 1, 8, 8));

        JButton btnIniciales = new JButton("Leer datos iniciales");
        JButton btnGuardar = new JButton("Guardar datos del sistema");
        JButton btnRecuperar = new JButton("Recuperar datos del sistema");
        JButton btnVenta = new JButton("Venta de pasajes");
        JButton btnCrearViaje = new JButton("Crear viaje");
        JButton btnEmpresas = new JButton("Consulta: listar empresas");
        JButton btnViajes = new JButton("Consulta: listar viajes");
        JButton btnVentasEmpresa = new JButton("Consulta: ventas por empresa");
        JButton btnSalir = new JButton("Salir");

        botones.add(btnIniciales);
        botones.add(btnGuardar);
        botones.add(btnRecuperar);
        botones.add(btnVenta);
        botones.add(btnCrearViaje);
        botones.add(btnEmpresas);
        botones.add(btnViajes);
        botones.add(btnVentasEmpresa);
        botones.add(btnSalir);

        panel.add(botones, BorderLayout.CENTER);

        lblEstado = new JLabel("Listo.");
        panel.add(lblEstado, BorderLayout.SOUTH);

        setContentPane(panel);

        btnIniciales.addActionListener(e -> leerDatosIniciales());
        btnGuardar.addActionListener(e -> guardarDatosSistema());
        btnRecuperar.addActionListener(e -> recuperarDatosSistema());
        btnVenta.addActionListener(e -> new VentanaVentaPasajes(this, sistema).setVisible(true));
        btnCrearViaje.addActionListener(e -> new VentanaCrearViaje(this, sistema, controladorEmpresas).setVisible(true));
        btnEmpresas.addActionListener(e -> new VentanaConsultaEmpresas(this, controladorEmpresas).setVisible(true));
        btnViajes.addActionListener(e -> new VentanaConsultaViajes(this, sistema).setVisible(true));
        btnVentasEmpresa.addActionListener(e -> new VentanaConsultaVentasEmpresa(this, controladorEmpresas).setVisible(true));
        btnSalir.addActionListener(e -> dispose());
    }

    private void leerDatosIniciales() {
        try {
            sistema.readDatosIniciales();
            actualizarReferencias();
            lblEstado.setText("Datos iniciales cargados.");
            GUIUtil.info(this, "Datos iniciales cargados correctamente.");
        } catch (SVPException ex) {
            GUIUtil.error(this, ex.getMessage());
        }
    }

    private void guardarDatosSistema() {
        try {
            sistema.saveDatosSistema();
            lblEstado.setText("Datos del sistema guardados.");
            GUIUtil.info(this, "Datos del sistema guardados correctamente.");
        } catch (SVPException ex) {
            GUIUtil.error(this, ex.getMessage());
        }
    }

    private void recuperarDatosSistema() {
        try {
            sistema.readDatosSistema();
            actualizarReferencias();
            lblEstado.setText("Datos del sistema recuperados.");
            GUIUtil.info(this, "Datos del sistema recuperados correctamente.");
        } catch (SVPException ex) {
            GUIUtil.error(this, ex.getMessage());
        }
    }

    private void actualizarReferencias() {
        sistema = SistemaVentaPasajes.getInstance();
        controladorEmpresas = ControladorEmpresas.getInstance();
    }
}
