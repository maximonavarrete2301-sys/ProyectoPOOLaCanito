package vista.gui;

import controlador.ControladorEmpresas;
import controlador.SistemaVentaPasajes;
import excepciones.SVPException;
import utilidades.IdPersona;
import utilidades.Rut;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class VentanaCrearViaje extends JFrame {

    private JPanel panelPrincipal; // Panel raíz asociado al archivo .form de IntelliJ GUI Designer.

    private final SistemaVentaPasajes sistema;
    private final ControladorEmpresas controlador;

    private final JTextField txtFecha = new JTextField(10);
    private final JTextField txtHora = new JTextField(8);
    private final JTextField txtPrecio = new JTextField(8);
    private final JTextField txtDuracion = new JTextField(8);

    private final JComboBox<ComboItem> cmbBus = new JComboBox<>();
    private final JComboBox<ComboItem> cmbAuxiliar = new JComboBox<>();
    private final JComboBox<ComboItem> cmbConductor1 = new JComboBox<>();
    private final JComboBox<ComboItem> cmbConductor2 = new JComboBox<>();
    private final JComboBox<ComboItem> cmbTerminalSalida = new JComboBox<>();
    private final JComboBox<ComboItem> cmbTerminalLlegada = new JComboBox<>();

    private final Map<String, String> rutEmpresaPorPatente = new HashMap<>();

    public VentanaCrearViaje(Frame owner, SistemaVentaPasajes sistema, ControladorEmpresas controlador) {
        this.sistema = sistema;
        this.controlador = controlador;
        setTitle("Crear viaje");
        setSize(660, 560);
        setLocationRelativeTo(owner);
        inicializarComponentes();
        cargarBuses();
        cargarTerminales();
        cargarTripulantesDeBusSeleccionado();
    }

    private void inicializarComponentes() {
        JPanel formulario = new JPanel(new GridLayout(0, 1, 7, 7));
        formulario.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        formulario.add(GUIUtil.fila("Fecha:", txtFecha));
        formulario.add(GUIUtil.fila("Hora:", txtHora));
        formulario.add(GUIUtil.fila("Precio:", txtPrecio));
        formulario.add(GUIUtil.fila("Duración minutos:", txtDuracion));
        formulario.add(GUIUtil.fila("Bus:", cmbBus));
        formulario.add(GUIUtil.fila("Auxiliar:", cmbAuxiliar));
        formulario.add(GUIUtil.fila("Conductor 1:", cmbConductor1));
        formulario.add(GUIUtil.fila("Conductor 2:", cmbConductor2));
        formulario.add(GUIUtil.fila("Terminal salida:", cmbTerminalSalida));
        formulario.add(GUIUtil.fila("Terminal llegada:", cmbTerminalLlegada));

        JLabel ayuda = new JLabel("Formato fecha: dd/MM/yyyy. Formato hora: HH:mm.");
        JButton btnCrear = new JButton("Crear viaje");
        JButton btnActualizar = new JButton("Actualizar listas");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnActualizar);
        botones.add(btnCrear);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(ayuda, BorderLayout.NORTH);
        panel.add(formulario, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        setContentPane(panel);

        cmbBus.addActionListener(e -> cargarTripulantesDeBusSeleccionado());
        btnActualizar.addActionListener(e -> {
            cargarBuses();
            cargarTerminales();
            cargarTripulantesDeBusSeleccionado();
        });
        btnCrear.addActionListener(e -> crearViaje());
    }

    private void cargarBuses() {
        cmbBus.removeAllItems();
        rutEmpresaPorPatente.clear();

        String[][] buses = controlador.listBuses();
        for (String[] b : buses) {
            String patente = b[0];
            String marca = b[1];
            String modelo = b[2];
            String asientos = b[3];
            String rutEmpresa = b[4];
            String nombreEmpresa = b[5];
            rutEmpresaPorPatente.put(patente, rutEmpresa);
            cmbBus.addItem(new ComboItem(patente, patente + " - " + marca + " " + modelo + " - " + asientos + " asientos - " + nombreEmpresa));
        }
    }

    private void cargarTerminales() {
        cmbTerminalSalida.removeAllItems();
        cmbTerminalLlegada.removeAllItems();

        String[][] terminales = controlador.listTerminales();
        for (String[] t : terminales) {
            String nombre = t[0];
            String comuna = t[3];
            ComboItem itemSalida = new ComboItem(comuna, nombre + " - " + comuna);
            ComboItem itemLlegada = new ComboItem(comuna, nombre + " - " + comuna);
            cmbTerminalSalida.addItem(itemSalida);
            cmbTerminalLlegada.addItem(itemLlegada);
        }
    }

    private void cargarTripulantesDeBusSeleccionado() {
        cmbAuxiliar.removeAllItems();
        cmbConductor1.removeAllItems();
        cmbConductor2.removeAllItems();
        cmbConductor2.addItem(new ComboItem("", "Ninguno"));

        ComboItem bus = (ComboItem) cmbBus.getSelectedItem();
        if (bus == null) {
            return;
        }

        String rutTexto = rutEmpresaPorPatente.get(bus.getValor());
        if (rutTexto == null) {
            return;
        }

        try {
            Rut rutEmpresa = Rut.of(rutTexto);
            for (String[] aux : controlador.listTripulantesEmpresa(rutEmpresa, "Auxiliar")) {
                cmbAuxiliar.addItem(new ComboItem(aux[1], aux[1] + " - " + aux[2]));
            }
            for (String[] cond : controlador.listTripulantesEmpresa(rutEmpresa, "Conductor")) {
                ComboItem item = new ComboItem(cond[1], cond[1] + " - " + cond[2]);
                cmbConductor1.addItem(item);
                cmbConductor2.addItem(new ComboItem(cond[1], cond[1] + " - " + cond[2]));
            }
        } catch (SVPException ex) {
            GUIUtil.error(this, ex.getMessage());
        }
    }

    private void crearViaje() {
        LocalDate fecha = GUIUtil.leerFecha(this, txtFecha, "fecha");
        if (fecha == null) return;
        LocalTime hora = GUIUtil.leerHora(this, txtHora, "hora");
        if (hora == null) return;
        Integer precio = GUIUtil.leerEntero(this, txtPrecio, "precio", 1);
        if (precio == null) return;
        Integer duracion = GUIUtil.leerEntero(this, txtDuracion, "duración", 1);
        if (duracion == null) return;

        ComboItem bus = (ComboItem) cmbBus.getSelectedItem();
        ComboItem auxiliar = (ComboItem) cmbAuxiliar.getSelectedItem();
        ComboItem conductor1 = (ComboItem) cmbConductor1.getSelectedItem();
        ComboItem conductor2 = (ComboItem) cmbConductor2.getSelectedItem();
        ComboItem salida = (ComboItem) cmbTerminalSalida.getSelectedItem();
        ComboItem llegada = (ComboItem) cmbTerminalLlegada.getSelectedItem();

        if (bus == null || auxiliar == null || conductor1 == null || salida == null || llegada == null) {
            GUIUtil.error(this, "Debe seleccionar bus, auxiliar, conductor y terminales.");
            return;
        }
        if (salida.getValor().equals(llegada.getValor())) {
            GUIUtil.error(this, "La comuna de salida y llegada no pueden ser iguales.");
            return;
        }

        try {
            IdPersona idAux = GUIUtil.parseIdDesdeTexto(auxiliar.getValor());
            IdPersona idCond1 = GUIUtil.parseIdDesdeTexto(conductor1.getValor());
            IdPersona[] tripulantes;
            if (conductor2 != null && !conductor2.getValor().isEmpty()) {
                tripulantes = new IdPersona[]{idAux, idCond1, GUIUtil.parseIdDesdeTexto(conductor2.getValor())};
            } else {
                tripulantes = new IdPersona[]{idAux, idCond1};
            }

            sistema.createViaje(
                    fecha,
                    hora,
                    precio,
                    duracion,
                    bus.getValor(),
                    tripulantes,
                    new String[]{salida.getValor(), llegada.getValor()}
            );

            GUIUtil.info(this, "Viaje creado correctamente.");
            limpiarCampos();
        } catch (SVPException ex) {
            GUIUtil.error(this, ex.getMessage());
        } catch (RuntimeException ex) {
            GUIUtil.error(this, "No se pudo crear el viaje: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        txtFecha.setText("");
        txtHora.setText("");
        txtPrecio.setText("");
        txtDuracion.setText("");
    }
}
