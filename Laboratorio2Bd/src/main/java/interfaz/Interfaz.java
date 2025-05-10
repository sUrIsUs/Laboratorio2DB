/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package interfaz;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Interfaz extends javax.swing.JFrame {

    private static Statement query = null;
    private static Connection conn = null;
    
    public Interfaz() throws SQLException {
        initComponents();
        
        query = conn.createStatement();
        query.execute("CREATE TABLE IF NOT EXISTS Sitios("
                + "S_Cod VARCHAR(20) PRIMARY KEY, "
                + "S_Localidad VARCHAR(30) NOT NULL))");
        
        query.execute("CREATE TABLE IF NOT EXISTS Cuadriculas("
                + "Cu_Cod VARCHAR(20) PRIMARY KEY, "
                + "S_Cod_Dividido, "
                + "FOREIGN KEY (S_Cod_Dividido) FROM Sitios(S_Cod))");
    
        query.execute("CREATE TABLE IF NOT EXISTS Cajas("
                + "Ca_Cod VARCHAR(20) PRIMARY KEY, "
                + "Ca_Fecha DATE() NOT NULL, "
                + "Ca_Lugar VARCHAR(50))");
        
        query.execute("CREATE TABLE IF NOT EXISTS Personas("
                + "P_DNI CHAR(8) PRIMARY KEY, "
                + "P_Nombre VARCHAR(50) NOT NULL, "
                + "P_Apellido VARCHAR(50) NOT NULL, "
                + "P_Email VARCHAR(70) NOT NULL UNIQUE, "
                + "P_Telefono VARCHAR(15) NOT NULL UNIQUE)");
        
        query.execute("CREATE TABLE IF NOT EXISTS Objetos("
                + "O_Cod VARCHAR(20) PRIMARY KEY, "
                + "O_Nombre VARCHAR(50) NOT NULL UNIQUE, "
                + "O_TipoExtraccion VARCHAR(50) NOT NULL, "
                + "O_Alto DOUBLE NOT NULL, "
                + "O_Largo DOUBLE NOT NULL, "
                + "O_Espesor DOUBLE NOT NULL, "
                + "O_Peso DOUBLE NOT NULL, "
                + "O_Cantidad INT NOT NULL, "
                + "O_FechaRegistro DATE() NOT NULL, "
                + "O_Descripcion VARCHAR(200) NOT NULL, "
                + "O_Origen VARCHAR(50), "
                + "Cu_Cod_Asocia VARCHAR(20) PRIMARY KEY, "
                + "Ca_Cod_Contiene VARCHAR(20) PRIMARY KEY, "
                + "P_DNI_Ingresa CHAR(8) PRIMARY KEY, "
                + "O_Es ENUM('Liticos, 'Ceramicos') NOT NULL, "
                + "FOREIGN KEY (Cu_Cod_Asocia) FROM Cuadriculas(Cu_Cod))"
                + "FOREIGN KEY (Ca_Cod_Contiene) FROM Cajas(Ca_Cod))"
                + "FOREIGN KEY (P_DNI_Ingresa) FROM Personas(P_DNI))");
        
        query.execute("CREATE TABLE IF NOT EXIST Liticos("
                + "O_Cod VARCHAR(20) PRIMARY KEY, "
                + "L_FechaCreacion DATE NOT NULL"
                + "FOREIGN KEY (O_Cod) FROM Objetos(O_Cod))");
        
        query.execute("CREATE TABLE IF NOT EXIST Ceramicos("
                + "O_Cod VARCHAR(20) PRIMARY KEY, "
                + "L_Color VARCHAR(20) NOT NULL"
                + "FOREIGN KEY (O_Cod) FROM Objetos(O_Cod))");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        background = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1280, 720));
        setResizable(false);

        background.setMaximumSize(new java.awt.Dimension(1280, 720));
        background.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1188, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 546, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("tab1", jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1188, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 546, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("tab2", jPanel2);

        background.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, -1, -1));

        jLabel2.setText("jLabel2");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1123, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 48, Short.MAX_VALUE))
        );

        background.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 6, 1240, -1));
        background.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 74, 1246, 10));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(background, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(background, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        FlatMacLightLaf.setup();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Interfaz().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel background;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
