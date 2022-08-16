/*
 * Created by JFormDesigner on Wed Jun 09 11:09:38 ICT 2021
 */

package Server;

import java.awt.event.*;
import Config.User.AccountUser;
import Config.User.Log;
import me.alexpanov.net.FreePortFinder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author nhat tan
 */
public class FormServer extends JFrame {
    private static final int MAX_CONNECTED = 50;
    private static int PORT;
    private static volatile boolean exit = false;
    public static ServerSocket server;
    public static ArrayList<AccountUser> UserList = new ArrayList<>();
    public static ArrayList<Log> Logs = new ArrayList<>();
    public static HashMap<String, PrintWriter> connectedClients = new HashMap<>();
    public static AccountUser selectUser;
    public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public String filelog = "ServerLogs.txt";
    public JTable tablelog;
    public JTable tableuser;
    public DefaultTableModel modellog;
    public DefaultTableModel modeluser;
    private static String[] columnslog = {"Username", "Acction", "Ipclient", "Datetime", "Description"};
    private static String[] columnsuser = {"Username", "Ipclient"};

    public FormServer() {
        initComponents();
        Log temp = new Log("", "", "", LocalDateTime.now().format(dateFormat), "");
        temp.readFile(Logs, filelog);
        LoadTableServer();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FormServer frame = new FormServer();
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(frame);
                    frame.setVisible(true);
                    new Thread(new refeshTable(frame)).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void btnStart(ActionEvent e) {
        Log temp;
        if (e.getSource() == btnStart) {
            if (btnStart.getText().equals("START")) {
                exit = false;
                getRandomPort();
                temp = new Log("Server", "Start", "Port " + PORT, LocalDateTime.now().format(dateFormat), "Start Server");
                Logs.add(temp);
                start(tablelog, tableuser);
                temp.writeFile(temp, filelog);
                btnStart.setText("STOP");
            } else {
                temp = new Log("Server", "Stop", "Port " + PORT, LocalDateTime.now().format(dateFormat), "Stop Server");
                Logs.add(temp);
                exit = true;
                temp.writeFile(temp, filelog);
                btnStart.setText("START");
            }
        }
        //Refresh UI
        refreshUIComponents();
    }

    private void btnChangedir(ActionEvent e) {
        if(selectUser!=null) {
            FormChangeDir fchangedir = new FormChangeDir(selectUser);
            fchangedir.setVisible(true);
        }
        btnChangedir.setEnabled(false);
    }

    private void tableuserMouseClicked(MouseEvent e) {
        int selectedRow = tableuser.getSelectedRow();
        selectUser = new AccountUser((String) tableuser.getValueAt(selectedRow,0),(String) tableuser.getValueAt(selectedRow,1));
        System.out.println("Selected: " + selectUser.getUsername() + " " + selectUser.getIpclient());
        if(selectUser!=null)
        {
            btnChangedir.setText("Change Directory: "+selectUser.getUsername() + " " + selectUser.getIpclient());
            btnChangedir.setEnabled(true);
        }
    }

    public void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        lblChatServer = new JLabel();
        scrollPane = new JScrollPane();
        tablelog = new JTable();
        btnStart = new JButton();
        scrollPane1 = new JScrollPane();
        tableuser = new JTable();
        btnChangedir = new JButton();

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

        //======== scrollPane1 ========
        {
            //---- tableuser ----
            tableuser.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    tableuserMouseClicked(e);
                }
            });
            scrollPane1.setViewportView(tableuser);
        }

        //---- btnChangedir ----
        btnChangedir.setText("Change Directory");
        btnChangedir.setEnabled(false);
        btnChangedir.addActionListener(e -> btnChangedir(e));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(lblChatServer, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                            .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnChangedir, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPaneLayout.createParallelGroup()
                                .addComponent(btnStart, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
                                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE))))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(lblChatServer, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                    .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnStart, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                        .addComponent(btnChangedir, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
                    .addGap(20, 20, 20))
        );
        setSize(1160, 595);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    public void refreshUIComponents() {
        lblChatServer.setText("CHAT SERVER" + (!exit ? ": " + PORT : ""));
        LoadTableServer();
    }

    public void LoadTableServer() {
        modellog = new DefaultTableModel(null, columnslog);
        addDataLog(modellog, tablelog);

        modeluser = new DefaultTableModel(null, columnsuser);
        addDataUser(modeluser, tableuser);

    }

    private static void addDataLog(DefaultTableModel model, JTable tablelog) {
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

    private static void addDataUser(DefaultTableModel model, JTable tableuser) {
        for (int i = 0; i < UserList.size(); i++) {
            String name = UserList.get(i).getUsername();
            String ip = UserList.get(i).getIpclient();

            Object[] data = {name, ip};
            model.addRow(data);
        }

        tableuser.setModel(model);
        tableuser.setRowSelectionAllowed(true);

    }

    public static void start(JTable log, JTable user) {
        new Thread(new ServerHandler(log, user)).start();

    }

   /* public static void stop() throws IOException {
        if (!server.isClosed()) server.close();
    }*/

    private static int getRandomPort() {
        int port = FreePortFinder.findFreeLocalPort();
        PORT = port;
        return port;
    }

    private static class refeshTable implements Runnable {
        public FormServer fserver;

        public refeshTable(FormServer fserver) {
            this.fserver = fserver;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(1000);
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            try {
                               fserver.LoadTableServer();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Bug", Arrays.toString(e.getStackTrace()), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class ServerHandler implements Runnable {
        public JTable log;
        public JTable user;

        public ServerHandler(JTable log, JTable user) {
            this.log = log;
            this.user = user;
        }

        @Override
        public void run() {
            try {
                server = new ServerSocket(PORT);
                while (!exit) {
                    if (connectedClients.size() <= MAX_CONNECTED) {
                        new Thread(new ClientHandler(server.accept(), log, user)).start();
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Bug", Arrays.toString(e.getStackTrace()), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // Start of Client Handler
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        public AccountUser currentUser;
        public JTable log;
        public JTable user;

        public String filelog = "ServerLogs.txt";

        public ClientHandler(Socket socket, JTable log, JTable user) {
            this.user = user;
            this.log = log;
            this.socket = socket;
        }

        public void LoadTableServer() {
            DefaultTableModel modellog = new DefaultTableModel(null, columnslog);
            FormServer.addDataLog(modellog, log);

            DefaultTableModel modeluser = new DefaultTableModel(null, columnsuser);
            FormServer.addDataUser(modeluser, user);

        }

        @Override
        public void run() {
            //Xử Lí Thông tin Client
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                String check;
                Log temp = new Log("", "", "", LocalDateTime.now().format(dateFormat), "");
                while (true) {
                    check = in.readLine();
                    if (check.equals("Connect")) {
                        var User = new AccountUser(in.readLine(), in.readLine());
                        currentUser=User;
                        if (!User.CheckAccount(UserList, User)) {
                            UserList.add(User);
                        }
                        temp = new Log(User.getUsername(), "Login", User.getIpclient(), LocalDateTime.now().format(dateFormat), User.getUsername() + " Đăng nhập");
                        Logs.add(temp);

                    }
                    if (check.equals("Scanning")) {
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if (!User.CheckAccount(UserList, User)) {
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Scanning", User.getIpclient(), LocalDateTime.now().format(dateFormat), "Scanning: " + des);
                        Logs.add(temp);
                    }

                    if (check.equals("Done")) {
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if (!User.CheckAccount(UserList, User)) {
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Done", User.getIpclient(), LocalDateTime.now().format(dateFormat), "Done: " + des);
                        Logs.add(temp);
                    }

                    if (check.equals("Register")) {
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if (!User.CheckAccount(UserList, User)) {
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Register", User.getIpclient(), LocalDateTime.now().format(dateFormat), "Register: " + des);
                        Logs.add(temp);
                    }

                    if (check.equals("Update")) {
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if (!User.CheckAccount(UserList, User)) {
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Update", User.getIpclient(), LocalDateTime.now().format(dateFormat), "Update: " + des);
                        Logs.add(temp);
                    }

                    if (check.equals("ENTRY_CREATE")) {
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if (!User.CheckAccount(UserList, User)) {
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Create", User.getIpclient(), LocalDateTime.now().format(dateFormat), "Tạo mới: " + des);
                        Logs.add(temp);
                    }

                    if (check.equals("ENTRY_DELETE")) {
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if (!User.CheckAccount(UserList, User)) {
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Delete", User.getIpclient(), LocalDateTime.now().format(dateFormat), "Xóa: " + des);
                        Logs.add(temp);
                    }
                    if (check.equals("ENTRY_MODIFY")) {
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if (!User.CheckAccount(UserList, User)) {
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Modify", User.getIpclient(), LocalDateTime.now().format(dateFormat), "Chỉnh sửa: " + des);
                        Logs.add(temp);
                    }
                    temp.writeFile(temp, filelog);
                    LoadTableServer();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(), "Bug", JOptionPane.ERROR_MESSAGE);
                System.out.println(e.getMessage());
            } finally {
                if (currentUser.getUsername() != null) {
                    Log temp = new Log(currentUser.getUsername(), "Logout", currentUser.getIpclient(), LocalDateTime.now().format(dateFormat), currentUser.getUsername() + " Đăng Xuất");
                    Logs.add(temp);
                    UserList.remove(currentUser);
                    temp.writeFile(temp, filelog);
                    LoadTableServer();
                }
            }

        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JLabel lblChatServer;
    private JScrollPane scrollPane;
    private JButton btnStart;
    private JScrollPane scrollPane1;
    private JButton btnChangedir;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
