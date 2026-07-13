package vista.gui;

import controlador.SistemaVentaPasajes;
import excepciones.SVPException;
import modelo.TipoDocumento;
import utilidades.IdPersona;
import utilidades.Nombre;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class VentanaVentaPasajes extends JFrame {

    private JPanel panelPrincipal;

    private final SistemaVentaPasajes sistema;

    private final JTextField txtIdDocumento = new JTextField(12);
    private final JComboBox<TipoDocumento> cmbTipoDocumento = new JComboBox<>(TipoDocumento.values());
    private final JComboBox<String> cmbTipoIdCliente = new JComboBox<>(new String[]{"RUT", "Pasaporte"});
    private final JTextField txtIdCliente = new JTextField(12);
    private final JTextField txtNacionalidadCliente = new JTextField(10);
    private final JTextField txtFechaViaje = new JTextField(10);
    private final JTextField txtComunaSalida = new JTextField(12);
    private final JTextField txtComunaLlegada = new JTextField(12);
    private final JTextField txtNroPasajes = new JTextField(5);

    private final DefaultTableModel modeloViajes = GUIUtil.modeloNoEditable(new String[]{"Patente", "Hora", "Precio", "Asientos disponibles"});
    private final JTable tablaViajes = new JTable(modeloViajes);

    private final DefaultTableModel modeloAsientos = GUIUtil.modeloNoEditable(new String[]{"Asiento", "Estado"});
    private final JTable tablaAsientos = new JTable(modeloAsientos);

    private final DefaultTableModel modeloPasajes = GUIUtil.modeloNoEditable(new String[]{"Asiento", "ID pasajero", "Nombre pasajero"});
    private final JTable tablaPasajes = new JTable(modeloPasajes);

    private final JTextField txtAsiento = new JTextField(5);
    private final JComboBox<String> cmbTipoIdPasajero = new JComboBox<>(new String[]{"RUT", "Pasaporte"});
    private final JTextField txtIdPasajero = new JTextField(12);
    private final JTextField txtNacionalidadPasajero = new JTextField(10);

    private final JComboBox<String> cmbTipoPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta"});
    private final JTextField txtNroTarjeta = new JTextField(14);
    private final JLabel lblEstado = new JLabel("Inicie una venta para comenzar.");
    private final JLabel lblMonto = new JLabel("Monto: $0");

    private final JButton btnIniciar = new JButton("Iniciar venta y buscar viajes");
    private final JButton btnCargarAsientos = new JButton("Cargar asientos del viaje seleccionado");
    private final JButton btnAgregarPasaje = new JButton("Agregar pasaje");
    private final JButton btnPagar = new JButton("Pagar venta");
    private final JButton btnGenerar = new JButton("Generar pasajes electrónicos");
    private final JButton btnNuevaVenta = new JButton("Nueva venta");

    private LocalDate fechaActual;
    private TipoDocumento tipoActual;
    private String idDocumentoActual;
    private int pasajesSolicitados;
    private int pasajesAgregados;
    private String patenteSeleccionada;
    private LocalTime horaSeleccionada;

    public VentanaVentaPasajes(Frame owner, SistemaVentaPasajes sistema) {
        this.sistema = sistema;
        setTitle("Venta de pasajes");
        setSize(1050, 720);
        setLocationRelativeTo(owner);
        inicializarComponentes();
        actualizarEstadoComponentes(false, false, false);
    }

    private void inicializarComponentes() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("1. Iniciar venta", crearPanelInicio());
        tabs.add("2. Viaje y pasajes", crearPanelPasajes());
        tabs.add("3. Pago y generación", crearPanelPago());

        panel.add(tabs, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout());
        inferior.add(lblEstado, BorderLayout.CENTER);
        inferior.add(lblMonto, BorderLayout.EAST);
        panel.add(inferior, BorderLayout.SOUTH);

        setContentPane(panel);

        cmbTipoIdCliente.addActionListener(e -> actualizarNacionalidad(txtNacionalidadCliente, cmbTipoIdCliente));
        cmbTipoIdPasajero.addActionListener(e -> actualizarNacionalidad(txtNacionalidadPasajero, cmbTipoIdPasajero));
        cmbTipoPago.addActionListener(e -> txtNroTarjeta.setEnabled("Tarjeta".equals(cmbTipoPago.getSelectedItem())));
        btnIniciar.addActionListener(e -> iniciarVenta());
        btnCargarAsientos.addActionListener(e -> cargarAsientos());
        btnAgregarPasaje.addActionListener(e -> agregarPasaje());
        btnPagar.addActionListener(e -> pagarVenta());
        btnGenerar.addActionListener(e -> generarPasajes());
        btnNuevaVenta.addActionListener(e -> limpiarTodo());

        actualizarNacionalidad(txtNacionalidadCliente, cmbTipoIdCliente);
        actualizarNacionalidad(txtNacionalidadPasajero, cmbTipoIdPasajero);
        txtNroTarjeta.setEnabled(false);
    }

    private JPanel crearPanelInicio() {
        JPanel formulario = new JPanel(new GridLayout(0, 1, 6, 6));
        formulario.add(GUIUtil.fila("ID documento:", txtIdDocumento));
        formulario.add(GUIUtil.fila("Tipo documento:", cmbTipoDocumento));
        formulario.add(GUIUtil.fila("Tipo ID cliente:", cmbTipoIdCliente));
        formulario.add(GUIUtil.fila("ID cliente:", txtIdCliente));
        formulario.add(GUIUtil.fila("Nacionalidad:", txtNacionalidadCliente));
        formulario.add(GUIUtil.fila("Fecha viaje:", txtFechaViaje));
        formulario.add(GUIUtil.fila("Comuna salida:", txtComunaSalida));
        formulario.add(GUIUtil.fila("Comuna llegada:", txtComunaLlegada));
        formulario.add(GUIUtil.fila("N° pasajes:", txtNroPasajes));

        JPanel superior = new JPanel(new BorderLayout(8, 8));
        superior.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        superior.add(new JLabel("Formato fecha: dd/MM/yyyy. Ingrese cliente ya existente."), BorderLayout.NORTH);
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(btnIniciar, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(superior, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaViajes), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelPasajes() {
        JPanel datos = new JPanel(new GridLayout(0, 1, 6, 6));
        datos.add(GUIUtil.fila("Asiento:", txtAsiento));
        datos.add(GUIUtil.fila("Tipo ID pasajero:", cmbTipoIdPasajero));
        datos.add(GUIUtil.fila("ID pasajero:", txtIdPasajero));
        datos.add(GUIUtil.fila("Nacionalidad:", txtNacionalidadPasajero));
        datos.add(btnAgregarPasaje);

        JPanel izquierda = new JPanel(new BorderLayout(8, 8));
        izquierda.add(btnCargarAsientos, BorderLayout.NORTH);
        izquierda.add(new JScrollPane(tablaAsientos), BorderLayout.CENTER);

        JPanel derecha = new JPanel(new BorderLayout(8, 8));
        derecha.add(datos, BorderLayout.NORTH);
        derecha.add(new JScrollPane(tablaPasajes), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, izquierda, derecha);
        split.setResizeWeight(0.45);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelPago() {
        JPanel formulario = new JPanel(new GridLayout(0, 1, 8, 8));
        formulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formulario.add(GUIUtil.fila("Tipo pago:", cmbTipoPago));
        formulario.add(GUIUtil.fila("N° tarjeta:", txtNroTarjeta));
        formulario.add(btnPagar);
        formulario.add(btnGenerar);
        formulario.add(btnNuevaVenta);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(formulario, BorderLayout.NORTH);
        return panel;
    }

    private void iniciarVenta() {
        String idDoc = GUIUtil.leerTexto(this, txtIdDocumento, "ID documento");
        if (idDoc == null) return;

        TipoDocumento tipo = (TipoDocumento) cmbTipoDocumento.getSelectedItem();
        LocalDate fechaViaje = GUIUtil.leerFecha(this, txtFechaViaje, "fecha del viaje");
        if (fechaViaje == null) return;

        String comunaSalida = GUIUtil.leerTexto(this, txtComunaSalida, "comuna de salida");
        if (comunaSalida == null) return;
        String comunaLlegada = GUIUtil.leerTexto(this, txtComunaLlegada, "comuna de llegada");
        if (comunaLlegada == null) return;
        Integer nroPasajes = GUIUtil.leerEntero(this, txtNroPasajes, "número de pasajes", 1);
        if (nroPasajes == null) return;

        IdPersona idCliente = GUIUtil.leerIdPersona(this, cmbTipoIdCliente, txtIdCliente, txtNacionalidadCliente);
        if (idCliente == null) return;

        try {
            sistema.iniciaVenta(idDoc, tipo, fechaViaje, comunaSalida, comunaLlegada, nroPasajes, idCliente);
            String[][] viajes = sistema.getHorariosDisponibles(fechaViaje, comunaSalida, comunaLlegada, nroPasajes);
            GUIUtil.cargarTabla(modeloViajes, viajes);

            idDocumentoActual = idDoc;
            tipoActual = tipo;
            fechaActual = fechaViaje;
            pasajesSolicitados = nroPasajes;
            pasajesAgregados = 0;
            patenteSeleccionada = null;
            horaSeleccionada = null;

            actualizarEstadoComponentes(true, false, false);
            lblEstado.setText("Venta iniciada. Seleccione un viaje y cargue sus asientos.");
            actualizarMonto();
            GUIUtil.info(this, "Venta iniciada correctamente. Seleccione un viaje disponible.");
        } catch (SVPException ex) {
            GUIUtil.error(this, ex.getMessage());
        }
    }

    private void cargarAsientos() {
        int fila = tablaViajes.getSelectedRow();
        if (fila < 0) {
            GUIUtil.error(this, "Debe seleccionar un viaje disponible.");
            return;
        }

        patenteSeleccionada = modeloViajes.getValueAt(fila, 0).toString();
        horaSeleccionada = LocalTime.parse(modeloViajes.getValueAt(fila, 1).toString());

        String[] asientos = sistema.listAsientosDeViaje(fechaActual, horaSeleccionada, patenteSeleccionada);
        modeloAsientos.setRowCount(0);
        for (String asiento : asientos) {
            if ("*".equals(asiento)) {
                modeloAsientos.addRow(new String[]{"*", "Ocupado"});
            } else {
                modeloAsientos.addRow(new String[]{asiento, "Libre"});
            }
        }

        actualizarEstadoComponentes(true, true, false);
        lblEstado.setText("Viaje seleccionado: " + patenteSeleccionada + " a las " + horaSeleccionada + ". Agregue pasajes.");
    }

    private void agregarPasaje() {
        if (patenteSeleccionada == null || horaSeleccionada == null) {
            GUIUtil.error(this, "Debe cargar los asientos de un viaje seleccionado.");
            return;
        }
        if (pasajesAgregados >= pasajesSolicitados) {
            GUIUtil.error(this, "Ya agregó todos los pasajes solicitados.");
            return;
        }

        Integer asiento = GUIUtil.leerEntero(this, txtAsiento, "asiento", 1);
        if (asiento == null) return;
        IdPersona idPasajero = GUIUtil.leerIdPersona(this, cmbTipoIdPasajero, txtIdPasajero, txtNacionalidadPasajero);
        if (idPasajero == null) return;

        try {
            String nombrePasajero = sistema.getNombrePasajero(idPasajero).orElse(null);
            if (nombrePasajero == null) {
                boolean creado = crearPasajero(idPasajero);
                if (!creado) {
                    return;
                }
                nombrePasajero = sistema.getNombrePasajero(idPasajero).orElse(idPasajero.toString());
            }

            sistema.vendePasaje(idDocumentoActual, tipoActual, fechaActual, horaSeleccionada, patenteSeleccionada, asiento, idPasajero);
            modeloPasajes.addRow(new String[]{String.valueOf(asiento), idPasajero.toString(), nombrePasajero});
            pasajesAgregados++;
            cargarAsientosActuales();
            actualizarMonto();
            limpiarDatosPasajero();

            if (pasajesAgregados >= pasajesSolicitados) {
                actualizarEstadoComponentes(true, true, true);
                lblEstado.setText("Todos los pasajes fueron agregados. Ahora puede pagar la venta.");
            } else {
                lblEstado.setText("Pasaje agregado. Faltan " + (pasajesSolicitados - pasajesAgregados) + " pasajes.");
            }
        } catch (SVPException ex) {
            GUIUtil.error(this, ex.getMessage());
        } catch (RuntimeException ex) {
            GUIUtil.error(this, "No se pudo agregar el pasaje: " + ex.getMessage());
        }
    }

    private boolean crearPasajero(IdPersona idPasajero) {
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "No existe un pasajero con ese identificador. ¿Desea crearlo ahora?",
                "Crear pasajero",
                JOptionPane.YES_NO_OPTION
        );
        if (respuesta != JOptionPane.YES_OPTION) {
            return false;
        }

        Nombre nombrePasajero = DialogosDatos.pedirNombre(this, "Datos del pasajero");
        if (nombrePasajero == null) {
            return false;
        }

        JTextField txtTelefono = new JTextField(15);
        JPanel panelFono = new JPanel(new GridLayout(0, 1, 5, 5));
        panelFono.add(GUIUtil.fila("Teléfono pasajero:", txtTelefono));
        int opFono = JOptionPane.showConfirmDialog(this, panelFono, "Teléfono del pasajero", JOptionPane.OK_CANCEL_OPTION);
        if (opFono != JOptionPane.OK_OPTION || txtTelefono.getText().trim().isEmpty()) {
            GUIUtil.error(this, "Debe ingresar teléfono del pasajero.");
            return false;
        }

        Nombre nombreContacto = DialogosDatos.pedirNombre(this, "Datos del contacto");
        if (nombreContacto == null) {
            return false;
        }

        JTextField txtTelefonoContacto = new JTextField(15);
        JPanel panelContacto = new JPanel(new GridLayout(0, 1, 5, 5));
        panelContacto.add(GUIUtil.fila("Teléfono contacto:", txtTelefonoContacto));
        int opContacto = JOptionPane.showConfirmDialog(this, panelContacto, "Teléfono del contacto", JOptionPane.OK_CANCEL_OPTION);
        if (opContacto != JOptionPane.OK_OPTION || txtTelefonoContacto.getText().trim().isEmpty()) {
            GUIUtil.error(this, "Debe ingresar teléfono del contacto.");
            return false;
        }

        sistema.createPasajero(idPasajero, nombrePasajero, txtTelefono.getText().trim(), nombreContacto, txtTelefonoContacto.getText().trim());
        return true;
    }

    private void pagarVenta() {
        try {
            if ("Tarjeta".equals(cmbTipoPago.getSelectedItem())) {
                Long tarjeta = GUIUtil.leerLong(this, txtNroTarjeta, "número de tarjeta", 1L);
                if (tarjeta == null) return;
                sistema.pagaVenta(idDocumentoActual, tipoActual, tarjeta);
            } else {
                sistema.pagaVenta(idDocumentoActual, tipoActual);
            }

            btnPagar.setEnabled(false);
            btnGenerar.setEnabled(true);
            lblEstado.setText("Venta pagada. Puede generar los pasajes electrónicos.");
            actualizarMonto();
            GUIUtil.info(this, "Venta pagada correctamente.");
        } catch (SVPException ex) {
            GUIUtil.error(this, ex.getMessage());
        }
    }

    private void generarPasajes() {
        try {
            sistema.generatePasajesVenta(idDocumentoActual, tipoActual);
            GUIUtil.info(this, "Pasajes electrónicos generados correctamente.");
            lblEstado.setText("Pasajes electrónicos generados.");
        } catch (SVPException ex) {
            GUIUtil.error(this, ex.getMessage());
        }
    }

    private void cargarAsientosActuales() {
        String[] asientos = sistema.listAsientosDeViaje(fechaActual, horaSeleccionada, patenteSeleccionada);
        modeloAsientos.setRowCount(0);
        for (String asientoDisponible : asientos) {
            if ("*".equals(asientoDisponible)) {
                modeloAsientos.addRow(new String[]{"*", "Ocupado"});
            } else {
                modeloAsientos.addRow(new String[]{asientoDisponible, "Libre"});
            }
        }
    }

    private void actualizarMonto() {
        if (idDocumentoActual == null || tipoActual == null) {
            lblMonto.setText("Monto: $0");
            return;
        }
        int monto = sistema.getMontoVenta(idDocumentoActual, tipoActual).orElse(0);
        lblMonto.setText("Monto: $" + monto);
    }

    private void actualizarEstadoComponentes(boolean ventaIniciada, boolean viajeSeleccionado, boolean permitePagar) {
        btnIniciar.setEnabled(!ventaIniciada);
        btnCargarAsientos.setEnabled(ventaIniciada);
        btnAgregarPasaje.setEnabled(viajeSeleccionado && !permitePagar);
        btnPagar.setEnabled(permitePagar);
        btnGenerar.setEnabled(false);
    }

    private void actualizarNacionalidad(JTextField nacionalidad, JComboBox<String> tipo) {
        boolean pasaporte = "Pasaporte".equals(tipo.getSelectedItem());
        nacionalidad.setEnabled(pasaporte);
        if (!pasaporte) {
            nacionalidad.setText("");
        }
    }

    private void limpiarDatosPasajero() {
        txtAsiento.setText("");
        txtIdPasajero.setText("");
        txtNacionalidadPasajero.setText("");
    }

    private void limpiarTodo() {
        txtIdDocumento.setText("");
        txtIdCliente.setText("");
        txtNacionalidadCliente.setText("");
        txtFechaViaje.setText("");
        txtComunaSalida.setText("");
        txtComunaLlegada.setText("");
        txtNroPasajes.setText("");
        limpiarDatosPasajero();
        txtNroTarjeta.setText("");
        modeloViajes.setRowCount(0);
        modeloAsientos.setRowCount(0);
        modeloPasajes.setRowCount(0);
        fechaActual = null;
        tipoActual = null;
        idDocumentoActual = null;
        pasajesSolicitados = 0;
        pasajesAgregados = 0;
        patenteSeleccionada = null;
        horaSeleccionada = null;
        actualizarEstadoComponentes(false, false, false);
        lblMonto.setText("Monto: $0");
        lblEstado.setText("Inicie una venta para comenzar.");
    }
}
