package Config.User;

import java.net.Socket;
import java.util.ArrayList;

public class AccountUser {
    private String Username;
    private String Ipclient;
    public String Pathdir;
    public Socket socket;


    public AccountUser(String username, String ipclient) {
        Username = username;
        Ipclient = ipclient;
        Pathdir = "D:/Test";
    }

    public String getUsername() {
        return Username;
    }

    public String getIpclient() {
        return Ipclient;
    }

    public String getPathdir() {
        return Pathdir;
    }

    public boolean CheckAccount(ArrayList<AccountUser> list, AccountUser user) {
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUsername().equals(user.getUsername()) & list.get(i).getIpclient().equals(user.getIpclient())) {
                    return true;
                }
            }
        }
        return false;
    }
}
