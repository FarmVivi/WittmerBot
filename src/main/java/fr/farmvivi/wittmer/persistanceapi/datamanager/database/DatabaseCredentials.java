package fr.farmvivi.wittmer.persistanceapi.datamanager.database;

public class DatabaseCredentials {
    private final String host;
    private final String user;
    private final String pass;
    private final String dbName;
    private final int port;

    public DatabaseCredentials(String host, String user, String pass, String dbName, int port) {
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.dbName = dbName;
        this.port = port;
    }

    public String toURI() {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
}
