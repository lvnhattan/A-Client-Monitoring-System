/*
 * Created by JFormDesigner on Tue Aug 09 10:49:46 ICT 2022
 */

package Client.Main;

import Config.LogDir;
import Config.Tracking;
import Config.User.AccountUser;
import Config.User.Log;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author unknown
 */
public class FormClient extends JFrame {

    public static AccountUser user;
    public static Socket clientSocket;
    public ArrayList<Log> Logs = new ArrayList<>();
    public DefaultTableModel modellog;
    private static String[] columnslog = {"Username", "Acction", "Ipclient", "Datetime", "Description"};


    public FormClient(Socket clientSocket, AccountUser user) {
        initComponents();
        this.user = user;
        this.clientSocket = clientSocket;
        start();
        LoadTableClient();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //FormClient frame = new FormClient(clientSocket, user);
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//                    SwingUtilities.updateComponentTreeUI(frame);
//                    frame.setVisible(true);
                    //new Thread(new refeshTable(frame)).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void LoadTableClient() {
        modellog = new DefaultTableModel(null, columnslog);
        addDataLog(modellog, tablelog, Logs);
    }

    public void addDataLog(DefaultTableModel model, JTable tablelog, ArrayList<Log> Logs) {

        for (int i = 0; i < Logs.size(); i++) {
            String name = Logs.get(i).getUsername();
            String action = Logs.get(i).getAcction();
            String ip = Logs.get(i).getIpclient();
            String time = Logs.get(i).getTime();
            String des = Logs.get(i).getDescription();

            Object[] data = {name, action, ip, time, des};
            model.addRow(data);
        }

        tablelog.setModel(model);
        tablelog.setRowSelectionAllowed(true);

    }


    private static class refeshTable implements Runnable {
        public Tracking track;
        public JTable tablelog;
        public ArrayList<Log> Logs;
        public DefaultTableModel modellog;
        private static String[] columnslog = {"Username", "Acction", "Ipclient", "Datetime", "Description"};


        public refeshTable(ArrayList<Log> logs,Tracking track,JTable tablelog) {
            this.Logs=logs;
            this.track=track;
            this.tablelog=tablelog;
        }

        public void LoadTableClient() {
            modellog = new DefaultTableModel(null, columnslog);
            addDataLog(modellog, tablelog, Logs);
        }

        public static void addDataLog(DefaultTableModel model, JTable tablelog, ArrayList<Log> Logs) {

            for (int i = 0; i < Logs.size(); i++) {
                String name = Logs.get(i).getUsername();
                String action = Logs.get(i).getAcction();
                String ip = Logs.get(i).getIpclient();
                String time = Logs.get(i).getTime();
                String des = Logs.get(i).getDescription();

                Object[] data = {name, action, ip, time, des};
                model.addRow(data);
            }

            tablelog.setModel(model);
            tablelog.setRowSelectionAllowed(true);

        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(1000);
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                Logs=track.userlogs;
                                LoadTableClient();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), Arrays.toString(e.getStackTrace()), "Bug", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void start() {
        System.out.println("Start");
        try {
            Listener listen = new Listener(clientSocket, user, tablelog, Logs);
            new Thread(listen).start();
            //new Thread(new Listener(clientSocket,user,tablelog)).start();


        } catch (Exception err) {
            System.out.println("[ERROR] " + err.getLocalizedMessage());
        }
    }

    private void btnStart(ActionEvent e) {
    }

    public static class Listener implements Runnable {
        public JTable tablelog;
        private LogDir logdir;
        private AccountUser user;
        public Tracking track;
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        public static ArrayList<Log> Logs;
        public DefaultTableModel modellog;
        private static String[] columnslog = {"Username", "Acction", "Ipclient", "Datetime", "Description"};

        public Listener(Socket socket, AccountUser user, JTable tablelog, ArrayList<Log> Logs) {
            this.Logs = Logs;
            this.tablelog = tablelog;
            this.socket = socket;
            this.user = user;
        }


        @Override
        public void run() {
            //userlogs = new ArrayList<>(Tracking.userlogs);
            //new Thread(new refeshTable(frame,track)).start();

            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                logdir = new LogDir(socket);
                track = new Tracking(Paths.get(user.Pathdir), true, logdir, user.getUsername());
                new Thread(track).start();
                new Thread(new refeshTable(Logs,track,tablelog)).start();

                while (true) {
                    String check = in.readLine();
                    if (check.equals("DirChange")) {
                        user = new AccountUser(in.readLine(), in.readLine());
                        user.Pathdir = in.readLine();
                        track.isrunning = false;
                        logdir = new LogDir(socket);
                        track = new Tracking(Paths.get(user.Pathdir), true, logdir, user.getUsername());
                        new Thread(track).start();
//                        System.out.println(Paths.get(user.Pathdir) + user.getUsername() + user.getIpclient());
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
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
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
    public JTable tablelog;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
