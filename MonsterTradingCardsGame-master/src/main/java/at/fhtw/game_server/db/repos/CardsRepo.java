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

public class CardsRepo {
    private ConnectDB db;

    public CardsRepo(ConnectDB db) {
        this.db = db;
    }

    public Card[] getCards(String token){ // throws SQLException, try und catch entfernen, Errorcode dann im Controller abfragen + rollback.
    try(
            PreparedStatement getCards_statement = db.getConnection().prepareStatement("""
            SELECT
            	c.cardid,
            	c.username,
            	c.name,
            	c.damage,
            	c.package,
            	e.name as elementname,
            	t.name as typename
            FROM cards c
            JOIN element e ON e.elementid = c.elementid
            JOIN type t ON t.typeid = c.typeid
            WHERE username = ?;
            """)
            ){


            List<Card> cards = new ArrayList<Card>();

            String username = db.getUserByToken(token).getUsername();

            getCards_statement.setString(1, username);
            ResultSet rs_cards = getCards_statement.executeQuery();


            while(rs_cards.next() != false){
                cards.add(new Card(rs_cards.getString("cardid"), rs_cards.getString("name"), rs_cards.getInt("damage"), rs_cards.getString("elementname"), rs_cards.getString("typename")));
            }

            Card[] cards_array = new Card[cards.size()];
            cards.toArray(cards_array);
            return cards_array;
    }
    catch (SQLException ex){
        ex.printStackTrace();
        throw new DataAccessException("Database error");
    }
    }
}
