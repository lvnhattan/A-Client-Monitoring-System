package Config.User;

import java.time.LocalDateTime;

public class Log {
    private String Username;
    private String Acction;
    private String Ipclient;
    private LocalDateTime Time;
    private String Description;

    public Log(String username, String acction, String ipclient, LocalDateTime time, String description) {
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

    public LocalDateTime getTime() {
        return Time;
    }

    public String getDescription() {
        return Description;
    }

}
