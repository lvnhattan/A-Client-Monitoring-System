/*
 * Created by JFormDesigner on Thu Jun 10 11:35:25 ICT 2021
 */

package Client.ConnectPort;



import Config.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author nhat tan
 */
public class FormConnectPort extends JFrame {

    public FormConnectPort() {
        initComponents();
    }
    public static int PORT;
    public static Socket clientSocket;
    public PrintWriter out;
    public BufferedReader in;
    public boolean isrunning ;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FormConnectPort frame = new FormConnectPort();
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(frame);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void btnConnectActionPerformed(ActionEvent e) {
        // TODO add your code here
        start();
        if(isrunning)
        {
            JOptionPane.showMessageDialog(this,"Success.");
            this.setVisible(false);
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Fail. Please Check Your Port");
        }

    }

    public void start() {
        try {
            PORT = Integer.parseInt(txtPort.getText().trim());
            clientSocket = new Socket("localhost", PORT);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            isrunning=true;
        } catch (Exception err) {
            out.println("[ERROR] "+err.getLocalizedMessage());
            isrunning=false;
        }
    }

    private void thisWindowClosing(WindowEvent e) {
        // TODO add your code here
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        lblIP = new JLabel();
        txtIp = new JTextField();
        lblPort = new JLabel();
        txtPort = new JTextField();
        lblInfo = new JLabel();
        btnConnect = new JButton();

        //======== this ========
        setTitle("Connection Port");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        var contentPane = getContentPane();

        //---- lblIP ----
        lblIP.setText("IP");
        lblIP.setHorizontalAlignment(SwingConstants.CENTER);

        //---- txtIp ----
        txtIp.setEditable(false);
        txtIp.setText("localhost");

        //---- lblPort ----
        lblPort.setText("Port");
        lblPort.setHorizontalAlignment(SwingConstants.CENTER);

        //---- lblInfo ----
        lblInfo.setText("Info IP/Port");
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);

        //---- btnConnect ----
        btnConnect.setText("Connect");
        btnConnect.addActionListener(e -> btnConnectActionPerformed(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(lblInfo, GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(lblIP, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblPort, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(txtIp, GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                                .addComponent(txtPort, GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE))))
                    .addContainerGap())
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(165, 165, 165)
                    .addComponent(btnConnect, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(174, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblInfo, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblIP, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtIp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblPort, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addComponent(btnConnect)
                    .addContainerGap(17, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }



    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JLabel lblIP;
    private JTextField txtIp;
    private JLabel lblPort;
    private JTextField txtPort;
    private JLabel lblInfo;
    private JButton btnConnect;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
