public class DBConnection {
    private String dbUsername;
    private String dbPassword;

    public DBConnection() {
        healthCheck();
    }
    private void healthCheck() {
        System.out.println("DB CONNECTION REACHED");
    }
}
