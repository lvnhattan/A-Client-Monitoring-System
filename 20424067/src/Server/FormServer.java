/*
 * Created by JFormDesigner on Wed Jun 09 11:09:38 ICT 2021
 */

package Server;

import Config.*;
import Config.User.AccountUser;
import Config.User.Log;
import me.alexpanov.net.FreePortFinder;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.FileHandler;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.AbstractTableModel;

/**
 * @author nhat tan
 */
public class FormServer extends JFrame {
    public static SimpleDateFormat formatter = new SimpleDateFormat("[hh:mm a]");
    private static final int MAX_CONNECTED = 50;
    private static int PORT;
    public static ServerSocket server;
    private static volatile boolean exit = false;
    public static ArrayList<AccountUser> UserList = new ArrayList<>();
    public static ArrayList<Log> Logs=new ArrayList<>();
    public static HashMap<String, PrintWriter> connectedClients = new HashMap<>();
    public static AccountUser Temp;
    public JTable tablelog;

    public FormServer() {
        initComponents();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FormServer frame = new FormServer();
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(frame);
                    //Logs
                    //System.setOut(new PrintStream(new TextAreaOutputStream(frame.txtAreaLogs)));
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void btnStart(ActionEvent e) {
            if (e.getSource() == btnStart) {
                if (btnStart.getText().equals("START")) {
                    exit = false;
                    getRandomPort();
                    Log temp=new Log("Server","Start","Port "+String.valueOf(PORT),LocalDateTime.now(),"Start Server");
                    Logs.add(temp);
                    start();
                    btnStart.setText("STOP");
                } else {
                    Log temp=new Log("Server","Stop","Port " +String.valueOf(PORT),LocalDateTime.now(),"Stop Server");
                    Logs.add(temp);
                    exit = true;
                    btnStart.setText("START");
                }
            }
            //Refresh UI
            refreshUIComponents();
    }

    public void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        lblChatServer = new JLabel();
        scrollPane = new JScrollPane();
        tablelog = new JTable();
        btnStart = new JButton();

        //======== this ========
        setResizable(false);
        setTitle("Server ");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        var contentPane = getContentPane();

        //---- lblChatServer ----
        lblChatServer.setText("CHAT SERVER");
        lblChatServer.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblChatServer.setHorizontalAlignment(SwingConstants.CENTER);

        //======== scrollPane ========
        {
            scrollPane.setViewportView(tablelog);
        }

        //---- btnStart ----
        btnStart.setText("START");
        btnStart.addActionListener(e -> btnStart(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(btnStart, GroupLayout.DEFAULT_SIZE, 1026, Short.MAX_VALUE)
                        .addComponent(lblChatServer, GroupLayout.DEFAULT_SIZE, 1026, Short.MAX_VALUE)
                        .addComponent(scrollPane, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1026, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(lblChatServer, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(btnStart)
                    .addGap(127, 127, 127))
        );
        setSize(1040, 590);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    public void refreshUIComponents() {
        lblChatServer.setText("CHAT SERVER" + (!exit ? ": " + PORT : ""));
        Logtable model = new Logtable(Logs);
        tablelog.setModel(model);
    }

    public static class Logtable extends AbstractTableModel{
        private final String[] columns = {"Username", "Acction", "Ipclient","Datetime","Description"};
        private ArrayList<Log> Logs;

        public Logtable (ArrayList<Log> Logs)
        {
            this.Logs=Logs;
        }

        @Override
        public int getRowCount() {
            return Logs.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return switch (columnIndex){
                case 0 -> Logs.get(rowIndex).getUsername();
                case 1 -> Logs.get(rowIndex).getAcction();
                case 2 -> Logs.get(rowIndex).getIpclient();
                case 3 -> Logs.get(rowIndex).getTime();
                case 4 -> Logs.get(rowIndex).getDescription();
                default -> "-";
            };
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (getValueAt(0,columnIndex)!=null)
            {
                return getValueAt(0,columnIndex).getClass();
            }
            else {
                return Object.class;
            }
        }
    }
    public static void start() {
        new Thread(new ServerHandler()).start();

    }

    public static void stop() throws IOException {
        if (!server.isClosed()) server.close();
    }

    private static void broadcastMessage(String message) {
        for (PrintWriter p: connectedClients.values()) {
            p.println(message);
        }
    }

    private static int getRandomPort() {
        int port = FreePortFinder.findFreeLocalPort();
        PORT = port;
        return port;
    }

    private static class ServerHandler implements Runnable {
        @Override
        public void run() {
            try {
                server = new ServerSocket(PORT);
                while (!exit) {
                    if (connectedClients.size() <= MAX_CONNECTED) {
                        new Thread(new ClientHandler(server.accept())).start();
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),"Bug",Arrays.toString(e.getStackTrace()),JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Start of Client Handler
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            //addToLogs("Client connected: " + socket.getInetAddress() + socket.getPort());

            String name="";
            //Xử Lí Thông tin Client
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                String check;
                while (true) {
                    check = in.readLine();
                    if (check.equals("Connect")) {
                        var User = new AccountUser(in.readLine(),in.readLine());
                        UserList.add(User);
                        Log temp = new Log(User.getUsername(),"Login",String.valueOf(socket.getInetAddress()) + String.valueOf(socket.getPort()), LocalDateTime.now(),"Đăng nhập");
                        Logs.add(temp);
                        System.out.print(temp);
                        JTable tablelog=new JTable();
                        Logtable model = new Logtable(Logs);
                        tablelog.setModel(model);
                    }

                }
            } catch (
                    Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),"Bug",e.getMessage(),JOptionPane.ERROR_MESSAGE);
            } finally {
                if (name != null) {
//                    addToLogs(name + " is leaving");
//                    connectedClients.remove(name);
//                    broadcastMessage(name + " has left");
                }
            }
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JLabel lblChatServer;
    private JScrollPane scrollPane;
    private JButton btnStart;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
