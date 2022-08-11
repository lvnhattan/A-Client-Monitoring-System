package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class LogDir {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public LogDir(Socket socket) {
        this.socket = socket;

        try {
            this.out= new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void sendMess(String msg)
    {
        out.println(msg);
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendPack(String name, Socket socket, String msg){
        out.println(name);
        out.println(socket.getInetAddress() + " " + socket.getPort());
        out.println(msg);
    }



}