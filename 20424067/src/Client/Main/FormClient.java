/*
 * Created by JFormDesigner on Tue Aug 09 10:49:46 ICT 2022
 */

package Client.Main;

import Client.ConnectPort.FormConnectPort;
import Config.FileSystemModel;
import Config.User.Log;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.*;
import javax.swing.GroupLayout;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;


/**
 * @author unknown
 */
public class FormClient extends JFrame {

    Socket clientSocket= FormConnectPort.clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isrunning;
    private Log userlog;

    public FormClient() {
        initComponents();
    }

    public void start(){

        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(FormConnectPort.clientSocket.getInputStream()));
            new Thread(new Listener()).start();

        } catch (Exception err) {
            System.out.println("[ERROR] "+err.getLocalizedMessage());
        }
    }

    public static class Listener implements Runnable{
        private BufferedReader in;
        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(FormConnectPort.clientSocket.getInputStream()));
                String read;
                for (; ; ) {
                    read = in.readLine();
                    if (read != null && !(read.isEmpty())) System.out.println(read);
                }
            } catch (IOException e) {
                return;
            }
        }
    }
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        scrollPane1 = new JScrollPane();
        treedir = new JTree();

        //======== this ========
        setTitle("Client");
        var contentPane = getContentPane();

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(treedir);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 493, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(174, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(32, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JScrollPane scrollPane1;
    private JTree treedir;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
