package Config.User;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Log {
    private String Username;
    private String Acction;
    private String Ipclient;
    private String Time;
    private String Description;

    public Log(String username, String acction, String ipclient, String time, String description) {
        Username = username;
        Acction = acction;
        Ipclient = ipclient;
        Time = time;
        Description = description;
    }

    public String getUsername() {
        return Username;
    }

    public String getAcction() {
        return Acction;
    }

    public String getIpclient() {
        return Ipclient;
    }

    public String getTime() {
        return Time;
    }

    public String getDescription() {
        return Description;
    }

    public void writeFile(Log log, String dir) {
        File file = new File(dir);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter myWriter = new BufferedWriter(new FileWriter(file, true));
            String temp = log.getUsername() + "|" + log.getAcction() + "|" + log.getIpclient() + "|" + log.getTime() + "|" + log.getDescription();
            myWriter.append(temp + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void readFile(ArrayList<Log> list, String dir) {
        File file = new File(dir);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null) {
                Log temp = new Log("", "", "", LocalDateTime.now().format(dateFormat), "");
                String[] parts = line.split("\\|");
                temp.Username = parts[0];
                temp.Acction = parts[1];
                temp.Ipclient = parts[2];
                temp.Time = parts[3];
                temp.Description = parts[4];
                list.add(temp);
//                System.out.println(parts[0]+" - "+parts[1]+" - "+parts[2]+" - "+parts[3]+" - "+parts[4]);
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred FileNotFoundException.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("An error occurred IOException.");
            e.printStackTrace();
        }
    }


}
