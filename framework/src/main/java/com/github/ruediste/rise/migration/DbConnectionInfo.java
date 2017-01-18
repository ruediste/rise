package com.github.ruediste.rise.migration;

public class DbConnectionInfo {

    public String host;
    public String port;
    public String database;
    public String user;
    public String password;

    public DbConnectionInfo() {
    }

    public DbConnectionInfo(String host, String port, String database, String user, String password) {
        super();
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

}
