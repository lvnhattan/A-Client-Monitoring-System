/*
 * Created by JFormDesigner on Tue Aug 09 10:49:46 ICT 2022
 */

package Client.Main;

import Client.ConnectPort.FormConnectPort;
import Config.LogDir;
import Config.Tracking;
import Config.User.AccountUser;
import Config.User.Log;

import javax.sound.midi.Track;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;


/**
 * @author unknown
 */
public class FormClient extends JFrame {

    public AccountUser user;
    public Socket clientSocket;
    public LogDir logdir;
    public Tracking track;
    public static ArrayList<Log> userlogs = new ArrayList<>();

    public FormClient(Socket clientSocket, AccountUser user) {
        this.user=user;
        this.clientSocket = clientSocket;
        initComponents();
        start();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //FormClient frame = new FormClient(track);
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//                    SwingUtilities.updateComponentTreeUI(frame);
//                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void start() {
        System.out.println("Start");
        try {

            new Thread(new Listener(clientSocket,user)).start();
        } catch (Exception err) {
            System.out.println("[ERROR] " + err.getLocalizedMessage());
        }
    }

    private void btnStart(ActionEvent e) {
    }

    public static class Listener implements Runnable {
        private LogDir logdir;
        private AccountUser user;
        private Tracking track;
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public Listener(Socket socket,AccountUser user) {
            this.socket = socket;
            this.user=user;
        }

        @Override
        public void run() {
            //userlogs = new ArrayList<>(Tracking.userlogs);
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                logdir = new LogDir(socket);
                track = new Tracking(Paths.get(user.Pathdir), true, logdir, user.getUsername());
                new Thread(track).start();
                while (true) {
                    String check = in.readLine();
                    if (check.equals("DirChange")) {
                        user = new AccountUser(in.readLine(), in.readLine());
                        user.Pathdir = in.readLine();
                        track.isrunning = false;
                        logdir = new LogDir(socket);
                        track = new Tracking(Paths.get(user.Pathdir), true, logdir, user.getUsername());
                        new Thread(track).start();
                        System.out.println(Paths.get(user.Pathdir) + user.getUsername() + user.getIpclient());
                    }
                }
            } catch (IOException e) {
                return;
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        btnStart = new JButton();
        scrollPane1 = new JScrollPane();
        tablelog = new JTable();

        //======== this ========
        setTitle("Client");
        var contentPane = getContentPane();

        //---- btnStart ----
        btnStart.setText("Start");
        btnStart.addActionListener(e -> btnStart(e));

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(tablelog);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap(7, Short.MAX_VALUE)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(btnStart, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(scrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 695, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnStart)
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JButton btnStart;
    private JScrollPane scrollPane1;
    private JTable tablelog;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
