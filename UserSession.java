/**
 * UserSession - Stores the currently logged-in user's details globally.
 * Call UserSession.setUser(...) after successful login.
 * Access anywhere with UserSession.getUserId(), getName(), etc.
 */
public class UserSession {

    private static int    userId;
    private static String name;
    private static String email;
    private static String phone;

    public static void setUser(int id, String name, String email, String phone) {
        UserSession.userId = id;
        UserSession.name   = name;
        UserSession.email  = email;
        UserSession.phone  = phone;
    }

    public static int    getUserId() { return userId; }
    public static String getName()   { return name;   }
    public static String getEmail()  { return email;  }
    public static String getPhone()  { return phone;  }

    public static void clear() {
        userId = 0;
        name   = null;
        email  = null;
        phone  = null;
    }
}