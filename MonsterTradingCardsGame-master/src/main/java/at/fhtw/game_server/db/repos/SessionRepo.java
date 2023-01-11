package at.fhtw.game_server.db.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.service.models.Session;

import at.fhtw.game_server.db.dbconfig.ConnectDB;

public class SessionRepo {
    private ConnectDB db;
    public SessionRepo(ConnectDB db){
        this.db = db;
    }

    public String loginUser(Session create_session) {
        try(
                PreparedStatement statement = db.getConnection().prepareStatement("""
                    Select * FROM users WHERE username = ? AND password = ?;
                """);

                PreparedStatement post_token = db.getConnection().prepareStatement("""
                UPDATE users 
                SET     token = ?
                WHERE username = ? AND password = ?; 
                """);
        ){
            statement.setString(1, create_session.getUsername());
            statement.setString(2, create_session.getPassword());
            ResultSet rs = statement.executeQuery();

            if (rs.next() == false){
                throw new DataAccessException("User nicht vorhanden oder falsches Passwort");
            }
            else{
                Session session = new Session(rs.getString("username"), rs.getString("password"));

                String token = "Basic " + session.getUsername() + "-mtcgToken";

                post_token.setString(1, token);
                post_token.setString(2, session.getUsername());
                post_token.setString(3, session.getPassword());

                post_token.execute();
                db.commit();
                return token;
            }
        } catch(SQLException ex){
            db.rollback();
            ex.printStackTrace();
            return null;
        }
    }
}
