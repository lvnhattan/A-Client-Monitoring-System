package Client;

import Config.User.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class LogDir {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public LogDir(Socket socket) {
        this.socket = socket;

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out= new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void sendMess(String msg)
    {
        out.println(msg);
        try {
            Files.write(Paths.get("ClientHistory.txt"), msg.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendPack(String name, Socket socket, String msg){
        out.println(name);
        out.println(socket.getInetAddress() + " " + socket.getPort());
        out.println(msg);

    }

    public Log getPack(String name, Socket socket, String msg){
        Log user= new Log(name,"",socket.getInetAddress() + " " + socket.getPort(), LocalDateTime.now(),msg);
        return user;
    }


}