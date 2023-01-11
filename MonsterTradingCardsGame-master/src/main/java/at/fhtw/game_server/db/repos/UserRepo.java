package at.fhtw.game_server.db.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

public class UserRepo {
    private ConnectDB db;

    public UserRepo(ConnectDB db){
        this.db = db;
    }

    public int registerUser(RegisterUser reguser){
        try(
                PreparedStatement statement = db.getConnection().prepareStatement("""
            INSERT INTO users 
            (username, password) 
            VALUES(?, ?);
            """)
        ){
            statement.setString(1, reguser.getUsername());
            statement.setString(2, reguser.getPasswort());
            statement.execute();
            db.commit();
            return 0;
        } catch(SQLException ex){
            db.rollback();
            ex.printStackTrace();
            String sqlstate = ex.getSQLState();

            if(sqlstate.equals("23505")){
                throw new DataAccessException("User already exists", ex);
            } else if(sqlstate.equals("23502")){
                throw new DataAccessException("No Username and/or Password given", ex);
            } else{
                throw new DataAccessException("Database Error");
            }
        }
    }

    public RegisterUser getUserData(String username, String token){
        try{
            if(db.login(username, token) == 0){
                PreparedStatement statement = db.getConnection().prepareStatement("""
                    SELECT * FROM users WHERE username = ?;    
                """);

                statement.setString(1, username);
                ResultSet rs = statement.executeQuery();
                rs.next();
                RegisterUser getuser = new RegisterUser(rs.getString("username"), rs.getString("password"), rs.getInt("coins"), rs.getInt("elo"), rs.getString("token"), rs.getString("name"), rs.getString("bio"), rs.getString("image"));
                return getuser;
            }
            return null;
        }catch (DataAccessException dax){
            throw new DataAccessException(dax.getMessage());
        } catch(SQLException ex){
            ex.printStackTrace();
            db.rollback();
            throw new DataAccessException("Fehler beim Abfragen der Userdaten");
        }
    }

    public int updateUser(RegisterUser userdata, String username, String token){
        try(
                PreparedStatement statement = db.getConnection().prepareStatement("""
                UPDATE users SET name = ?,
                bio = ?, image = ? WHERE username = ? AND token = ?;
                """);
                ){
            RegisterUser user = db.getUserByToken(token);
            if(user.getName() == null && user.getBio() != null && user.getImage() != null){
                System.out.printf("test");
                if(user.getName().equals(userdata.getName()) && user.getBio().equals(userdata.getBio()) && user.getImage().equals(userdata.getImage())){
                    db.rollback();
                    throw new DataAccessException("Values did not change (same input)");
                }
            }

            statement.setString(1, userdata.getName());
            statement.setString(2, userdata.getBio());
            statement.setString(3, userdata.getImage());
            statement.setString(4, username);
            statement.setString(5, token);

            int i = statement.executeUpdate();

            if(i == 0){
                db.rollback();
                throw new DataAccessException("Update failed, username/token is invalid.");
            }

            db.commit();
            return 0;
        } catch(SQLException ex){
            db.rollback();
            ex.printStackTrace();
            throw new DataAccessException(ex.getSQLState(), ex);
        }
    }

}
