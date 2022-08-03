package Config.User;

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
}
