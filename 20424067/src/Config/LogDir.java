package Config;

import Config.User.Log;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class LogDir {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public LogDir(Socket socket) {
        this.socket = socket;

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void sendMess(String msg) {
        out.println(msg);
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendPack(String name, Socket socket, String des) {
        out.println(name);
        out.println(socket.getInetAddress() + ":" + socket.getPort() + " " + socket.getLocalPort());
        out.println(des);

    }

    public Log getPack(String name, Socket socket, String msg) {
        Log user = new Log(name, "", socket.getInetAddress() + ":" + socket.getPort() + " " + socket.getLocalPort(), LocalDateTime.now(), msg);
        return user;
    }

    public void readFile() {

    }

    public void writeFile(Log log) {
        File file = new File("ClientLogs.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter myWriter = new BufferedWriter(new FileWriter(file, true));
            String temp = log.getUsername() + " " + log.getAcction() + " " + log.getIpclient() + " " + log.getTime() + " " + log.getDescription();
            myWriter.append(temp + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}