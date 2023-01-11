package at.fhtw.game_server.db.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.service.models.Card;

import at.fhtw.game_server.db.dbconfig.ConnectDB;
import at.fhtw.game_server.service.models.RegisterUser;
import at.fhtw.httpserver.server.Response;


public class DeckRepo {
    private ConnectDB db;

    public DeckRepo(ConnectDB db){ this.db = db;}

    public Card[] getDeck(String token){
        try(
                PreparedStatement getDeck_statement = db.getConnection().prepareStatement("""
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
                    WHERE username = ? AND indeck is TRUE;
                """)
            ){

            List<Card> cards = new ArrayList<Card>();

            String username = db.getUserByToken(token).getUsername();

            getDeck_statement.setString(1, username);
            ResultSet rs_deck = getDeck_statement.executeQuery();

            while(rs_deck.next() != false){
                cards.add(new Card(rs_deck.getString("username"), rs_deck.getString("cardid"), rs_deck.getString("name"), rs_deck.getInt("damage"), rs_deck.getString("elementname"), rs_deck.getString("typename")));
            }

            if(cards.size() == 0){
                throw new DataAccessException("Deck unconfigured");
            }
            else{
                Card[] deck = new Card[cards.size()];
                cards.toArray(deck);
                db.commit();
                return deck;
            }

        } catch (SQLException ex){
            ex.printStackTrace();
            db.rollback();
            throw new DataAccessException("Database error", ex);
        }
    }

    public int configureDeck(String[] card_id_array, String token){

        if(card_id_array.length != 4){
            throw new DataAccessException("Deck must consist of 4 cards. Amount given: " + String.valueOf(card_id_array.length));
        }

        try(
            PreparedStatement configureDeck_true_statement = db.getConnection().prepareStatement("""
            UPDATE cards SET indeck = TRUE WHERE cardid = ? AND username = ? AND instore = FALSE;
            """);

            PreparedStatement configureDeck_false_statement = db.getConnection().prepareStatement("""
            UPDATE cards SET indeck = FALSE WHERE cardid = ? AND username = ?;
            """);

            PreparedStatement getDeck_statement = db.getConnection().prepareStatement("""
            SELECT * FROM cards WHERE indeck = TRUE AND username = ?;
            """);
        ){

            String username = db.getUserByToken(token).getUsername();

            getDeck_statement.setString(1, username);
            ResultSet rs_deck = getDeck_statement.executeQuery();

            while(rs_deck.next() != false){
                configureDeck_false_statement.setString(1, rs_deck.getString("cardid"));
                configureDeck_false_statement.setString(2, username);
                configureDeck_false_statement.execute();
            }

            int r = 0;

            for(int i = 0; i < card_id_array.length; i++){
                configureDeck_true_statement.setString(1, card_id_array[i]);
                configureDeck_true_statement.setString(2, username);
                r = configureDeck_true_statement.executeUpdate();

                if(r == 0){
                    throw new DataAccessException("CardId: " + card_id_array[i] + " not found");
                }
            }
            return 0;

        } catch(SQLException ex){
            ex.printStackTrace();
            throw new DataAccessException("Database error");
        }
    }
}
