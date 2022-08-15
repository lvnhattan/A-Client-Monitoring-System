package Config.User;

import java.util.ArrayList;

public class AccountUser {
    private String Username;
    private String Ipclient;

    public AccountUser(String username, String ipclient) {
        Username = username;
        Ipclient = ipclient;
    }

    public String getUsername() {
        return Username;
    }

    public String getIpclient() {
        return Ipclient;
    }

    public boolean CheckAccount(ArrayList<AccountUser> list,AccountUser user)
    {
        for(int i=0;i<=list.size();i++){
            if (list.get(i).getUsername().equals(user.getUsername())&list.get(i).getIpclient().equals(user.getIpclient())){
                return true;
            }
        }
        return false;
    }
}
