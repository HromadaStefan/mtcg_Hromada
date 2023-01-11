package at.fhtw.game_server.db.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.service.models.RegisterUser;

import at.fhtw.game_server.db.dbconfig.ConnectDB;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;

import javax.security.auth.login.AppConfigurationEntry;
import javax.swing.text.AbstractDocument;
import javax.xml.crypto.Data;


public class ScoreRepo {
    private ConnectDB db;
    public ScoreRepo(ConnectDB db){this.db = db;}

    public RegisterUser[] getScore(String token){
        try(
                PreparedStatement getScore_statement = db.getConnection().prepareStatement("""
                SELECT username, elo FROM users;
                """)
                ){
            if(db.proofByToken(token) != 0){
                throw new DataAccessException("Invalid Token");
            }
            ResultSet rs_getScore = getScore_statement.executeQuery();

            List<RegisterUser> users = new ArrayList<RegisterUser>();

            while(rs_getScore.next() != false){
                users.add(new RegisterUser(rs_getScore.getString("username"), rs_getScore.getInt("elo")));
            }

            RegisterUser[] users_array = new RegisterUser[users.size()];
            users.toArray(users_array);

            return users_array;
        } catch(SQLException ex){
            ex.printStackTrace();
            throw new DataAccessException("Database error");
        } catch(DataAccessException dax){
            throw dax;
        }
    }
}
