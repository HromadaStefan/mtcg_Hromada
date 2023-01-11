package at.fhtw.game_server.db.dbconfig;

import at.fhtw.game_server.service.models.RegisterUser;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.*;


public class ConnectDB {

    private Connection connection;

    public ConnectDB(){
        connection = Singleton.INSTANCE.getConnection();
    }
    public Connection startConnection(){
        return connection;
    }

    public int login(String username, String token){
        try(
                PreparedStatement statement = connection.prepareStatement("""
            SELECT * FROM users WHERE username = ?;    
            """)
        ){
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if(rs.next() == false){
                throw new DataAccessException("User nicht vorhanden");
            }
            else{
                if(rs.getString("token").equals(token) && rs.getString("token").length() != 0 && rs.getString("token") != null ){
                    return 0;
                }
                else{
                    throw new DataAccessException("Authorisierung fehlgeschlagen");
                }
            }
        } catch (SQLException ex){
            rollback();
            /*if(ex.getSQLState().equals(""))*/
            throw new DataAccessException("Update nicht erfolgreich", ex);
        }
    }

    public int proofByToken(String token){
        try(
                PreparedStatement statement = connection.prepareStatement("""
                SELECT * FROM users WHERE token = ?;
                """)
                ){
                statement.setString(1, token);
                ResultSet rs_statement = statement.executeQuery();

                if(rs_statement.next() == false){
                    throw new DataAccessException("Token invalid");
                } else{
                    return 0;
                }
        } catch(SQLException ex){
            ex.printStackTrace();
            throw new DataAccessException("Database error");
        }
    }

    public RegisterUser getUserByToken(String token){
        try(
                PreparedStatement statement = connection.prepareStatement("""
                SELECT * FROM users WHERE token = ?;
                """)
                ){

            statement.setString(1, token);
            ResultSet rs = statement.executeQuery();

            if(rs.next() == false){
                throw new DataAccessException("Invalid Token");
            }
            else{
                return new RegisterUser(rs.getString("username"), rs.getString("password"), rs.getInt("coins"), rs.getInt("elo"), rs.getString("token"));
            }

        }catch (SQLException ex){
            rollback();
            ex.printStackTrace();
            throw new DataAccessException("Database error");
        }
    }

    public RegisterUser getUserByUsername(String username){
        try(
                PreparedStatement statement = connection.prepareStatement("""
                SELECT * FROM users WHERE username = ?;
                """)
        ){

            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if(rs.next() == false){
                throw new DataAccessException("Invalid username");
            }
            else{
                return new RegisterUser(rs.getString("username"), rs.getString("password"), rs.getInt("coins"), rs.getInt("elo"), rs.getString("token"));
            }

        }catch (SQLException ex){
            rollback();
            ex.printStackTrace();
            throw new DataAccessException("Database error");
        }
    }

    public int AuthorizeAdmin(String token){
        try(
            PreparedStatement statement = connection.prepareStatement("""
                SELECT * FROM users WHERE token = ?;
            """)
            ){

            statement.setString(1, token);
            ResultSet rs = statement.executeQuery();

            if(rs.next() == false){
                throw new DataAccessException("Authorization failed, invalid token");
            }
            else{
                if(rs.getString("username").equals("admin")){
                    return 0;
                }
                else{
                    throw new DataAccessException("Authorization failed, only the admin can create Packages");
                }
            }

            }catch (SQLException ex){
                ex.printStackTrace();
                throw new DataAccessException("Database error", ex);
        }
    }

    public void commit(){
        try{
            this.connection.commit();
        } catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public void rollback(){
        try{
            this.connection.rollback();
        } catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public Connection getConnection (){
        return connection;
    }
}

