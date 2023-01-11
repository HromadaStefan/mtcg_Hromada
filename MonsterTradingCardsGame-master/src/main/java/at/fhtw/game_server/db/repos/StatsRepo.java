package at.fhtw.game_server.db.repos;

import at.fhtw.game_server.db.dbconfig.ConnectDB;
import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.service.models.RegisterUser;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class StatsRepo {
    private ConnectDB db;
    public StatsRepo(ConnectDB db) {this.db = db;}

    public RegisterUser getStats(String token){
        try{
            RegisterUser user = db.getUserByToken(token);
            return user;
        } catch (DataAccessException dax){
            dax.printStackTrace();
            throw new DataAccessException("Get Stats failed");
        }
    }
}
