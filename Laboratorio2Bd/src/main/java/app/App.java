/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package app;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


public class App extends javax.swing.JFrame {
    
    LocalDate now = LocalDate.now();
    int day = now.getDayOfMonth(), month = now.getMonthValue(), year = now.getYear();
    String [] months = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    // 
    private static final String DB_NAME = "archeologics";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/" + DB_NAME;
    private static final String DB_USER = "postgres";
    private static final String DB_PWD = "admin";
    
    private static Statement query = null;
    private static Connection conn = null;
    
    public App() throws SQLException {
        
        initComponents();
        // Muestro fecha
        todayDate.setText("Hoy es " + day + " de " + months[month - 1] + " de " + year);
        // Creo tablas
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
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
      
        query.execute(/*"CREATE TYPE tipo AS ENUM('L', 'C'); "*/
                "CREATE TABLE IF NOT EXISTS Objetos("
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
                + "FOREIGN KEY (P_DNI_Ingresa) REFERENCES Personas(P_DNI))");
       
        query.execute("CREATE TABLE IF NOT EXISTS Liticos("
                + "O_Cod VARCHAR(20) PRIMARY KEY, "
                + "L_FechaCreacion INT NOT NULL, "
                + "FOREIGN KEY (O_Cod) REFERENCES Objetos(O_Cod))");
        
        query.execute("CREATE TABLE IF NOT EXISTS Ceramicos("
                + "O_Cod VARCHAR(20) PRIMARY KEY, "
                + "C_Color VARCHAR(20) NOT NULL, "
                + "FOREIGN KEY (O_Cod) REFERENCES Objetos(O_Cod))");
//        cargaSQL();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        background = new javax.swing.JPanel();
        separadorVerticalContainer = new javax.swing.JSeparator();
        separadorHorizontalHeader = new javax.swing.JSeparator();
        contenedor = new javax.swing.JPanel();
        objetosPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        separadorObjetos1 = new javax.swing.JSeparator();
        buscarCodigoObjeto = new javax.swing.JButton();
        buscarFecha = new javax.swing.JButton();
        infoGeneralPanel = new javax.swing.JPanel();
        arqueologosPanel = new javax.swing.JPanel();
        header = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        todayDate = new javax.swing.JLabel();
        menu = new javax.swing.JPanel();
        infoGeneralBtn = new javax.swing.JButton();
        objetosBtn = new javax.swing.JButton();
        arqueologosBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        background.setMaximumSize(new java.awt.Dimension(1000, 600));
        background.setMinimumSize(new java.awt.Dimension(1000, 600));
        background.setPreferredSize(new java.awt.Dimension(1000, 600));
        background.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        separadorVerticalContainer.setOrientation(javax.swing.SwingConstants.VERTICAL);
        background.add(separadorVerticalContainer, new org.netbeans.lib.awtextra.AbsoluteConstraints(147, 70, -1, 540));
        background.add(separadorHorizontalHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 1000, -1));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("Agregar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Modificar");

        jButton3.setText("Eliminar");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Buscar por código de objeto");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Buscar por fecha");

        jLabel3.setText("Inicio");

        jLabel4.setText("Fin");

        buscarCodigoObjeto.setText("Buscar");

        buscarFecha.setText("Buscar");

        javax.swing.GroupLayout objetosPanelLayout = new javax.swing.GroupLayout(objetosPanel);
        objetosPanel.setLayout(objetosPanelLayout);
        objetosPanelLayout.setHorizontalGroup(
            objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(objetosPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(objetosPanelLayout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(objetosPanelLayout.createSequentialGroup()
                                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(43, 43, 43)
                                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(objetosPanelLayout.createSequentialGroup()
                                        .addGap(31, 31, 31)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(68, 68, 68))))
                            .addGroup(objetosPanelLayout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(objetosPanelLayout.createSequentialGroup()
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(buscarCodigoObjeto))
                                    .addGroup(objetosPanelLayout.createSequentialGroup()
                                        .addGap(31, 31, 31)
                                        .addComponent(jLabel1))
                                    .addComponent(separadorObjetos1, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(objetosPanelLayout.createSequentialGroup()
                                .addGap(101, 101, 101)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(10, Short.MAX_VALUE))
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(buscarFecha)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(objetosPanelLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(jButton1)
                .addGap(92, 92, 92)
                .addComponent(jButton2)
                .addGap(93, 93, 93)
                .addComponent(jButton3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        objetosPanelLayout.setVerticalGroup(
            objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(objetosPanelLayout.createSequentialGroup()
                .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(objetosPanelLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buscarCodigoObjeto))
                        .addGap(18, 18, 18)
                        .addComponent(separadorObjetos1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(15, 15, 15)
                        .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buscarFecha)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(objetosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        javax.swing.GroupLayout infoGeneralPanelLayout = new javax.swing.GroupLayout(infoGeneralPanel);
        infoGeneralPanel.setLayout(infoGeneralPanelLayout);
        infoGeneralPanelLayout.setHorizontalGroup(
            infoGeneralPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 850, Short.MAX_VALUE)
        );
        infoGeneralPanelLayout.setVerticalGroup(
            infoGeneralPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 540, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout arqueologosPanelLayout = new javax.swing.GroupLayout(arqueologosPanel);
        arqueologosPanel.setLayout(arqueologosPanelLayout);
        arqueologosPanelLayout.setHorizontalGroup(
            arqueologosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 850, Short.MAX_VALUE)
        );
        arqueologosPanelLayout.setVerticalGroup(
            arqueologosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 540, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout contenedorLayout = new javax.swing.GroupLayout(contenedor);
        contenedor.setLayout(contenedorLayout);
        contenedorLayout.setHorizontalGroup(
            contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(arqueologosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(objetosPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(infoGeneralPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        contenedorLayout.setVerticalGroup(
            contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(arqueologosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(objetosPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(infoGeneralPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        background.add(contenedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 60, 850, 540));

        title.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        title.setText("Archeologic Data Base Manager");

        javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(header);
        header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 471, Short.MAX_VALUE)
                .addComponent(todayDate, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerLayout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addGroup(headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(title)
                    .addComponent(todayDate, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
        );

        background.add(header, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 1000, 70));

        infoGeneralBtn.setText("Info. General");
        infoGeneralBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoGeneralBtnActionPerformed(evt);
            }
        });

        objetosBtn.setText("Objetos");

        arqueologosBtn.setText("Arquéologos");
        arqueologosBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arqueologosBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout menuLayout = new javax.swing.GroupLayout(menu);
        menu.setLayout(menuLayout);
        menuLayout.setHorizontalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infoGeneralBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(objetosBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(arqueologosBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        menuLayout.setVerticalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(infoGeneralBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addComponent(objetosBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addComponent(arqueologosBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(223, Short.MAX_VALUE))
        );

        background.add(menu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 150, 530));

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

    private void infoGeneralBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoGeneralBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_infoGeneralBtnActionPerformed

    private void arqueologosBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arqueologosBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_arqueologosBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    public void cargaSQL() throws SQLException{
         try{
            File archivo = new File ("Inserta_Datos2.sql");
            FileReader rArchivo = new FileReader(archivo);
            BufferedReader bArchivo = new BufferedReader(rArchivo);
            String line;
            Statement stmt = conn.createStatement();
            StringBuilder sql = new StringBuilder();
            while((line = bArchivo.readLine()) != null){
                if (!line.trim().isEmpty() && !line.startsWith("--")) {
                    sql.append(line);
                }
                // Ejecutar la sentencia si encontramos un ';'
                if (line.trim().endsWith(";")) {
                    stmt.execute(sql.toString());
                    sql = new StringBuilder(); // Reiniciar para la siguiente sentencia
                }
            }
            bArchivo.close();
         }
         catch(FileNotFoundException ex){
            JOptionPane.showMessageDialog(background, "Hubo un error en la carga de datos","Error",JOptionPane.ERROR_MESSAGE);
         }
         catch(IOException ex){
            JOptionPane.showMessageDialog(background, "Hubo un error inesperado","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
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
    private javax.swing.JButton arqueologosBtn;
    private javax.swing.JPanel arqueologosPanel;
    private javax.swing.JPanel background;
    private javax.swing.JButton buscarCodigoObjeto;
    private javax.swing.JButton buscarFecha;
    private javax.swing.JPanel contenedor;
    private javax.swing.JPanel header;
    private javax.swing.JButton infoGeneralBtn;
    private javax.swing.JPanel infoGeneralPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel menu;
    private javax.swing.JButton objetosBtn;
    private javax.swing.JPanel objetosPanel;
    private javax.swing.JSeparator separadorHorizontalHeader;
    private javax.swing.JSeparator separadorObjetos1;
    private javax.swing.JSeparator separadorVerticalContainer;
    private javax.swing.JLabel title;
    private javax.swing.JLabel todayDate;
    // End of variables declaration//GEN-END:variables
}
