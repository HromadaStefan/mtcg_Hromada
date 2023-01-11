package at.fhtw.game_server.db.dbconfig;

import java.sql.Connection;
import java.sql.DriverManager;

public enum Singleton {

    INSTANCE;

    private Connection connection;

    Singleton() {}

    public Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:49153/mtcg", "postgres", "postgres");
            if(connection != null){
                System.out.println("Connection OK");
            }
            else{
                System.out.println("Connection failed");
            }
            connection.setAutoCommit(false);
            return connection;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}