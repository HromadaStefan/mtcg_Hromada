package at.fhtw.game_server.db.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.service.models.Card;

import at.fhtw.game_server.db.dbconfig.ConnectDB;
import at.fhtw.httpserver.server.Response;

public class PackageRepo {

    private ConnectDB db;

    public PackageRepo(ConnectDB db){
        this.db = db;
    }

    public int createPackage(Card[] cards, String token){
        Card card;
        int packageId;

        try{
            if(db.AuthorizeAdmin(token) != 0){
                throw new DataAccessException("Authorization as admin failed");
            }
        }catch (DataAccessException dax){
            throw dax;
        }

        try(
                PreparedStatement packageId_statement = db.getConnection().prepareStatement("""
                SELECT MAX(package) as maxId FROM cards;
                """);
                ){
            ResultSet rs_packageId = packageId_statement.executeQuery();
            if(rs_packageId.next() == false){
                packageId = 0;
            }
            else{
                packageId = rs_packageId.getInt("maxId") + 1;
                System.out.printf(String.valueOf(packageId));
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            throw new DataAccessException("Selecting package failed");
        }


        for(int i = 0; i < cards.length; i++){
            try(
                PreparedStatement statement = db.getConnection().prepareStatement("""
                INSERT INTO cards (cardid, name, damage, package, elementId, typeId) VALUES (?, ?, ?, ?, ?, ?);
                """);

                PreparedStatement createElement_statement = db.getConnection().prepareStatement("""
                INSERT INTO element (name) VALUES (?);
                """);

                PreparedStatement selectElement_statement = db.getConnection().prepareStatement("""
                SELECT * FROM element WHERE name = ?;
                """);

                PreparedStatement createType_statement = db.getConnection().prepareStatement("""
                INSERT INTO type (name) VALUES (?);
                """);

                PreparedStatement selectType_statement = db.getConnection().prepareStatement("""
                SELECT * FROM type WHERE name = ?;
                """);
                ){

                card = cards[i];
                selectElement_statement.setString(1, cards[i].getElement());
                ResultSet rs_selectElement = selectElement_statement.executeQuery();
                if(rs_selectElement.next() == false){
                    createElement_statement.setString(1, cards[i].getElement());
                    createElement_statement.execute();
                }
                selectElement_statement.setString(1, cards[i].getElement());
                rs_selectElement = selectElement_statement.executeQuery();
                rs_selectElement.next();

                // GET TYPE
                selectType_statement.setString(1, cards[i].getType());
                ResultSet rs_selectType = selectType_statement.executeQuery();
                if(rs_selectType.next() == false){
                    createType_statement.setString(1, cards[i].getType());
                    createType_statement.execute();
                }
                selectType_statement.setString(1, cards[i].getType());
                rs_selectType = selectType_statement.executeQuery();
                rs_selectType.next();

                statement.setString(1, card.getId());
                statement.setString(2, card.getName());
                statement.setInt(3, card.getDamage());
                statement.setInt(4, packageId);
                statement.setInt(5, rs_selectElement.getInt("elementId"));
                statement.setInt(6, rs_selectType.getInt("typeId"));

                statement.execute();
            }
            catch(SQLException ex){
                ex.printStackTrace();
                db.rollback();
                throw new DataAccessException("Failed creating Package");
            }
        }
        db.commit();
        return 0;
    }
}
