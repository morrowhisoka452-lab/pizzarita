package gui.login;

public class Session {

    private static String username;

    public static void setUsername(String user) {
        username = user;
    }

    public static String getUsername() {
        return username;
    }

    public static void clear() {
        username = null;
    }
}