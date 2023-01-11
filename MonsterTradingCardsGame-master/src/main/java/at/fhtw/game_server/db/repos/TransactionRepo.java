package at.fhtw.game_server.db.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.service.models.RegisterUser;

import at.fhtw.game_server.db.dbconfig.ConnectDB;

import at.fhtw.game_server.db.dbconfig.ConnectDB;
import net.bytebuddy.dynamic.scaffold.MethodRegistry;

public class TransactionRepo {
    private ConnectDB db;

    public TransactionRepo(ConnectDB db){
        this.db = db;
    }

    public int aquirePackage(String token){
        try(
                PreparedStatement newestpackage_statement = db.getConnection().prepareStatement("""
            SELECT MIN(package) as newestpackageId FROM cards WHERE username IS NULL;
            """);

                PreparedStatement packageAvailable_statement = db.getConnection().prepareStatement("""
            SELECT * FROM cards WHERE username is NULL;
            """);

                PreparedStatement aquire_cards_statement = db.getConnection().prepareStatement("""
            UPDATE cards SET username = ? WHERE package = ?;
            """);

                PreparedStatement update_coins_statement = db.getConnection().prepareStatement("""
            UPDATE users  SET coins = ? WHERE username = ?;
            """);

                PreparedStatement createJoker_statement = db.getConnection().prepareStatement("""
            UPDATE users SET joker = ? WHERE username = ?;
            """);

                PreparedStatement getJoker_statement = db.getConnection().prepareStatement("""
            SELECT joker FROM users WHERE username = ?;
            """);
                ){

            Random rd = new Random();

            RegisterUser user = db.getUserByToken(token);

            if(user.getCoins() < 5) {
                db.rollback();
                throw new DataAccessException("too less coins to aquire package");
            }

            int packageId;

            ResultSet rs = newestpackage_statement.executeQuery();
            rs.next();
            ResultSet rs_packageAvailable = packageAvailable_statement.executeQuery();

            if(rs_packageAvailable.next() == false){
                db.rollback();
                throw new DataAccessException("No package available at the moment");
            }
            else{
                packageId = rs.getInt("newestpackageId");
            }

            update_coins_statement.setInt(1, user.getCoins() - 5);
            update_coins_statement.setString(2, user.getUsername());
            update_coins_statement.execute();

            aquire_cards_statement.setString(1, user.getUsername());
            aquire_cards_statement.setInt(2, packageId);
            aquire_cards_statement.execute();

            if(rd.nextInt() % 10 == 0){
                getJoker_statement.setString(1, user.getUsername());
                ResultSet rs_getJoker = getJoker_statement.executeQuery();
                rs_getJoker.next();

                createJoker_statement.setInt(1, rs_getJoker.getInt("joker") + 1);
                createJoker_statement.setString(2, user.getUsername());
                createJoker_statement.executeUpdate();
                db.commit();
                return 1;
            }

            db.commit();
            return 0;

        } catch(SQLException ex){
            db.rollback();
            ex.printStackTrace();
            throw new DataAccessException("Database error", ex);
        }
    }
}
