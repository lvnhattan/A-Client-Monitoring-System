/*
 * Created by JFormDesigner on Tue Aug 09 10:49:46 ICT 2022
 */

package Client.Main;

import java.awt.event.*;
import Client.ConnectPort.FormConnectPort;
import Config.Tracking;
import Config.User.Log;

import javax.swing.*;
import javax.swing.GroupLayout;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


/**
 * @author unknown
 */
public class FormClient extends JFrame {
    Socket clientSocket= FormConnectPort.clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isrunning;
    public static ArrayList<Log> userlogs = new ArrayList<>();

    public FormClient() {
        initComponents();
    }

    public void start(){
        System.out.println("Start");
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(FormConnectPort.clientSocket.getInputStream()));
            new Thread(new Listener()).start();

        } catch (Exception err) {
            System.out.println("[ERROR] "+err.getLocalizedMessage());
        }
    }

    private void btnStart(ActionEvent e) {
        start();
    }

    public static class Listener implements Runnable{
        private PrintWriter out;
        private BufferedReader in;

        @Override
        public void run() {
            userlogs = new ArrayList<>(Tracking.userlogs);
            try {
                out = new PrintWriter(FormConnectPort.clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(FormConnectPort.clientSocket.getInputStream()));

            } catch (IOException e) {
                return;
            }
        }
    }
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        btnStart = new JButton();

        //======== this ========
        setTitle("Client");
        var contentPane = getContentPane();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //---- btnStart ----
        btnStart.setText("Start");
        btnStart.addActionListener(e -> btnStart(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(258, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btnStart)
                    .addContainerGap(211, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JButton btnStart;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
