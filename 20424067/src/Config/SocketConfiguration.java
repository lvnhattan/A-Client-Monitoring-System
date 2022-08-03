package Config;

import Client.ConnectPort.FormConnectPort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class SocketConfiguration {

    public static class Listener implements Runnable {
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
}
