package at.fhtw.game_server.db.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import at.fhtw.game_server.db.dbconfig.ConnectDB;
import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.service.models.Card;
import at.fhtw.game_server.service.models.trading;

import javax.xml.crypto.Data;

public class TradingsRepo {
    private ConnectDB db;
    public TradingsRepo(ConnectDB db) {this.db = db;}

    public trading[] getTradingDeals(String token){
        try(
                PreparedStatement getDeals_statement = db.getConnection().prepareStatement("""
                SELECT * FROM tradings t JOIN type e ON e.typeid = t.type;
            """)
            ){
            db.getUserByToken(token);

            List<trading> deals = new ArrayList<trading>();

            ResultSet rs_getDeals = getDeals_statement.executeQuery();

            while(rs_getDeals.next() != false){
                deals.add(new trading(rs_getDeals.getString("tradeid"), rs_getDeals.getString("cardid"), rs_getDeals.getString("name"), rs_getDeals.getInt("damage")));
            }

            trading[] deals_array = new trading[deals.size()];
            deals.toArray(deals_array);
            return deals_array;

        }catch(SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Database error", ex);
        } catch (DataAccessException dax){
            throw dax;
        }
    }

    public int createTradingDeal(trading trading, String token){
        try(
                PreparedStatement checkCardOwner = db.getConnection().prepareStatement("""
                SELECT * FROM cards WHERE cardId = ? AND username = ?;
                """);

                PreparedStatement createTradingDealStatement = db.getConnection().prepareStatement("""
                    INSERT INTO tradings 
                    (tradeid, cardid, type, damage, username)
                    VALUES (?, ?, ?, ?, ?);
                """);

                PreparedStatement getTypeId = db.getConnection().prepareStatement("""
                    SELECT * FROM type WHERE name = ?;
                """)
                ){
            String username = db.getUserByToken(token).getUsername();

            checkCardOwner.setString(1, trading.getCardId());
            checkCardOwner.setString(2, username);

            ResultSet rs_checkCardOwner = checkCardOwner.executeQuery();

            if(rs_checkCardOwner.next() == false){
                throw new DataAccessException("You dont own this card");
            }

            if(rs_checkCardOwner.getBoolean("indeck") == true){
                throw new DataAccessException("Your card is selected in your deck. Please remove the card from your active deck");
            }

            getTypeId.setString(1, trading.getType());
            ResultSet rs_getTypeId = getTypeId.executeQuery();

            int typeId;

            if(rs_getTypeId.next() != false){
                typeId = rs_getTypeId.getInt("typeid");
            }
            else{
                throw new DataAccessException("unknown type");
            }

            createTradingDealStatement.setString(1, trading.getTradeId());
            createTradingDealStatement.setString(2, trading.getCardId());
            createTradingDealStatement.setInt(3, typeId);
            createTradingDealStatement.setInt(4, trading.getMindamage());
            createTradingDealStatement.setString(5, username);
            createTradingDealStatement.execute();
            db.commit();
            return 0;

        } catch (SQLException ex){
            db.rollback();
            ex.printStackTrace();
            String sqlstate = ex.getSQLState();

            if(sqlstate.equals("23505")){
                throw new DataAccessException("Trading deal already exists", ex);
            } else if(sqlstate.equals("23502")){
                throw new DataAccessException("No TradingId given", ex);
            } else{
                throw new DataAccessException("Database Error");
            }
        }
    }

    public int checkOwner(String tradeId, String token){
        try(
                PreparedStatement proof_statement = db.getConnection().prepareStatement("""
                    SELECT * FROM tradings WHERE username = ? AND tradeid = ?;
                """)
                ){
                String username = db.getUserByToken(token).getUsername();

                proof_statement.setString(1, username);
                proof_statement.setString(2, tradeId);
                ResultSet rs_proof = proof_statement.executeQuery();

                if(rs_proof.next() == false){
                    return 1;
                }
                else{
                    return 0;
                }
        } catch(SQLException ex){
            ex.printStackTrace();
            return 1;
        }
    }

    public int deleteTradingDealWithoutToken(String tradeId){
        try(

                PreparedStatement deleteTradingId_statement = db.getConnection().prepareStatement("""
                    DELETE FROM tradings WHERE tradeid = ?; 
                """)
        ){
            deleteTradingId_statement.setString(1, tradeId);
            deleteTradingId_statement.execute();
            return 0;
        } catch (SQLException ex){
            ex.printStackTrace();
            throw new DataAccessException("Database error", ex);
        }
    }

    public int deleteTradingDeal(String tradeId, String token){
        try(

                PreparedStatement deleteTradingId_statement = db.getConnection().prepareStatement("""
                    DELETE FROM tradings WHERE tradeid = ?; 
                """)
        ){
            if(checkOwner(tradeId, token) == 0){
                deleteTradingId_statement.setString(1, tradeId);
                deleteTradingId_statement.execute();
                db.commit();
                return 0;
            }
            else{
                db.rollback();
                throw new DataAccessException("You don't own this card!");
            }

        } catch (SQLException ex){
            ex.printStackTrace();
            db.rollback();
            throw new DataAccessException("Database error", ex);
        }
    }

    public int acceptTradingDeal(String tradeId, String cardId, String token){
        try(
                PreparedStatement getDeal_statement = db.getConnection().prepareStatement("""
                    SELECT * FROM tradings where tradeid = ?;
                """);

                PreparedStatement checkCardOwner_statement = db.getConnection().prepareStatement("""
                    SELECT * FROM cards WHERE cardid = ? AND username = ? ;
                """);

                PreparedStatement swapOwner_statement = db.getConnection().prepareStatement("""
                    UPDATE cards SET username = ? WHERE username = ? AND cardid = ?;
                """)
                ){

            String username = db.getUserByToken(token).getUsername();

                getDeal_statement.setString(1, tradeId);
                ResultSet rs_getDeal_statement = getDeal_statement.executeQuery();

                if(rs_getDeal_statement.next() == false){
                    throw new DataAccessException("invalid tradeid");
                }

            checkCardOwner_statement.setString(1, cardId);
            checkCardOwner_statement.setString(2, username);
            ResultSet rs_checkCardOwner_statement = checkCardOwner_statement.executeQuery();


            if(rs_checkCardOwner_statement.next() == false){
                throw new DataAccessException("You don't own this card.");
            }

            if (rs_checkCardOwner_statement.getBoolean("indeck") == true) {
                throw new DataAccessException("Your card is selected in your deck. Please remove the card from your active deck");
            }

            if(rs_checkCardOwner_statement.getInt("typeid") != rs_getDeal_statement.getInt("type") || rs_checkCardOwner_statement.getInt("damage") < rs_getDeal_statement.getInt("damage")){
                throw new DataAccessException("Your cards does not match the requirements of the other user");
            }

            if(rs_checkCardOwner_statement.getString("username").equals(rs_getDeal_statement.getString("username"))){
                throw new DataAccessException("You cant trade with yourself");
            }

            swapOwner_statement.setString(1, username);
            swapOwner_statement.setString(2, rs_getDeal_statement.getString("username"));
            swapOwner_statement.setString(3, rs_getDeal_statement.getString("cardid"));
            swapOwner_statement.executeUpdate();

            swapOwner_statement.setString(1, rs_getDeal_statement.getString("username"));
            swapOwner_statement.setString(2, username);
            swapOwner_statement.setString(3, cardId);
            swapOwner_statement.executeUpdate();

            if(deleteTradingDealWithoutToken(tradeId) != 0){
                throw new DataAccessException("Failed deleting the Deal");
            }

            db.commit();
            return 0;
        } catch (SQLException ex){
            db.rollback();
            ex.printStackTrace();
            throw new DataAccessException("Database error");
            //  Errorhandling
        }
    }
}
