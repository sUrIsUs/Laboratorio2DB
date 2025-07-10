/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package app;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.toedter.calendar.JDateChooser;
import exceptions.FechasCruzadas;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;


public class App extends javax.swing.JFrame {
    // Variables
    int tuplaSeleccionada;
    LocalDate now = LocalDate.now();
    int day = now.getDayOfMonth(), month = now.getMonthValue(), year = now.getYear();
    String [] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    HashMap<String, JTable> mapTablas = new HashMap<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    JComponent [] objectComponents;
    JComponent [] personComponents;
    JComponent [] boxComponents;
    
    // Constantes para la base de datos
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/";
    private static final String DB_USER = "postgres";
    private static final String DB_PWD = "admin";
    
    // Objetos utilizados para interactuar con la base de datos
    private static Statement query = null;
    private static PreparedStatement p_query = null;
    private static Connection conn = null;
    private static ResultSet result = null;
    
    public App() throws SQLException {
        
        // Inicio ejecución
        initComponents();
        this.setLocationRelativeTo(null);
        this.objectComponents = new JComponent[]{ codigoObjModTF, nombreObjModTF, tipoExtraccionObjModTF, altoObjModTF, largoObjModTF, espesorObjModTF, pesoObjModTF, cantidadObjModTF, fechaRegistroObjModTF, descripcionObjModTF, origenObjModTF, codigoCuadriculaObjModTF, codigoCajaObjModTF, dniPersonaObjModTF, tipoObjModCB};
        this.personComponents = new JComponent[]{ dniArqModTF, nombreArqModTF, apellidoArqModTF, mailArqModTF, telefonoArqModTF};
        
        showPanel(inicioPanel);
        
        // Muestro fecha
        todayDate.setText("Hoy es " + day + " de " + months[month - 1] + " de " + year);
        
        // Instancio el mapeo de las tablas
        mapTablas.put("Objetos", tablaObjetos);
        mapTablas.put("Personas", tablaPersonas);
        mapTablas.put("Cajas", tablaCajas);
        
        
        
        // Cargo la base de datos si no fue cargada previamente
        cargaSQL();

        // Instancio las tablas
        actualizarInformacionObjetos();
        
        updateTabla("Objetos");
        updateTabla("Personas");
        updateTabla("Cajas");
    }
    
    // Función auxiliar para cambiar de panel
    public void showPanel(JPanel p){
       p.setLocation(0,0);
       contenedor.removeAll();
       contenedor.add(p);
       contenedor.revalidate();
       contenedor.repaint();
    }

    // Función que reliza la inicialización de las tablas en la BD
    public final void cargaSQL() {
        
        try {
            // Hago coneccion con la DB
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
            // Creo la BD
            query = conn.createStatement();
            query.executeUpdate("CREATE DATABASE Arqueologos");
            // Creo tablas
            query = conn.createStatement();
            query.execute("CREATE TABLE IF NOT EXISTS Sitios("
                    + "S_Cod VARCHAR(20) PRIMARY KEY, "
                    + "S_Localidad VARCHAR(30) NOT NULL)");

            query.execute("CREATE TABLE IF NOT EXISTS Cuadriculas("
                    + "Cu_Cod VARCHAR(20) PRIMARY KEY, "
                    + "S_Cod_Dividido VARCHAR(20), "
                    + "FOREIGN KEY (S_Cod_Dividido) REFERENCES Sitios(S_Cod))");

            query.execute("CREATE TABLE IF NOT EXISTS Cajas("
                    + "Ca_Cod VARCHAR(20) PRIMARY KEY, "
                    + "Ca_Fecha DATE NOT NULL, "
                    + "Ca_Lugar VARCHAR(50) NOT NULL)");

            query.execute("CREATE TABLE IF NOT EXISTS Personas("
                    + "P_DNI CHAR(8) PRIMARY KEY, "
                    + "P_Nombre VARCHAR(50) NOT NULL, "
                    + "P_Apellido VARCHAR(50) NOT NULL, "
                    + "P_Email VARCHAR(70) NOT NULL UNIQUE, "
                    + "P_Telefono VARCHAR(15) NOT NULL UNIQUE)");

            query.execute("DO $$ BEGIN " +
              "IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tipo') THEN " +
              "CREATE TYPE tipo AS ENUM('L', 'C'); " +
              "END IF; " +
              "END $$;");
            
            query.execute("CREATE TABLE IF NOT EXISTS Objetos("
                    + "O_Cod VARCHAR(20) PRIMARY KEY, "
                    + "O_Nombre VARCHAR(50) NOT NULL, "
                    + "O_TipoExtraccion VARCHAR(50) NOT NULL, "
                    + "O_Alto FLOAT NOT NULL, "
                    + "O_Largo FLOAT NOT NULL, "
                    + "O_Espesor FLOAT NOT NULL, "
                    + "O_Peso FLOAT NOT NULL, "
                    + "O_Cantidad INT NOT NULL, "
                    + "O_FechaRegistro DATE NOT NULL, "
                    + "O_Descripcion VARCHAR(200) NOT NULL, "
                    + "O_Origen VARCHAR(50), "
                    + "Cu_Cod_Asocia VARCHAR(20), "
                    + "Ca_Cod_Contiene VARCHAR(20), "
                    + "P_DNI_Ingresa CHAR(8), "
                    + "O_Es tipo NOT NULL, "
                    + "FOREIGN KEY (Cu_Cod_Asocia) REFERENCES Cuadriculas(Cu_Cod), "
                    + "FOREIGN KEY (Ca_Cod_Contiene) REFERENCES Cajas(Ca_Cod), "
                    + "FOREIGN KEY (P_DNI_Ingresa) REFERENCES Personas(P_DNI) ON DELETE CASCADE)");

            query.execute("CREATE TABLE IF NOT EXISTS Liticos("
                    + "O_Cod VARCHAR(20) PRIMARY KEY, "
                    + "L_FechaCreacion INT NOT NULL, "
                    + "FOREIGN KEY (O_Cod) REFERENCES Objetos(O_Cod) ON DELETE CASCADE)");

            query.execute("CREATE TABLE IF NOT EXISTS Ceramicos("
                    + "O_Cod VARCHAR(20) PRIMARY KEY, "
                    + "C_Color VARCHAR(20) NOT NULL, "
                    + "FOREIGN KEY (O_Cod) REFERENCES Objetos(O_Cod) ON DELETE CASCADE)");

            // Instancio tablas
            File archivo = new File("Inserta_Datos2.sql");
            FileReader rArchivo = new FileReader(archivo);
            BufferedReader bArchivo = new BufferedReader(rArchivo);
            String line;
            Statement stmt = conn.createStatement();
            StringBuilder sql = new StringBuilder();
            while ((line = bArchivo.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("--")) {
                    sql.append(line);
                }
                // Ejecutar la sentencia si encontramos un ';'
                if (line.trim().endsWith(";")) {
                    stmt.execute(sql.toString());
                    sql = new StringBuilder(); // Reiniciar para la siguiente sentencia
                }
            }
            
            query.execute("INSERT INTO Personas VALUES(25544555 ,'Rodolphe', 'Rominov', 'rrominovm@sciencedaily.com', 7135986253)");
            query.execute("DELETE FROM Personas WHERE P_DNI = '40417189'");
            bArchivo.close();
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(background, "Hubo un error en la carga de datos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(background, "Hubo un error inesperado", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Función que realiza la consulta SELECT * 
    private void updateTabla(String nombreTabla) throws SQLException {
        query = conn.createStatement();
        result = query.executeQuery("SELECT * FROM " + nombreTabla);
        mapTablas.get(nombreTabla).setModel(resultToTable(result));
    }

    // Función auxiliar para convertir las consultas en tablas
    private static DefaultTableModel resultToTable(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Creamos las columnas
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // Creamos las filas
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }

    // Funcion que me permite eliminar una tupla, asumimos que el codigo de cada esquema se encuentra en la primer columna
    private void eliminarTupla(String nombreTabla) {
        try {
            String codigo = (mapTablas.get(nombreTabla).getValueAt(mapTablas.get(nombreTabla).getSelectedRow(), 0)).toString();
            if (JOptionPane.showConfirmDialog(null, "Está seguro de eliminar " + codigo + "?", "Mensaje de confirmación", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == 0) {
                String nombreColumnaCodigo = mapTablas.get(nombreTabla).getColumnName(0);
                String sql = "DELETE FROM " + nombreTabla + " WHERE " + nombreColumnaCodigo + " = ?";
                p_query = conn.prepareStatement(sql);
                p_query.setString(1, codigo.toUpperCase());
                p_query.executeQuery();
                updateTabla(nombreTabla);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(contenedor, "Esta fila no se puede eliminar ya que esta referenciada en la tabla objetos ", "Atención!", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarInformacionObjetos() {
        try {
            query = conn.createStatement();
            ResultSet rs;
            rs = query.executeQuery("SELECT COUNT(O_cod) AS count FROM Liticos");
            rs.next();
            cantidadLiticos.setText(String.valueOf(rs.getInt("count")));
            rs = query.executeQuery("SELECT COUNT(O_cod) AS count FROM Ceramicos");
            rs.next();
            cantidadCeramicos.setText(String.valueOf(rs.getInt("count")));
            rs = query.executeQuery("SELECT MAX(o_peso) AS MAX, AVG(o_peso) AS AVG, MIN(o_peso) AS MIN FROM Objetos");
            rs.next();
            pesoMaximoObjetos.setText(String.valueOf(rs.getDouble("MAX")));
            pesoPromedioObjetos.setText(String.valueOf(rs.getDouble("AVG")));
            pesoMinimoObjetos.setText(String.valueOf(rs.getDouble("MIN")));
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void actualizarInformacionResumen(){
        try{
            query = conn.createStatement();
            ResultSet rs;
            rs = query.executeQuery("SELECT COUNT(P_DNI) AS count FROM Personas");
            rs.next();
            cantidadArqueologos.setText(String.valueOf(rs.getInt("count")));
            rs = query.executeQuery("SELECT COUNT(cu_cod) AS count FROM Cuadriculas");
            rs.next();
            cantidadCuadriculas.setText(String.valueOf(rs.getInt("count")));
            rs = query.executeQuery("SELECT COUNT(o_cod) AS count FROM Objetos");
            rs.next();
            cantidadObjetos.setText(String.valueOf(rs.getInt("count")));
            rs = query.executeQuery("SELECT COUNT(ca_cod) AS count FROM Cajas");
            rs.next();
            cantidadCajas.setText(String.valueOf(rs.getInt("count")));
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void FechasCruzadas(Date fechaInicio, Date fechaFin) throws FechasCruzadas{
        if(fechaFin.getTime()-fechaInicio.getTime()<0) throw new FechasCruzadas();
    }
    
    public void cargarModificarTextFields(JTable table, JComponent[] components) throws ParseException{
        try{
            int selectedRow = table.getSelectedRow();
            for(int i = 0; i < components.length; i++){
                String value = table.getValueAt(selectedRow, i).toString();
                if(components[i] instanceof JTextField jTextField){
                    jTextField.setText(value);
                } else if (components[i] instanceof JDateChooser jDateChooser){
                    Date fecha = sdf.parse(value);
                    jDateChooser.setDate(fecha);
                } else if(components[i] instanceof JComboBox){
                    JComboBox<String> comboBox = (JComboBox<String>) components[i];
                    String valorParaSeleccionar = value != null ? value : "";
                    comboBox.setSelectedItem(valorParaSeleccionar);
                }
            }
        }
        catch(ArrayIndexOutOfBoundsException evt1) {
            JOptionPane.showMessageDialog(contenedor, "Seleccione un proyecto","Atención!",JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
        public static boolean isValidPhoneNumber(JTextField textField) {
        String phoneNumber = textField.getText();

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            // Considera si un campo vacío es válido para tu aplicación.
            // Para un número de teléfono, usualmente no lo sería.
            JOptionPane.showMessageDialog(null, "El campo de teléfono no puede estar vacío.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Caracteres permitidos además de los dígitos: '+', '(', ')', '-', ' '
        String allowedChars = "+()- ";

        for (char c : phoneNumber.toCharArray()) {
            // Verifica si el caracter es un dígito (0-9)
            // O si es uno de los caracteres especiales permitidos
            if (!Character.isDigit(c) && allowedChars.indexOf(c) == -1) {
                JOptionPane.showMessageDialog(null, "El número de teléfono solo puede contener números y los caracteres +, -, (, ), espacio.", "Entrada Inválida", JOptionPane.ERROR_MESSAGE);
                return false; // Se encontró un carácter no permitido
            }
        }
        return true; // Todos los caracteres son válidos
    }
        
            public static boolean isValidDNI(JTextField textField) {
        String dni = textField.getText();

        // 1. Verificar si el campo está vacío o solo contiene espacios en blanco
        if (dni == null || dni.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El campo DNI no puede estar vacío.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Eliminar cualquier punto o caracter que no sea un dígito, si se permiten formatos como 12.345.678
        // Para una validación estricta de solo números, omite esta línea.
        // String cleanedDNI = dni.replace(".", "").replace("-", "").trim();

        // Para el DNI argentino, asumimos solo dígitos. Si se ingresan puntos, el usuario debe quitarlos.
        String cleanedDNI = dni.trim(); // Solo eliminamos espacios al inicio/final

        // 2. Verificar que todos los caracteres sean dígitos
        for (char c : cleanedDNI.toCharArray()) {
            if (!Character.isDigit(c)) {
                JOptionPane.showMessageDialog(null, "El DNI solo puede contener caracteres numéricos (0-9).", "Entrada Inválida", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // 3. Verificar la longitud del DNI
        // Los DNI en Argentina suelen tener 7 u 8 dígitos.
        int length = cleanedDNI.length();
        if (length < 7 || length > 8) {
            JOptionPane.showMessageDialog(null, "El DNI debe tener entre 7 y 8 dígitos.", "Longitud Incorrecta", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Si pasa todas las validaciones, es un DNI válido
        return true;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        background = new javax.swing.JPanel();
        separadorHorizontalHeader = new javax.swing.JSeparator();
        separadorVerticalContainer = new javax.swing.JSeparator();
        contenedor = new javax.swing.JPanel();
        agregarArquelogoPanel = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        dniArqTF = new javax.swing.JTextField();
        nombreArqTF = new javax.swing.JTextField();
        apellidoArqTF = new javax.swing.JTextField();
        mailArqTF = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        telefonoArqTF = new javax.swing.JTextField();
        cancelarAgregarArqBtn = new javax.swing.JButton();
        ingresarAgregarArqBtn = new javax.swing.JButton();
        arqueologosPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaPersonas = new javax.swing.JTable();
        seleccionarVistaPersonasBtn = new javax.swing.JButton();
        modificarPersonasBtn = new javax.swing.JButton();
        agregarPersonasBtn = new javax.swing.JButton();
        eliminarPersonasBtn = new javax.swing.JButton();
        inicioPanel = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        agregarObjetosPanel = new javax.swing.JPanel();
        codigoObjTF = new javax.swing.JTextField();
        nombreObjTF = new javax.swing.JTextField();
        tipoExtraccionObjTF = new javax.swing.JTextField();
        altoObjTF = new javax.swing.JTextField();
        largoObjTF = new javax.swing.JTextField();
        espesorObjTF = new javax.swing.JTextField();
        pesoObjTF = new javax.swing.JTextField();
        cantidadObjTF = new javax.swing.JTextField();
        descripcionObjTF = new javax.swing.JTextField();
        origenObjTF = new javax.swing.JTextField();
        codigoCuadriculaObjTF = new javax.swing.JTextField();
        codigoCajaObjTF = new javax.swing.JTextField();
        dniPersonaObjTF = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        tipoObjCB = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        fechaRegistroObjTF = new com.toedter.calendar.JDateChooser();
        ingresarObjetoBtn = new javax.swing.JButton();
        cancelarObjetoBtn = new javax.swing.JButton();
        cajasPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaCajas = new javax.swing.JTable();
        modificarCajasBtn = new javax.swing.JButton();
        agregarCajasBtn = new javax.swing.JButton();
        eliminarCajasBtn = new javax.swing.JButton();
        seleccionarVistaCajasBtn = new javax.swing.JButton();
        mostrarPorPesoBtn = new javax.swing.JButton();
        resumenPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cantidadArqueologos = new javax.swing.JTextField();
        cantidadCuadriculas = new javax.swing.JTextField();
        cantidadObjetos = new javax.swing.JTextField();
        cantidadCajas = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        objetosPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaObjetos = new javax.swing.JTable();
        agregarObjetoBtn = new javax.swing.JButton();
        modificarObjetoBtn = new javax.swing.JButton();
        eliminarObjetoBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        codigoObjetoTextField = new javax.swing.JTextField();
        fechaInicioBuscar = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        fechaFinBuscar = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        separadorObjetos1 = new javax.swing.JSeparator();
        buscarCodigoObjeto = new javax.swing.JButton();
        buscarFecha = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cantidadLiticos = new javax.swing.JTextField();
        cantidadCeramicos = new javax.swing.JTextField();
        pesoMaximoObjetos = new javax.swing.JTextField();
        pesoPromedioObjetos = new javax.swing.JTextField();
        pesoMinimoObjetos = new javax.swing.JTextField();
        separadorObjetos2 = new javax.swing.JSeparator();
        mostrarTodosBtn = new javax.swing.JButton();
        modificarObjetosPanel = new javax.swing.JPanel();
        codigoObjModTF = new javax.swing.JTextField();
        nombreObjModTF = new javax.swing.JTextField();
        tipoExtraccionObjModTF = new javax.swing.JTextField();
        altoObjModTF = new javax.swing.JTextField();
        largoObjModTF = new javax.swing.JTextField();
        espesorObjModTF = new javax.swing.JTextField();
        pesoObjModTF = new javax.swing.JTextField();
        cantidadObjModTF = new javax.swing.JTextField();
        descripcionObjModTF = new javax.swing.JTextField();
        origenObjModTF = new javax.swing.JTextField();
        codigoCuadriculaObjModTF = new javax.swing.JTextField();
        codigoCajaObjModTF = new javax.swing.JTextField();
        dniPersonaObjModTF = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        tipoObjModCB = new javax.swing.JComboBox<>();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        fechaRegistroObjModTF = new com.toedter.calendar.JDateChooser();
        actualizarObjetoBtn = new javax.swing.JButton();
        cancelarObjetoModBtn = new javax.swing.JButton();
        modificarArqueologoPanel = new javax.swing.JPanel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        dniArqModTF = new javax.swing.JTextField();
        nombreArqModTF = new javax.swing.JTextField();
        apellidoArqModTF = new javax.swing.JTextField();
        mailArqModTF = new javax.swing.JTextField();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        telefonoArqModTF = new javax.swing.JTextField();
        cancelarModificarArqBtn = new javax.swing.JButton();
        actualizarModificarArqBtn = new javax.swing.JButton();
        header = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        todayDate = new javax.swing.JLabel();
        menu = new javax.swing.JPanel();
        resumenBtn = new javax.swing.JButton();
        objetosBtn = new javax.swing.JButton();
        arqueologosBtn = new javax.swing.JButton();
        cajasBtn = new javax.swing.JButton();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        background.setMaximumSize(new java.awt.Dimension(1000, 600));
        background.setMinimumSize(new java.awt.Dimension(1000, 600));
        background.setPreferredSize(new java.awt.Dimension(1000, 600));
        background.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        background.add(separadorHorizontalHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1000, 10));

        separadorVerticalContainer.setOrientation(javax.swing.SwingConstants.VERTICAL);
        background.add(separadorVerticalContainer, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, -1, 540));

        jLabel51.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel51.setText("Ingrese la información del arqueólogo");

        jLabel52.setText("DNI");

        jLabel53.setText("Nombre");

        jLabel54.setText("Apellido");

        dniArqTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dniArqTFActionPerformed(evt);
            }
        });

        mailArqTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mailArqTFActionPerformed(evt);
            }
        });

        jLabel55.setText("E-Mail");

        jLabel56.setText("Teléfono");

        cancelarAgregarArqBtn.setText("Cancelar");
        cancelarAgregarArqBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarAgregarArqBtnActionPerformed(evt);
            }
        });

        ingresarAgregarArqBtn.setText("Ingresar");
        ingresarAgregarArqBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ingresarAgregarArqBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout agregarArquelogoPanelLayout = new javax.swing.GroupLayout(agregarArquelogoPanel);
        agregarArquelogoPanel.setLayout(agregarArquelogoPanelLayout);
        agregarArquelogoPanelLayout.setHorizontalGroup(
            agregarArquelogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agregarArquelogoPanelLayout.createSequentialGroup()
                .addGroup(agregarArquelogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(agregarArquelogoPanelLayout.createSequentialGroup()
                        .addGap(233, 233, 233)
                        .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(agregarArquelogoPanelLayout.createSequentialGroup()
                        .addGap(265, 265, 265)
                        .addGroup(agregarArquelogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(agregarArquelogoPanelLayout.createSequentialGroup()
                                .addComponent(jLabel53)
                                .addGap(18, 18, 18)
                                .addComponent(nombreArqTF))
                            .addGroup(agregarArquelogoPanelLayout.createSequentialGroup()
                                .addGroup(agregarArquelogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(agregarArquelogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel54)
                                        .addGroup(agregarArquelogoPanelLayout.createSequentialGroup()
                                            .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(16, 16, 16)))
                                    .addComponent(jLabel55))
                                .addGap(18, 18, 18)
                                .addGroup(agregarArquelogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(mailArqTF)
                                    .addComponent(apellidoArqTF)
                                    .addComponent(dniArqTF)))
                            .addGroup(agregarArquelogoPanelLayout.createSequentialGroup()
                                .addComponent(jLabel56)
                                .addGap(18, 18, 18)
                                .addComponent(telefonoArqTF, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)))))
                .addContainerGap(274, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, agregarArquelogoPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(cancelarAgregarArqBtn)
                .addGap(32, 32, 32)
                .addComponent(ingresarAgregarArqBtn)
                .addGap(39, 39, 39))
        );
        agregarArquelogoPanelLayout.setVerticalGroup(
            agregarArquelogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agregarArquelogoPanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel51)
                .addGap(113, 113, 113)
                .addGroup(agregarArquelogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(dniArqTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(agregarArquelogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(nombreArqTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(agregarArquelogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(apellidoArqTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(agregarArquelogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mailArqTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55))
                .addGap(30, 30, 30)
                .addGroup(agregarArquelogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel56)
                    .addComponent(telefonoArqTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addGroup(agregarArquelogoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarAgregarArqBtn)
                    .addComponent(ingresarAgregarArqBtn))
                .addGap(26, 26, 26))
        );

        tablaPersonas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tablaPersonas);
        tablaPersonas.getTableHeader().setReorderingAllowed(false);

        seleccionarVistaPersonasBtn.setText("Mostrar objetos encontrados");
        seleccionarVistaPersonasBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarVistaPersonasBtnActionPerformed(evt);
            }
        });

        modificarPersonasBtn.setText("Modificar");
        modificarPersonasBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificarPersonasBtnActionPerformed(evt);
            }
        });

        agregarPersonasBtn.setText("Agregar");
        agregarPersonasBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarPersonasBtnActionPerformed(evt);
            }
        });

        eliminarPersonasBtn.setText("Eliminar");
        eliminarPersonasBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarPersonasBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout arqueologosPanelLayout = new javax.swing.GroupLayout(arqueologosPanel);
        arqueologosPanel.setLayout(arqueologosPanelLayout);
        arqueologosPanelLayout.setHorizontalGroup(
            arqueologosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, arqueologosPanelLayout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(arqueologosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(arqueologosPanelLayout.createSequentialGroup()
                        .addGap(167, 167, 167)
                        .addComponent(agregarPersonasBtn)
                        .addGap(112, 112, 112)
                        .addComponent(modificarPersonasBtn)
                        .addGap(112, 112, 112)
                        .addComponent(eliminarPersonasBtn))
                    .addGroup(arqueologosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(seleccionarVistaPersonasBtn)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 793, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30))
        );
        arqueologosPanelLayout.setVerticalGroup(
            arqueologosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(arqueologosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(seleccionarVistaPersonasBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(arqueologosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(eliminarPersonasBtn)
                    .addComponent(agregarPersonasBtn)
                    .addComponent(modificarPersonasBtn))
                .addContainerGap(58, Short.MAX_VALUE))
        );

        jLabel31.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel31.setText("Bienvenido!");

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dpto Informática.png"))); // NOI18N
        jLabel32.setText("jLabel32");

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/FCFMyN.png"))); // NOI18N
        jLabel33.setText("jLabel33");

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/UNSL.png"))); // NOI18N
        jLabel34.setText("jLabel34");

        javax.swing.GroupLayout inicioPanelLayout = new javax.swing.GroupLayout(inicioPanel);
        inicioPanel.setLayout(inicioPanelLayout);
        inicioPanelLayout.setHorizontalGroup(
            inicioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inicioPanelLayout.createSequentialGroup()
                .addGroup(inicioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(inicioPanelLayout.createSequentialGroup()
                        .addGap(324, 324, 324)
                        .addComponent(jLabel31))
                    .addGroup(inicioPanelLayout.createSequentialGroup()
                        .addGap(303, 303, 303)
                        .addGroup(inicioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(inicioPanelLayout.createSequentialGroup()
                                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(311, Short.MAX_VALUE))
        );
        inicioPanelLayout.setVerticalGroup(
            inicioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inicioPanelLayout.createSequentialGroup()
                .addGap(114, 114, 114)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(176, 176, 176)
                .addGroup(inicioPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel33)
                    .addComponent(jLabel34))
                .addGap(98, 98, 98))
        );

        altoObjTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altoObjTFActionPerformed(evt);
            }
        });

        largoObjTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                largoObjTFActionPerformed(evt);
            }
        });

        espesorObjTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                espesorObjTFActionPerformed(evt);
            }
        });

        origenObjTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                origenObjTFActionPerformed(evt);
            }
        });

        jLabel15.setText("Código");

        jLabel16.setText("Nombre");

        jLabel17.setText("Tipo extracción");

        jLabel18.setText("Alto");

        jLabel19.setText("Largo");

        jLabel20.setText("Espesor");

        jLabel21.setText("Peso");

        jLabel22.setText("Cantidad");

        jLabel23.setText("Fecha registro");

        jLabel24.setText("Descripción");

        jLabel25.setText("Origen");

        jLabel26.setText("Código cuadricula");

        jLabel27.setText("Código caja");

        jLabel28.setText("DNI Arqueologo");

        tipoObjCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ceramico", "Litico" }));
        tipoObjCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipoObjCBActionPerformed(evt);
            }
        });

        jLabel29.setText("Tipo");

        jLabel30.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel30.setText("Ingrese la información del objeto");

        ingresarObjetoBtn.setText("Ingresar");
        ingresarObjetoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ingresarObjetoBtnActionPerformed(evt);
            }
        });

        cancelarObjetoBtn.setText("Cancelar");
        cancelarObjetoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarObjetoBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout agregarObjetosPanelLayout = new javax.swing.GroupLayout(agregarObjetosPanel);
        agregarObjetosPanel.setLayout(agregarObjetosPanelLayout);
        agregarObjetosPanelLayout.setHorizontalGroup(
            agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                        .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                                .addGap(160, 160, 160)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(57, 57, 57)
                                .addComponent(codigoObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(195, 195, 195)
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(fechaRegistroObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                                .addGap(160, 160, 160)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(nombreObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(195, 195, 195)
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)
                                .addComponent(descripcionObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                                .addGap(160, 160, 160)
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(cantidadObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                                .addGap(241, 241, 241)
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                                .addGap(160, 160, 160)
                                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(327, 327, 327)
                                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(tipoExtraccionObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(201, 201, 201)
                                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(6, 6, 6)
                                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(origenObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(codigoCuadriculaObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                                .addGap(160, 160, 160)
                                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(41, 41, 41)
                                        .addComponent(espesorObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(195, 195, 195)
                                        .addComponent(jLabel28))
                                    .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(57, 57, 57)
                                        .addComponent(pesoObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(195, 195, 195)
                                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dniPersonaObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tipoObjCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                                .addGap(160, 160, 160)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(57, 57, 57)
                                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(altoObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                                        .addComponent(largoObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(195, 195, 195)
                                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(39, 39, 39)
                                        .addComponent(codigoCajaObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(73, 73, 73))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, agregarObjetosPanelLayout.createSequentialGroup()
                        .addComponent(cancelarObjetoBtn)
                        .addGap(10, 10, 10)
                        .addComponent(ingresarObjetoBtn)))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        agregarObjetosPanelLayout.setVerticalGroup(
            agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel30)
                .addGap(67, 67, 67)
                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(codigoObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel15))
                    .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel23))
                    .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(fechaRegistroObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(nombreObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16))
                    .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel24))
                    .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(descripcionObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel25)
                        .addComponent(origenObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tipoExtraccionObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17)))
                .addGap(17, 17, 17)
                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(codigoCuadriculaObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel18)
                                .addComponent(altoObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel26))))
                .addGap(16, 16, 16)
                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(largoObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel27)
                        .addComponent(codigoCajaObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14)
                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(espesorObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel28)
                                .addComponent(dniPersonaObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(11, 11, 11)
                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pesoObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(agregarObjetosPanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addComponent(tipoObjCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29))))
                .addGap(16, 16, 16)
                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(cantidadObjTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(agregarObjetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cancelarObjetoBtn)
                    .addComponent(ingresarObjetoBtn))
                .addContainerGap(68, Short.MAX_VALUE))
        );

        cajasPanel.setPreferredSize(new java.awt.Dimension(850, 541));

        tablaCajas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tablaCajas);
        tablaCajas.getTableHeader().setReorderingAllowed(false);

        modificarCajasBtn.setText("Modificar");
        modificarCajasBtn.setEnabled(false);
        modificarCajasBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificarCajasBtnActionPerformed(evt);
            }
        });

        agregarCajasBtn.setText("Agregar");
        agregarCajasBtn.setEnabled(false);
        agregarCajasBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarCajasBtnActionPerformed(evt);
            }
        });

        eliminarCajasBtn.setText("Eliminar");
        eliminarCajasBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarCajasBtnActionPerformed(evt);
            }
        });

        seleccionarVistaCajasBtn.setText("Mostrar cajas vacias");
        seleccionarVistaCajasBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarVistaCajasBtnActionPerformed(evt);
            }
        });

        mostrarPorPesoBtn.setText("Mostar por peso");
        mostrarPorPesoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostrarPorPesoBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout cajasPanelLayout = new javax.swing.GroupLayout(cajasPanel);
        cajasPanel.setLayout(cajasPanelLayout);
        cajasPanelLayout.setHorizontalGroup(
            cajasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cajasPanelLayout.createSequentialGroup()
                .addGap(542, 542, 542)
                .addComponent(mostrarPorPesoBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(seleccionarVistaCajasBtn)
                .addContainerGap(29, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cajasPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 787, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(cajasPanelLayout.createSequentialGroup()
                .addGap(191, 191, 191)
                .addComponent(agregarCajasBtn)
                .addGap(112, 112, 112)
                .addComponent(modificarCajasBtn)
                .addGap(112, 112, 112)
                .addComponent(eliminarCajasBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        cajasPanelLayout.setVerticalGroup(
            cajasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cajasPanelLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(cajasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(seleccionarVistaCajasBtn)
                    .addComponent(mostrarPorPesoBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(cajasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(agregarCajasBtn)
                    .addComponent(modificarCajasBtn)
                    .addComponent(eliminarCajasBtn))
                .addGap(24, 24, 24))
        );

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setText("Cantidad de arqueologos");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Cantidad de cuadrículas");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText("Cantidad de objetos");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Cantidad de cajas");

        cantidadArqueologos.setEditable(false);

        cantidadCuadriculas.setEditable(false);

        cantidadObjetos.setEditable(false);

        cantidadCajas.setEditable(false);

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel14.setText("Resumen de la información en el sistema");

        javax.swing.GroupLayout resumenPanelLayout = new javax.swing.GroupLayout(resumenPanel);
        resumenPanel.setLayout(resumenPanelLayout);
        resumenPanelLayout.setHorizontalGroup(
            resumenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resumenPanelLayout.createSequentialGroup()
                .addContainerGap(188, Short.MAX_VALUE)
                .addGroup(resumenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(resumenPanelLayout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(resumenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(resumenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(38, 38, 38)
                        .addGroup(resumenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cantidadObjetos, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cantidadCuadriculas, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cantidadArqueologos, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cantidadCajas, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 479, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(183, 183, 183))
        );
        resumenPanelLayout.setVerticalGroup(
            resumenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resumenPanelLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(jLabel14)
                .addGap(93, 93, 93)
                .addGroup(resumenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(cantidadArqueologos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(resumenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(cantidadCuadriculas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(resumenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cantidadObjetos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(resumenPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(cantidadCajas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(139, Short.MAX_VALUE))
        );

        objetosPanel.setPreferredSize(new java.awt.Dimension(862, 553));

        tablaObjetos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(tablaObjetos);
        tablaObjetos.getTableHeader().setReorderingAllowed(false);

        agregarObjetoBtn.setText("Agregar");
        agregarObjetoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarObjetoBtnActionPerformed(evt);
            }
        });

        modificarObjetoBtn.setText("Modificar");
        modificarObjetoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificarObjetoBtnActionPerformed(evt);
            }
        });

        eliminarObjetoBtn.setText("Eliminar");
        eliminarObjetoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarObjetoBtnActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Buscar por código de objeto");

        codigoObjetoTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codigoObjetoTextFieldActionPerformed(evt);
            }
        });

        jLabel2.setText("Buscar por fecha");

        jLabel3.setText("Inicio");

        jLabel4.setText("Fin");

        buscarCodigoObjeto.setText("Buscar");
        buscarCodigoObjeto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarCodigoObjetoActionPerformed(evt);
            }
        });

        buscarFecha.setText("Buscar");
        buscarFecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarFechaActionPerformed(evt);
            }
        });

        jLabel5.setText("Cantidad de líticos");

        jLabel6.setText("Cantidad de cerámicos");

        jLabel7.setText("Peso máximo");

        jLabel8.setText("Peso promedio");

        jLabel9.setText("Peso mínimo");

        cantidadLiticos.setEditable(false);
        cantidadLiticos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cantidadLiticosActionPerformed(evt);
            }
        });

        cantidadCeramicos.setEditable(false);

        pesoMaximoObjetos.setEditable(false);
        pesoMaximoObjetos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pesoMaximoObjetosActionPerformed(evt);
            }
        });

        pesoPromedioObjetos.setEditable(false);

        pesoMinimoObjetos.setEditable(false);

        mostrarTodosBtn.setText("Mostrar todos");
        mostrarTodosBtn.setEnabled(false);
        mostrarTodosBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostrarTodosBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout objetosPanelLayout = new javax.swing.GroupLayout(objetosPanel);
        objetosPanel.setLayout(objetosPanelLayout);
        objetosPanelLayout.setHorizontalGroup(
            objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(objetosPanelLayout.createSequentialGroup()
                .addGap(610, 610, 610)
                .addComponent(jLabel1))
            .addGroup(objetosPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(agregarObjetoBtn)
                        .addGap(92, 92, 92)
                        .addComponent(modificarObjetoBtn)
                        .addGap(93, 93, 93)
                        .addComponent(eliminarObjetoBtn)))
                .addGap(40, 40, 40)
                .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addComponent(codigoObjetoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(buscarCodigoObjeto))
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(mostrarTodosBtn))
                    .addComponent(separadorObjetos1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(93, 93, 93)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addComponent(fechaInicioBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(fechaFinBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(buscarFecha))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(cantidadLiticos, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(cantidadCeramicos, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(separadorObjetos2, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(79, 79, 79)
                        .addComponent(pesoMaximoObjetos, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(79, 79, 79)
                        .addComponent(pesoPromedioObjetos, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
                        .addComponent(pesoMinimoObjetos, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        objetosPanelLayout.setVerticalGroup(
            objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(objetosPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(codigoObjetoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buscarCodigoObjeto))
                        .addGap(13, 13, 13)
                        .addComponent(mostrarTodosBtn)
                        .addGap(13, 13, 13)
                        .addComponent(separadorObjetos1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addGap(14, 14, 14)
                        .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(4, 4, 4)
                        .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fechaInicioBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fechaFinBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addComponent(buscarFecha)
                        .addGap(13, 13, 13)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(cantidadLiticos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(cantidadCeramicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addComponent(separadorObjetos2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(pesoMaximoObjetos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(pesoPromedioObjetos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(pesoMinimoObjetos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21))
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(agregarObjetoBtn)
                            .addComponent(modificarObjetoBtn)
                            .addComponent(eliminarObjetoBtn))
                        .addGap(0, 19, Short.MAX_VALUE))))
        );

        modificarObjetosPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        codigoObjModTF.setEnabled(false);
        codigoObjModTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codigoObjModTFActionPerformed(evt);
            }
        });
        modificarObjetosPanel.add(codigoObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 111, 75, -1));
        modificarObjetosPanel.add(nombreObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 151, 75, -1));
        modificarObjetosPanel.add(tipoExtraccionObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 202, 75, -1));

        altoObjModTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altoObjModTFActionPerformed(evt);
            }
        });
        modificarObjetosPanel.add(altoObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 247, 75, -1));

        largoObjModTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                largoObjModTFActionPerformed(evt);
            }
        });
        modificarObjetosPanel.add(largoObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 292, 75, -1));

        espesorObjModTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                espesorObjModTFActionPerformed(evt);
            }
        });
        modificarObjetosPanel.add(espesorObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 328, 75, -1));
        modificarObjetosPanel.add(pesoObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 368, 75, -1));
        modificarObjetosPanel.add(cantidadObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 413, 75, -1));
        modificarObjetosPanel.add(descripcionObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 161, 81, -1));

        origenObjModTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                origenObjModTFActionPerformed(evt);
            }
        });
        modificarObjetosPanel.add(origenObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 210, 81, -1));

        codigoCuadriculaObjModTF.setEnabled(false);
        codigoCuadriculaObjModTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codigoCuadriculaObjModTFActionPerformed(evt);
            }
        });
        modificarObjetosPanel.add(codigoCuadriculaObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 250, 81, -1));

        codigoCajaObjModTF.setEnabled(false);
        modificarObjetosPanel.add(codigoCajaObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 292, 81, -1));

        dniPersonaObjModTF.setEnabled(false);
        modificarObjetosPanel.add(dniPersonaObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 340, 81, -1));

        jLabel35.setText("Código");
        modificarObjetosPanel.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 114, 43, -1));

        jLabel36.setText("Nombre");
        modificarObjetosPanel.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 154, 55, -1));

        jLabel37.setText("Tipo extracción");
        modificarObjetosPanel.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(154, 205, -1, -1));

        jLabel38.setText("Alto");
        modificarObjetosPanel.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 257, 43, -1));

        jLabel39.setText("Largo");
        modificarObjetosPanel.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 292, 43, -1));

        jLabel40.setText("Espesor");
        modificarObjetosPanel.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 331, 62, -1));

        jLabel41.setText("Peso");
        modificarObjetosPanel.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 371, 43, -1));

        jLabel42.setText("Cantidad");
        modificarObjetosPanel.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 416, 70, -1));

        jLabel43.setText("Fecha registro");
        modificarObjetosPanel.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(537, 121, 85, -1));

        jLabel44.setText("Descripción");
        modificarObjetosPanel.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(537, 164, 73, -1));

        jLabel45.setText("Origen");
        modificarObjetosPanel.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(537, 211, 43, -1));

        jLabel46.setText("Código cuadricula");
        modificarObjetosPanel.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 250, 104, -1));

        jLabel47.setText("Código caja");
        modificarObjetosPanel.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 295, 71, -1));

        jLabel48.setText("DNI Arqueologo");
        modificarObjetosPanel.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 338, -1, -1));

        tipoObjModCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ceramico", "Litico" }));
        tipoObjModCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tipoObjModCBActionPerformed(evt);
            }
        });
        modificarObjetosPanel.add(tipoObjModCB, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 380, -1, -1));

        jLabel49.setText("Tipo");
        modificarObjetosPanel.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 380, 43, -1));

        jLabel50.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel50.setText("Ingrese la información del objeto");
        modificarObjetosPanel.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(241, 18, 306, -1));
        modificarObjetosPanel.add(fechaRegistroObjModTF, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 115, -1, -1));

        actualizarObjetoBtn.setText("Actualizar");
        actualizarObjetoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actualizarObjetoBtnActionPerformed(evt);
            }
        });
        modificarObjetosPanel.add(actualizarObjetoBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(729, 447, -1, -1));

        cancelarObjetoModBtn.setText("Cancelar");
        cancelarObjetoModBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarObjetoModBtnActionPerformed(evt);
            }
        });
        modificarObjetosPanel.add(cancelarObjetoModBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(638, 447, -1, -1));

        jLabel57.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel57.setText("Ingrese la información del arqueólogo");

        jLabel58.setText("DNI");

        jLabel59.setText("Nombre");

        jLabel60.setText("Apellido");

        dniArqModTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dniArqModTFActionPerformed(evt);
            }
        });

        mailArqModTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mailArqModTFActionPerformed(evt);
            }
        });

        jLabel61.setText("E-Mail");

        jLabel62.setText("Teléfono");

        cancelarModificarArqBtn.setText("Cancelar");
        cancelarModificarArqBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelarModificarArqBtnActionPerformed(evt);
            }
        });

        actualizarModificarArqBtn.setText("Actualizar");
        actualizarModificarArqBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actualizarModificarArqBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout modificarArqueologoPanelLayout = new javax.swing.GroupLayout(modificarArqueologoPanel);
        modificarArqueologoPanel.setLayout(modificarArqueologoPanelLayout);
        modificarArqueologoPanelLayout.setHorizontalGroup(
            modificarArqueologoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modificarArqueologoPanelLayout.createSequentialGroup()
                .addGroup(modificarArqueologoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(modificarArqueologoPanelLayout.createSequentialGroup()
                        .addGap(233, 233, 233)
                        .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(modificarArqueologoPanelLayout.createSequentialGroup()
                        .addGap(265, 265, 265)
                        .addGroup(modificarArqueologoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(modificarArqueologoPanelLayout.createSequentialGroup()
                                .addComponent(jLabel59)
                                .addGap(18, 18, 18)
                                .addComponent(nombreArqModTF))
                            .addGroup(modificarArqueologoPanelLayout.createSequentialGroup()
                                .addGroup(modificarArqueologoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(modificarArqueologoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel60)
                                        .addGroup(modificarArqueologoPanelLayout.createSequentialGroup()
                                            .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(16, 16, 16)))
                                    .addComponent(jLabel61))
                                .addGap(18, 18, 18)
                                .addGroup(modificarArqueologoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(mailArqModTF)
                                    .addComponent(apellidoArqModTF)
                                    .addComponent(dniArqModTF)))
                            .addGroup(modificarArqueologoPanelLayout.createSequentialGroup()
                                .addComponent(jLabel62)
                                .addGap(18, 18, 18)
                                .addComponent(telefonoArqModTF, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)))))
                .addContainerGap(274, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, modificarArqueologoPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(cancelarModificarArqBtn)
                .addGap(32, 32, 32)
                .addComponent(actualizarModificarArqBtn)
                .addGap(39, 39, 39))
        );
        modificarArqueologoPanelLayout.setVerticalGroup(
            modificarArqueologoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modificarArqueologoPanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel57)
                .addGap(113, 113, 113)
                .addGroup(modificarArqueologoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(dniArqModTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(modificarArqueologoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel59)
                    .addComponent(nombreArqModTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(modificarArqueologoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel60)
                    .addComponent(apellidoArqModTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(modificarArqueologoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mailArqModTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61))
                .addGap(30, 30, 30)
                .addGroup(modificarArqueologoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel62)
                    .addComponent(telefonoArqModTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addGroup(modificarArqueologoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelarModificarArqBtn)
                    .addComponent(actualizarModificarArqBtn))
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout contenedorLayout = new javax.swing.GroupLayout(contenedor);
        contenedor.setLayout(contenedorLayout);
        contenedorLayout.setHorizontalGroup(
            contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 850, Short.MAX_VALUE)
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(objetosPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(resumenPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(arqueologosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(cajasPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(agregarObjetosPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(inicioPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(modificarObjetosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(agregarArquelogoPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(modificarArqueologoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        contenedorLayout.setVerticalGroup(
            contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 534, Short.MAX_VALUE)
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(objetosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(resumenPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(arqueologosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(cajasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(agregarObjetosPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(inicioPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(modificarObjetosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(agregarArquelogoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(modificarArqueologoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        background.add(contenedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 70, 850, 530));

        title.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        title.setText("Archeologic Data Base Manager");

        javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(header);
        header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 483, Short.MAX_VALUE)
                .addComponent(todayDate, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(todayDate, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
            .addGroup(headerLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(title)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        background.add(header, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 1000, 60));

        resumenBtn.setText("Resumen");
        resumenBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resumenBtnActionPerformed(evt);
            }
        });

        objetosBtn.setText("Objetos");
        objetosBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                objetosBtnActionPerformed(evt);
            }
        });

        arqueologosBtn.setText("Arquéologos");
        arqueologosBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arqueologosBtnActionPerformed(evt);
            }
        });

        cajasBtn.setText("Cajas");
        cajasBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cajasBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout menuLayout = new javax.swing.GroupLayout(menu);
        menu.setLayout(menuLayout);
        menuLayout.setHorizontalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cajasBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resumenBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(objetosBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(arqueologosBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        menuLayout.setVerticalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(objetosBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(arqueologosBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(cajasBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 128, Short.MAX_VALUE)
                .addComponent(resumenBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );

        background.add(menu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 150, 520));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void resumenBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resumenBtnActionPerformed
        actualizarInformacionResumen();
        showPanel(resumenPanel);
    }//GEN-LAST:event_resumenBtnActionPerformed

    private void arqueologosBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arqueologosBtnActionPerformed
        showPanel(arqueologosPanel);
    }//GEN-LAST:event_arqueologosBtnActionPerformed

    private void agregarObjetoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarObjetoBtnActionPerformed
        showPanel(agregarObjetosPanel);
    }//GEN-LAST:event_agregarObjetoBtnActionPerformed

    private void codigoObjetoTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codigoObjetoTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_codigoObjetoTextFieldActionPerformed

    private void cantidadLiticosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cantidadLiticosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cantidadLiticosActionPerformed

    private void pesoMaximoObjetosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pesoMaximoObjetosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pesoMaximoObjetosActionPerformed

    private void objetosBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_objetosBtnActionPerformed
        actualizarInformacionObjetos();
        showPanel(objetosPanel);
    }//GEN-LAST:event_objetosBtnActionPerformed

    private void seleccionarVistaPersonasBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarVistaPersonasBtnActionPerformed
        try {
            if (seleccionarVistaPersonasBtn.getText().equals("Mostrar objetos encontrados")) {
                seleccionarVistaPersonasBtn.setText("Mostrar personas");
                result = query.executeQuery("SELECT p_nombre, p_apellido, (SELECT COUNT(*) AS ObjetosEncontrados FROM objetos WHERE p_dni_ingresa = p_dni) FROM personas");
                tablaPersonas.setModel(resultToTable(result));
            }
            else{
                seleccionarVistaPersonasBtn.setText("Mostrar objetos encontrados");
                updateTabla("Personas");
            }
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_seleccionarVistaPersonasBtnActionPerformed

    private void seleccionarVistaCajasBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarVistaCajasBtnActionPerformed
        try {
            mostrarPorPesoBtn.setEnabled(true);
            query = conn.createStatement();
            if (seleccionarVistaCajasBtn.getText().equals("Mostrar cajas vacias")) {
                seleccionarVistaCajasBtn.setText("Mostrar todas las cajas");
                result = query.executeQuery("SELECT ca_cod, ca_lugar FROM Cajas WHERE ca_cod NOT IN (SELECT ca_cod_Contiene FROM Objetos)");
                tablaCajas.setModel(resultToTable(result));
            } else {
                seleccionarVistaCajasBtn.setText("Mostrar cajas vacias");
                updateTabla("Cajas");
            }
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_seleccionarVistaCajasBtnActionPerformed

    private void agregarCajasBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarCajasBtnActionPerformed
        
    }//GEN-LAST:event_agregarCajasBtnActionPerformed

    private void cajasBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cajasBtnActionPerformed
        try {
            mostrarPorPesoBtn.setEnabled(true);
            seleccionarVistaCajasBtn.setText("Mostrar cajas vacias");
            updateTabla("Cajas");
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        showPanel(cajasPanel);
    }//GEN-LAST:event_cajasBtnActionPerformed

    private void agregarPersonasBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarPersonasBtnActionPerformed
        showPanel(agregarArquelogoPanel);
    }//GEN-LAST:event_agregarPersonasBtnActionPerformed

    private void modificarPersonasBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificarPersonasBtnActionPerformed
       try {
            cargarModificarTextFields(tablaPersonas, personComponents);
            showPanel(modificarArqueologoPanel);
        } catch (ParseException ex) {
            System.getLogger(App.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_modificarPersonasBtnActionPerformed

    private void eliminarPersonasBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarPersonasBtnActionPerformed
        eliminarTupla("Personas");
    }//GEN-LAST:event_eliminarPersonasBtnActionPerformed

    private void modificarCajasBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificarCajasBtnActionPerformed
        try {
            tuplaSeleccionada = tablaCajas.getSelectedRow();
        } catch (ArrayIndexOutOfBoundsException evt1) {
            JOptionPane.showMessageDialog(contenedor, "Seleccione un Proyecto", "Atención!", JOptionPane.INFORMATION_MESSAGE);
        } catch (Throwable evt2) {
            JOptionPane.showMessageDialog(contenedor, "Error inesperado", "Atención!", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_modificarCajasBtnActionPerformed

    private void eliminarCajasBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarCajasBtnActionPerformed
        eliminarTupla("Cajas");
    }//GEN-LAST:event_eliminarCajasBtnActionPerformed

    private void modificarObjetoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificarObjetoBtnActionPerformed
        try {
            cargarModificarTextFields(tablaObjetos, objectComponents);
            showPanel(modificarObjetosPanel);
        } catch (ParseException ex) {
            System.getLogger(App.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_modificarObjetoBtnActionPerformed

    private void eliminarObjetoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarObjetoBtnActionPerformed
        try {
            eliminarTupla("Objetos");
            updateTabla("Objetos");
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_eliminarObjetoBtnActionPerformed

    private void buscarCodigoObjetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarCodigoObjetoActionPerformed
        try {
            p_query = conn.prepareStatement("SELECT * FROM Objetos WHERE O_Cod = ?");
            p_query.setString(1, codigoObjetoTextField.getText().toUpperCase());
            result = p_query.executeQuery();
            tablaObjetos.setModel(resultToTable(result));
            mostrarTodosBtn.setEnabled(true);
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_buscarCodigoObjetoActionPerformed

    private void mostrarTodosBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mostrarTodosBtnActionPerformed
        try {
            mostrarTodosBtn.setEnabled(false);
            updateTabla("Objetos");
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mostrarTodosBtnActionPerformed

    private void buscarFechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarFechaActionPerformed
        try {
            if (fechaInicioBuscar.getDate() == null || fechaFinBuscar.getDate() == null) {
                JOptionPane.showMessageDialog(contenedor, "Ingrese las fechas", "Atención", JOptionPane.WARNING_MESSAGE);
            } else {
                FechasCruzadas(fechaInicioBuscar.getDate(), fechaFinBuscar.getDate());
                query = conn.createStatement();
                result = query.executeQuery("SELECT * FROM Objetos WHERE o_fecharegistro BETWEEN " + "'" + sdf.format(fechaInicioBuscar.getDate()) + "'" + " AND " + "'" + sdf.format(fechaFinBuscar.getDate()) + "' ORDER BY o_fecharegistro");
                tablaObjetos.setModel(resultToTable(result));
            }
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FechasCruzadas ex) {
            JOptionPane.showMessageDialog(contenedor, "La fecha de incio no puede ser mayor que la fecha de fin", "Atención", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_buscarFechaActionPerformed

    private void altoObjTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altoObjTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_altoObjTFActionPerformed

    private void espesorObjTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_espesorObjTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_espesorObjTFActionPerformed

    private void ingresarObjetoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ingresarObjetoBtnActionPerformed
        try{
            if(codigoObjTF.getText().equals("") || nombreObjTF.getText().equals("") || 
                    tipoExtraccionObjTF.getText().equals("") || altoObjTF.getText().equals("") || 
                    largoObjTF.getText().equals("") || espesorObjTF.getText().equals("") || 
                    pesoObjTF.getText().equals("") || cantidadObjTF.getText().equals("") || 
                    fechaRegistroObjTF.getDate() == null || descripcionObjTF.getText().equals("") ||
                    origenObjTF.getText().equals("") || codigoCuadriculaObjTF.getText().equals("") || 
                    codigoCajaObjTF.getText().equals("") || dniPersonaObjTF.getText().equals("")){
                JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos...");  
            }
            else if(Float.parseFloat(altoObjTF.getText()) < 0 || Float.parseFloat(largoObjTF.getText()) < 0 || 
                    Float.parseFloat(espesorObjTF.getText()) < 0 || Float.parseFloat(pesoObjTF.getText()) < 0 || 
                    Integer.parseInt(cantidadObjTF.getText()) < 0 || Integer.parseInt(dniPersonaObjTF.getText()) < 0){
                JOptionPane.showMessageDialog(contenedor, "Ingrese un valor válido", "Error", JOptionPane.ERROR_MESSAGE);  
            }
            else{
                String sql = "INSERT INTO Objetos VALUES(?, ?, ?, ?, ?, ?, ?, ?, " + "'" + sdf.format(fechaRegistroObjTF.getDate()) + "'" + ", ?, ?, ?, ?, ?, ?::tipo)";
                p_query = conn.prepareStatement(sql);
                p_query.setString(1, codigoObjTF.getText().toUpperCase());
                p_query.setString(2, nombreObjTF.getText());
                p_query.setString(3, tipoExtraccionObjTF.getText());
                p_query.setFloat(4, Float.parseFloat(altoObjTF.getText()));
                p_query.setFloat(5, Float.parseFloat(largoObjTF.getText()));
                p_query.setFloat(6, Float.parseFloat(espesorObjTF.getText()));
                p_query.setFloat(7, Float.parseFloat(pesoObjTF.getText()));
                p_query.setInt(8, Integer.parseInt(cantidadObjTF.getText()));
                p_query.setString(9, descripcionObjTF.getText());
                p_query.setString(10, origenObjTF.getText());
                p_query.setString(11, codigoCuadriculaObjTF.getText().toUpperCase());
                p_query.setString(12, codigoCajaObjTF.getText().toUpperCase());
                p_query.setString(13, dniPersonaObjTF.getText());
                String tipoEnum = tipoObjCB.getSelectedItem().equals("Litico") ? "L" : "C";
                p_query.setString(14, tipoEnum);
                if(JOptionPane.showConfirmDialog(null, "Está seguro de agregar el objeto?", "Mensaje de confirmación", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == 0){
                    p_query.execute();
                    updateTabla("Objetos");
                    actualizarInformacionObjetos();
                }
                showPanel(objetosPanel);
            }
        } catch(NumberFormatException e){
            JOptionPane.showMessageDialog(contenedor, "Ingrese un valor valido","Error",JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(contenedor, "Verifique que todos los campos esten correctamente ingresados", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_ingresarObjetoBtnActionPerformed

    private void tipoObjCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoObjCBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tipoObjCBActionPerformed

    private void largoObjTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_largoObjTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_largoObjTFActionPerformed

    private void origenObjTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_origenObjTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_origenObjTFActionPerformed

    private void mostrarPorPesoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mostrarPorPesoBtnActionPerformed
        try {
            mostrarPorPesoBtn.setEnabled(false);
            query = conn.createStatement();
            result = query.executeQuery("SELECT ca_cod_contiene AS ca_cod, SUM(o_peso) AS peso FROM Objetos GROUP BY ca_cod_contiene");
            tablaCajas.setModel(resultToTable(result));
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mostrarPorPesoBtnActionPerformed

    private void cancelarObjetoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarObjetoBtnActionPerformed
        if(JOptionPane.showConfirmDialog(null, "Está seguro de cancelar?", "Mensaje de confirmación", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == 0){
            showPanel(objetosPanel);
        }
    }//GEN-LAST:event_cancelarObjetoBtnActionPerformed

    private void altoObjModTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altoObjModTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_altoObjModTFActionPerformed

    private void largoObjModTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_largoObjModTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_largoObjModTFActionPerformed

    private void espesorObjModTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_espesorObjModTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_espesorObjModTFActionPerformed

    private void origenObjModTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_origenObjModTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_origenObjModTFActionPerformed

    private void tipoObjModCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipoObjModCBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tipoObjModCBActionPerformed

    private void actualizarObjetoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualizarObjetoBtnActionPerformed
        try {
            if (nombreObjModTF.getText().equals("")
                    || tipoExtraccionObjModTF.getText().equals("") || altoObjModTF.getText().equals("")
                    || largoObjModTF.getText().equals("") || espesorObjModTF.getText().equals("")
                    || pesoObjModTF.getText().equals("") || cantidadObjModTF.getText().equals("")
                    || fechaRegistroObjModTF.getDate() == null || descripcionObjModTF.getText().equals("")
                    || origenObjModTF.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos...");
            } else if (Float.parseFloat(altoObjModTF.getText()) < 0 || Float.parseFloat(largoObjModTF.getText()) < 0
                    || Float.parseFloat(espesorObjModTF.getText()) < 0 || Float.parseFloat(pesoObjModTF.getText()) < 0
                    || Integer.parseInt(cantidadObjModTF.getText()) < 0) {
                JOptionPane.showMessageDialog(contenedor, "Ingrese un valor válido", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String sql = "UPDATE Objetos SET o_nombre = ?, o_tipoextraccion = ?, o_alto = ?, o_largo = ?, o_espesor = ?, o_peso = ?, o_cantidad = ?, o_fecharegistro = ?, o_descripcion = ?, o_origen = ?, o_es = ?::tipo WHERE o_cod = ?";
                p_query = conn.prepareStatement(sql);

                p_query.setString(1, nombreObjModTF.getText());
                p_query.setString(2, tipoExtraccionObjModTF.getText());
                p_query.setFloat(3, Float.parseFloat(altoObjModTF.getText()));
                p_query.setFloat(4, Float.parseFloat(largoObjModTF.getText()));
                p_query.setFloat(5, Float.parseFloat(espesorObjModTF.getText()));
                p_query.setFloat(6, Float.parseFloat(pesoObjModTF.getText()));
                p_query.setInt(7, Integer.parseInt(cantidadObjModTF.getText()));

                java.util.Date utilDate = fechaRegistroObjModTF.getDate();
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                p_query.setDate(8, sqlDate);

                p_query.setString(9, descripcionObjModTF.getText());
                p_query.setString(10, origenObjModTF.getText());
                String tipoEnum = tipoObjModCB.getSelectedItem().equals("Litico") ? "L" : "C";
                p_query.setString(11, tipoEnum);
                p_query.setString(12, codigoObjModTF.getText().toUpperCase()); // Este es el último parámetro (WHERE o_cod = ?)

                if (JOptionPane.showConfirmDialog(null, "Está seguro de modificar el objeto?", "Mensaje de confirmación", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == 0) {
                    p_query.execute();
                    updateTabla("Objetos");
                    actualizarInformacionObjetos();
                }
                showPanel(objetosPanel);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(contenedor, "Ingrese un valor válido en los campos numéricos", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Imprime la traza para depuración
        } catch (SQLException ex) {
            // Es CRÍTICO imprimir la traza de la excepción SQL para ver el mensaje exacto del error de la DB.
            ex.printStackTrace();
            JOptionPane.showMessageDialog(contenedor, "Error al modificar en la base de datos: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_actualizarObjetoBtnActionPerformed

    private void cancelarObjetoModBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarObjetoModBtnActionPerformed
        if (JOptionPane.showConfirmDialog(null, "Está seguro de cancelar?", "Mensaje de confirmación", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == 0) {
            showPanel(objetosPanel);
        }
    }//GEN-LAST:event_cancelarObjetoModBtnActionPerformed

    private void codigoCuadriculaObjModTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codigoCuadriculaObjModTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_codigoCuadriculaObjModTFActionPerformed

    private void codigoObjModTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codigoObjModTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_codigoObjModTFActionPerformed

    private void mailArqTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mailArqTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mailArqTFActionPerformed

    private void dniArqTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dniArqTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dniArqTFActionPerformed

    private void cancelarAgregarArqBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarAgregarArqBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cancelarAgregarArqBtnActionPerformed

    private void ingresarAgregarArqBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ingresarAgregarArqBtnActionPerformed
        try {
            if (dniArqModTF.getText().equals("") || nombreArqModTF.getText().equals("")
                    || apellidoArqModTF.getText().equals("") || mailArqModTF.getText().equals("")
                    || telefonoArqModTF.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos...");
            } else if (Integer.parseInt(dniArqModTF.getText()) < 0 || telefonoArqModTF.getText().length() < 10 
                        || telefonoArqModTF.getText().length() > 15
                        || !isValidPhoneNumber(telefonoArqModTF) || !isValidDNI(dniArqModTF)) {
                JOptionPane.showMessageDialog(contenedor, "Ingrese un valor válido", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String sql = "INSERT INTO Personas VALUES(?, ?, ?, ?, ?)";
                p_query = conn.prepareStatement(sql);
                p_query.setInt(1, Integer.parseInt(dniArqModTF.getText()));
                p_query.setString(2, nombreArqModTF.getText());
                p_query.setString(3, apellidoArqModTF.getText());
                p_query.setString(4, mailArqModTF.getText().toLowerCase());
                p_query.setString(5, telefonoArqModTF.getText());
                if (JOptionPane.showConfirmDialog(null, "Está seguro de agregar el arqueologo?", "Mensaje de confirmación", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == 0) {
                    p_query.execute();
                    updateTabla("Personas");
                }
                showPanel(arqueologosPanel);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(contenedor, "Ingrese un valor valido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(contenedor, "Verifique que todos los campos esten correctamente ingresados", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_ingresarAgregarArqBtnActionPerformed

    private void dniArqModTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dniArqModTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dniArqModTFActionPerformed

    private void mailArqModTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mailArqModTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mailArqModTFActionPerformed

    private void cancelarModificarArqBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelarModificarArqBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cancelarModificarArqBtnActionPerformed

    private void actualizarModificarArqBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualizarModificarArqBtnActionPerformed
        try {
            if (nombreArqModTF.getText().equals("")
                    || apellidoArqModTF.getText().equals("") || mailArqModTF.getText().equals("")
                    || telefonoArqModTF.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos...");
            } else if (telefonoArqModTF.getText().length() < 9 
                        || telefonoArqModTF.getText().length() > 15
                        || !isValidPhoneNumber(telefonoArqModTF)) {
                JOptionPane.showMessageDialog(contenedor, "Ingrese un valor válido", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                String sql = "UPDATE Personas SET p_nombre = ?, p_apellido = ?, p_email = ?, p_telefono = ? WHERE p_dni = ?";
                p_query = conn.prepareStatement(sql);
                
                p_query.setString(1, nombreArqModTF.getText());
                p_query.setString(2, apellidoArqModTF.getText());
                p_query.setString(3, mailArqModTF.getText());
                p_query.setString(4, telefonoArqModTF.getText());
                p_query.setString(5, dniArqModTF.getText());
                if (JOptionPane.showConfirmDialog(null, "Está seguro de modificar el arqueologo?", "Mensaje de confirmación", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == 0) {
                    p_query.execute();
                    updateTabla("Personas");
                }
                showPanel(arqueologosPanel);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(contenedor, "Ingrese un valor valido", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(contenedor, "Verifique que todos los campos esten correctamente ingresados", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_actualizarModificarArqBtnActionPerformed

    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        FlatMacLightLaf.setup();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new App().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton actualizarModificarArqBtn;
    private javax.swing.JButton actualizarObjetoBtn;
    private javax.swing.JPanel agregarArquelogoPanel;
    private javax.swing.JButton agregarCajasBtn;
    private javax.swing.JButton agregarObjetoBtn;
    private javax.swing.JPanel agregarObjetosPanel;
    private javax.swing.JButton agregarPersonasBtn;
    private javax.swing.JTextField altoObjModTF;
    private javax.swing.JTextField altoObjTF;
    private javax.swing.JTextField apellidoArqModTF;
    private javax.swing.JTextField apellidoArqTF;
    private javax.swing.JButton arqueologosBtn;
    private javax.swing.JPanel arqueologosPanel;
    private javax.swing.JPanel background;
    private javax.swing.JButton buscarCodigoObjeto;
    private javax.swing.JButton buscarFecha;
    private javax.swing.JButton cajasBtn;
    private javax.swing.JPanel cajasPanel;
    private javax.swing.JButton cancelarAgregarArqBtn;
    private javax.swing.JButton cancelarModificarArqBtn;
    private javax.swing.JButton cancelarObjetoBtn;
    private javax.swing.JButton cancelarObjetoModBtn;
    private javax.swing.JTextField cantidadArqueologos;
    private javax.swing.JTextField cantidadCajas;
    private javax.swing.JTextField cantidadCeramicos;
    private javax.swing.JTextField cantidadCuadriculas;
    private javax.swing.JTextField cantidadLiticos;
    private javax.swing.JTextField cantidadObjModTF;
    private javax.swing.JTextField cantidadObjTF;
    private javax.swing.JTextField cantidadObjetos;
    private javax.swing.JTextField codigoCajaObjModTF;
    private javax.swing.JTextField codigoCajaObjTF;
    private javax.swing.JTextField codigoCuadriculaObjModTF;
    private javax.swing.JTextField codigoCuadriculaObjTF;
    private javax.swing.JTextField codigoObjModTF;
    private javax.swing.JTextField codigoObjTF;
    private javax.swing.JTextField codigoObjetoTextField;
    private javax.swing.JPanel contenedor;
    private javax.swing.JTextField descripcionObjModTF;
    private javax.swing.JTextField descripcionObjTF;
    private javax.swing.JTextField dniArqModTF;
    private javax.swing.JTextField dniArqTF;
    private javax.swing.JTextField dniPersonaObjModTF;
    private javax.swing.JTextField dniPersonaObjTF;
    private javax.swing.JButton eliminarCajasBtn;
    private javax.swing.JButton eliminarObjetoBtn;
    private javax.swing.JButton eliminarPersonasBtn;
    private javax.swing.JTextField espesorObjModTF;
    private javax.swing.JTextField espesorObjTF;
    private com.toedter.calendar.JDateChooser fechaFinBuscar;
    private com.toedter.calendar.JDateChooser fechaInicioBuscar;
    private com.toedter.calendar.JDateChooser fechaRegistroObjModTF;
    private com.toedter.calendar.JDateChooser fechaRegistroObjTF;
    private javax.swing.JPanel header;
    private javax.swing.JButton ingresarAgregarArqBtn;
    private javax.swing.JButton ingresarObjetoBtn;
    private javax.swing.JPanel inicioPanel;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField largoObjModTF;
    private javax.swing.JTextField largoObjTF;
    private javax.swing.JTextField mailArqModTF;
    private javax.swing.JTextField mailArqTF;
    private javax.swing.JPanel menu;
    private javax.swing.JPanel modificarArqueologoPanel;
    private javax.swing.JButton modificarCajasBtn;
    private javax.swing.JButton modificarObjetoBtn;
    private javax.swing.JPanel modificarObjetosPanel;
    private javax.swing.JButton modificarPersonasBtn;
    private javax.swing.JButton mostrarPorPesoBtn;
    private javax.swing.JButton mostrarTodosBtn;
    private javax.swing.JTextField nombreArqModTF;
    private javax.swing.JTextField nombreArqTF;
    private javax.swing.JTextField nombreObjModTF;
    private javax.swing.JTextField nombreObjTF;
    private javax.swing.JButton objetosBtn;
    private javax.swing.JPanel objetosPanel;
    private javax.swing.JTextField origenObjModTF;
    private javax.swing.JTextField origenObjTF;
    private javax.swing.JTextField pesoMaximoObjetos;
    private javax.swing.JTextField pesoMinimoObjetos;
    private javax.swing.JTextField pesoObjModTF;
    private javax.swing.JTextField pesoObjTF;
    private javax.swing.JTextField pesoPromedioObjetos;
    private javax.swing.JButton resumenBtn;
    private javax.swing.JPanel resumenPanel;
    private javax.swing.JButton seleccionarVistaCajasBtn;
    private javax.swing.JButton seleccionarVistaPersonasBtn;
    private javax.swing.JSeparator separadorHorizontalHeader;
    private javax.swing.JSeparator separadorObjetos1;
    private javax.swing.JSeparator separadorObjetos2;
    private javax.swing.JSeparator separadorVerticalContainer;
    private javax.swing.JTable tablaCajas;
    private javax.swing.JTable tablaObjetos;
    private javax.swing.JTable tablaPersonas;
    private javax.swing.JTextField telefonoArqModTF;
    private javax.swing.JTextField telefonoArqTF;
    private javax.swing.JTextField tipoExtraccionObjModTF;
    private javax.swing.JTextField tipoExtraccionObjTF;
    private javax.swing.JComboBox<String> tipoObjCB;
    private javax.swing.JComboBox<String> tipoObjModCB;
    private javax.swing.JLabel title;
    private javax.swing.JLabel todayDate;
    // End of variables declaration//GEN-END:variables
}
