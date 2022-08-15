/*
 * Created by JFormDesigner on Wed Jun 09 11:09:38 ICT 2021
 */

package Server;

import Config.LogDir;
import Config.User.AccountUser;
import Config.User.Log;
import me.alexpanov.net.FreePortFinder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    public static AccountUser Temp;
    public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public String filelog = "ServerLogs.txt";
    public JTable tablelog;
    public DefaultTableModel model;
    private static String[] columns = {"Username", "Acction", "Ipclient", "Datetime", "Description"};

    public FormServer() {
        initComponents();
        Log temp=new Log("","","",LocalDateTime.now().format(dateFormat),"");
        temp.readFile(Logs,filelog);
        LoadLogServer();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FormServer frame = new FormServer();
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    SwingUtilities.updateComponentTreeUI(frame);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void btnStart(ActionEvent e) {
        Log temp=new Log("","","",LocalDateTime.now().format(dateFormat),"");
        if (e.getSource() == btnStart) {
            if (btnStart.getText().equals("START")) {
                exit = false;
                getRandomPort();
                temp = new Log("Server", "Start", "Port " + String.valueOf(PORT), LocalDateTime.now().format(dateFormat), "Start Server");
                Logs.add(temp);
                start(tablelog);
                temp.writeFile(temp,filelog);
                btnStart.setText("STOP");
            } else {
                temp = new Log("Server", "Stop", "Port " + String.valueOf(PORT), LocalDateTime.now().format(dateFormat), "Stop Server");
                Logs.add(temp);
                exit = true;
                temp.writeFile(temp,filelog);
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
        LoadLogServer();
    }

    private void LoadLogServer() {
        model = new DefaultTableModel(null, columns);
        addData(model, tablelog);
    }

    private static void addData(DefaultTableModel model, JTable tablelog) {
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

    public static void start(JTable log) {
        new Thread(new ServerHandler(log)).start();

    }

   /* public static void stop() throws IOException {
        if (!server.isClosed()) server.close();
    }*/

    private static int getRandomPort() {
        int port = FreePortFinder.findFreeLocalPort();
        PORT = port;
        return port;
    }

    private static class ServerHandler implements Runnable {
        public JTable log;


        public ServerHandler(JTable log){
            this.log=log;

        }

        @Override
        public void run() {
            try {
                server = new ServerSocket(PORT);
                while (!exit) {
                    if (connectedClients.size() <= MAX_CONNECTED) {
                        new Thread(new ClientHandler(server.accept(),log)).start();
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
        public JTable log;
        public String filelog = "ServerLogs.txt";

        public ClientHandler(Socket socket,JTable log) {
            this.log=log;
            this.socket = socket;
        }

        public void LoadLogTable() {
            DefaultTableModel model = new DefaultTableModel(null, columns);
            FormServer.addData(model,log);

        }

        @Override
        public void run() {
            String name = "";
            //Xử Lí Thông tin Client
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                String check;
                Log temp=new Log("","","",LocalDateTime.now().format(dateFormat),"");
                while (true) {
                    check = in.readLine();
                    if (check.equals("Connect")) {
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if(!User.CheckAccount(UserList,User)){
                            UserList.add(User);
                        }
                        temp = new Log(User.getUsername(), "Login", User.getIpclient(), LocalDateTime.now().format(dateFormat), User.getUsername() + " Đăng nhập");
                        Logs.add(temp);
                        name=User.getUsername();
                    }
                    if(check.equals("Scanning")){
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if(!User.CheckAccount(UserList,User)){
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Scanning", User.getIpclient(), LocalDateTime.now().format(dateFormat),"Scanning: " +des);
                        Logs.add(temp);
                    }

                    if(check.equals("Done")){
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if(!User.CheckAccount(UserList,User)){
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Done", User.getIpclient(), LocalDateTime.now().format(dateFormat),"Done: " +des);
                        Logs.add(temp);
                    }

                    if(check.equals("Register")){
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if(!User.CheckAccount(UserList,User)){
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Register", User.getIpclient(), LocalDateTime.now().format(dateFormat),"Register: " +des);
                        Logs.add(temp);
                    }

                    if(check.equals("Update")){
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if(!User.CheckAccount(UserList,User)){
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Update", User.getIpclient(), LocalDateTime.now().format(dateFormat),"Update: " +des);
                        Logs.add(temp);
                    }

                    if(check.equals("ENTRY_CREATE")){
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if(!User.CheckAccount(UserList,User)){
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Create", User.getIpclient(), LocalDateTime.now().format(dateFormat), "Tạo mới: "+des);
                        Logs.add(temp);
                    }

                    if(check.equals("ENTRY_DELETE")){
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if(!User.CheckAccount(UserList,User)){
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Delete", User.getIpclient(), LocalDateTime.now().format(dateFormat), "Xóa: "+des);
                        Logs.add(temp);
                    }
                    if(check.equals("ENTRY_MODIFY")){
                        var User = new AccountUser(in.readLine(), in.readLine());
                        if(!User.CheckAccount(UserList,User)){
                            UserList.add(User);
                        }
                        var des = in.readLine();
                        temp = new Log(User.getUsername(), "Modify", User.getIpclient(), LocalDateTime.now().format(dateFormat), "Chỉnh sửa: "+des);
                        Logs.add(temp);
                    }
                    temp.writeFile(temp,filelog);
                    LoadLogTable();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), e.getMessage(), "Bug" ,JOptionPane.ERROR_MESSAGE);
                System.out.println(e.getMessage());
            } finally {
                if (name != null) {
                    Log temp = new Log(name, "Logout", String.valueOf(socket.getInetAddress()) ,LocalDateTime.now().format(dateFormat), name + " Đăng Xuất");
                    Logs.add(temp);
                    UserList.remove(name);
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
