import java.awt.Color;
import java.util.prefs.Preferences;

public class Menu2 extends javax.swing.JFrame {

    Preferences prefs = Preferences.userNodeForPackage(getClass());

    public Menu2() {
        initComponents();

        setLocationRelativeTo(null);

        xText.setText("" + prefs.getInt("x", 16));
        yText.setText("" + prefs.getInt("y", 16));
        minesText.setText("" + prefs.getInt("mines", 52));
        tileSizeText.setText("" + prefs.getInt("tileSize", 28));
        tileColorText.setText(prefs.get("color", "#008000"));
        botCheck.setSelected(prefs.getBoolean("bot", false));
        getContentPane().setBackground(Color.decode(tileColorText.getText()));

        this.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        xText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        yText = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        minesText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tileSizeText = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tileColorText = new javax.swing.JTextField();
        botCheck = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 128, 0));
        setResizable(false);

        playButton.setText("Play");
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("Title.png"))); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));
        jPanel1.setLayout(new java.awt.GridLayout(5, 2));

        jLabel2.setText(" Horizontal");
        jPanel1.add(jLabel2);

        xText.setText("16");
        jPanel1.add(xText);

        jLabel3.setText(" Vertical");
        jPanel1.add(jLabel3);

        yText.setText("16");
        jPanel1.add(yText);

        jLabel4.setText(" Mines");
        jPanel1.add(jLabel4);

        minesText.setText("32");
        jPanel1.add(minesText);

        jLabel1.setText(" Tile size");
        jPanel1.add(jLabel1);

        tileSizeText.setText("17");
        tileSizeText.setName("tileSizeText"); // NOI18N
        tileSizeText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tileSizeTextActionPerformed(evt);
            }
        });
        jPanel1.add(tileSizeText);

        jLabel5.setText(" Color-RGB");
        jPanel1.add(jLabel5);

        tileColorText.setText("#008000");
        jPanel1.add(tileColorText);

        botCheck.setText("Bot");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6)
                    .addComponent(playButton)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botCheck))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(44, 44, 44)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed

        if (Integer.parseInt(minesText.getText()) > Integer.parseInt(xText.getText()) * Integer.parseInt(yText.getText())) {
            playButton.setText("Error");
        } else {
            new Minesweeper2(Integer.parseInt(xText.getText()), Integer.parseInt(yText.getText()), Integer.parseInt(minesText.getText()), Color.decode(tileColorText.getText()), Integer.parseInt(tileSizeText.getText()), botCheck.isSelected());
            dispose();

            prefs.putInt("x", Integer.parseInt(xText.getText()));
            prefs.putInt("y", Integer.parseInt(yText.getText()));
            prefs.putInt("mines", Integer.parseInt(minesText.getText()));
            prefs.putInt("tileSize", Integer.parseInt(tileSizeText.getText()));
            prefs.put("color", tileColorText.getText());
            prefs.putBoolean("bot", botCheck.isSelected());
        }
    }//GEN-LAST:event_playButtonActionPerformed

    private void tileSizeTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tileSizeTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tileSizeTextActionPerformed

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
            java.util.logging.Logger.getLogger(Menu2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Menu2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Menu2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Menu2().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox botCheck;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField minesText;
    private javax.swing.JButton playButton;
    private javax.swing.JTextField tileColorText;
    private javax.swing.JTextField tileSizeText;
    private javax.swing.JTextField xText;
    private javax.swing.JTextField yText;
    // End of variables declaration//GEN-END:variables
}
