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
    private static final String DB_PWD = "1414";
    
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        todayDate = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        background.setMaximumSize(new java.awt.Dimension(1000, 600));
        background.setMinimumSize(new java.awt.Dimension(1000, 600));
        background.setPreferredSize(new java.awt.Dimension(1000, 600));
        background.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 445, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("tab2", jPanel2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 445, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("tab1", jPanel1);

        background.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 1000, 480));

        title.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        title.setText("Archeologic Data Base Manager");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 452, Short.MAX_VALUE)
                .addComponent(todayDate, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(todayDate, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        background.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -4, 1000, 90));
        background.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 74, 1246, 10));

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

    public void cargaSQL() throws SQLException{
         try{
            File archivo = new File ("C:\\Users\\Jhon\\Documents\\GitHub\\Laboratorio2DB\\Inserta_Datos2.sql");
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
    private javax.swing.JPanel background;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel title;
    private javax.swing.JLabel todayDate;
    // End of variables declaration//GEN-END:variables
}
