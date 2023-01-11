package at.fhtw.game_server.db.repos;

import at.fhtw.game_server.db.dbconfig.ConnectDB;
import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.service.models.Card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleRepo {
    private ConnectDB db;

    public BattleRepo(ConnectDB db) {this.db = db;}

    public String battle(String token){
        try(
            PreparedStatement getwaitingbattle = db.getConnection().prepareStatement("""
            SELECT * FROM battles WHERE user2 is NULL;
            """);

            PreparedStatement createBattle_statement = db.getConnection().prepareStatement("""
            INSERT INTO battles (user1) VALUES (?);
                """);

            PreparedStatement fight_statement = db.getConnection().prepareStatement("""
            UPDATE battles SET user2 = ?, winner = ? WHERE battleid = ?;
            """);

            PreparedStatement updateElo_statement = db.getConnection().prepareStatement("""
            UPDATE users SET elo = ? WHERE username = ?;
            """);

            PreparedStatement getJoker_statement = db.getConnection().prepareStatement("""
            SELECT joker FROM users WHERE username = ?;
            """);

            PreparedStatement setJoker_statement = db.getConnection().prepareStatement("""
            UPDATE users SET joker = ? WHERE username = ?;
            """);
            ){
            String username = db.getUserByToken(token).getUsername();
            ResultSet rs_waitingbattle = getwaitingbattle.executeQuery();
            List<Card> userdeck = getDeck(username);

            if(rs_waitingbattle.next() == false){
                createBattle_statement.setString(1, username);
                createBattle_statement.execute();
                db.commit();
                return "waiting for battle...";
            }
            else{
                String username1 = rs_waitingbattle.getString("user1");
                String username2 = username;


                if(username1.equals(username2)){
                    throw new DataAccessException("you are not allowed to play against yourself");
                }

                int battleId = rs_waitingbattle.getInt("battleid");
                Random rd = new Random();
                String winner = null;

                List<Card> user1_deck;
                List<Card> user2_deck;

                Card c1;
                Card c2;
                Card winner_card;

                String output = "";

                user1_deck = getDeck(username1);
                user2_deck = getDeck(username2);

                int random1;
                int random2;

                for (int i = 0; i < 100; i++){

                    if(user1_deck.size() < 1){
                        System.out.printf("BREAK");
                        winner = username2;
                        break;
                    }
                    if(user2_deck.size() < 1){
                        winner = username1;
                        System.out.printf("BREAK");
                        break;
                    }

                    random1 = rd.nextInt() % user1_deck.size();
                    if(random1 < 0){
                        random1 = random1 * -1;
                    }
                    c1 = user1_deck.get(random1);

                    random2 = rd.nextInt() % user2_deck.size();
                    if(random2 < 0){
                        random2 = random2 * -1;
                    }
                    c2 = user2_deck.get(random2);

                    winner_card = Fear(c1, c2);
                    if(winner_card != null){
                        if(c1.getUsername().equals(winner_card.getUsername())){
                            output = output + c1.getUsername() + ": " + c1.getName() + "(" + String.valueOf(c1.getDamage()) + ") vs " + c2.getUsername() + ": " + c2.getName() + "(" + String.valueOf(c2.getDamage()) + ") => " + winner_card.getUsername() + " wins (Fear)\n";
                            c2.setUsername(username1);
                            user1_deck.add(c2);
                            user2_deck.remove(random2);
                            if(getCard(winner_card.getUsername(), c2) != 0){
                                throw new DataAccessException("Database error");
                            }
                        } else{
                            output = output + c1.getUsername() + ": " + c1.getName() + "(" + String.valueOf(c1.getDamage()) + ") vs " + c2.getUsername() + ": " + c2.getName() + "(" + String.valueOf(c2.getDamage()) + ") => " + winner_card.getUsername() + " wins (Fear)\n";
                            c1.setUsername(username2);
                            user2_deck.add(c1);
                            user1_deck.remove(random1);
                            if(getCard(winner_card.getUsername(), c1) != 0){
                                throw new DataAccessException("Database error");
                            }
                        }
                        continue;
                    }

                    if(!c1.getType().equals("spell") && !c2.getType().equals("spell")){
                        if(c1.getDamage() > c2.getDamage()){
                            winner_card = c1;
                            output = output + c1.getUsername() + ": " + c1.getName() + "(" + String.valueOf(c1.getDamage()) + ") vs " + c2.getUsername() + ": " + c2.getName() + "(" + String.valueOf(c2.getDamage()) + ") => " + winner_card.getUsername() + " wins\n";
                            c2.setUsername(username1);
                            user1_deck.add(c2);
                            user2_deck.remove(random2);
                            if(getCard(username1, c2) != 0){
                                throw new DataAccessException("Database error");
                            }
                        } else if (c1.getDamage() < c2.getDamage()) {
                            winner_card = c2;
                            output = output + c1.getUsername() + ": " + c1.getName() + "(" + String.valueOf(c1.getDamage()) + ") vs " + c2.getUsername() + ": " + c2.getName() + "(" + String.valueOf(c2.getDamage()) + ") => " + winner_card.getUsername() + " wins\n";
                            c1.setUsername(username2);
                            user2_deck.add(c1);
                            user1_deck.remove(random1);
                            if(getCard(username2, c1) != 0){
                                throw new DataAccessException("Database error");
                            }
                        }
                        else{
                            output = output + c1.getUsername() + ": " + c1.getName() + "(" + String.valueOf(c1.getDamage()) + ") vs " + c2.getUsername() + ": " + c2.getName() + "(" + String.valueOf(c2.getDamage()) + ") => Draw!\n";
                        }

                    } else{
                        winner_card = Fight(c1, c2);

                        if(winner_card == null){
                            output = output + c1.getUsername() + ": " + c1.getName() + "(" + String.valueOf(c1.getDamage()) + ") vs " + c2.getUsername() + ": " + c2.getName() + "(" + String.valueOf(c2.getDamage()) + ") => Draw!\n";
                            continue;
                        }
                        else if(c1.getUsername().equals(winner_card.getUsername())){
                            output = output + c1.getUsername() + ": " + c1.getName() + "(" + String.valueOf(c1.getDamage()) + ") vs " + c2.getUsername() + ": " + c2.getName() + "(" + String.valueOf(c2.getDamage()) + ") => " + winner_card.getUsername() + " wins\n";
                            c2.setUsername(username1);
                            user1_deck.add(c2);
                            user2_deck.remove(random2);
                            if(getCard(winner_card.getUsername(), c2) != 0){
                                throw new DataAccessException("Database error");
                            }
                        } else{
                            output = output + c1.getUsername() + ": " + c1.getName() + "(" + String.valueOf(c1.getDamage()) + ") vs " + c2.getUsername() + ": " + c2.getName() + "(" + String.valueOf(c2.getDamage()) + ") => " + winner_card.getUsername() + " wins\n";
                            c1.setUsername(username2);
                            user2_deck.add(c1);
                            user1_deck.remove(random1);
                            if(getCard(winner_card.getUsername(), c1) != 0){
                                throw new DataAccessException("Database error");
                            }
                        }
                    }
                }


                if(winner != null){
                    fight_statement.setString(1, username2);
                    fight_statement.setString(2, winner);
                    fight_statement.setInt(3, battleId);
                    fight_statement.executeUpdate();


                    if(winner.equals(username2)){
                        getJoker_statement.setString(1, username1);
                        ResultSet rs_getJoker = getJoker_statement.executeQuery();
                        rs_getJoker.next();

                        if(rs_getJoker.getInt("joker") > 0){
                            db.rollback();
                            output = output + winner + " wins but the looser used the Joker --> Draw!";
                            setJoker_statement.setInt(1, rs_getJoker.getInt("joker") - 1);
                            setJoker_statement.setString(2, username1);
                            setJoker_statement.executeUpdate();
                            db.commit();
                            return output;
                        }

                        updateElo_statement.setInt(1, db.getUserByToken(token).getElo() + 5);
                        updateElo_statement.setString(2, db.getUserByToken(token).getUsername());
                        updateElo_statement.executeUpdate();

                        updateElo_statement.setInt(1, db.getUserByUsername(username1).getElo() -3);
                        updateElo_statement.setString(2, db.getUserByUsername(username1).getUsername());
                        updateElo_statement.executeUpdate();


                    } else{
                        getJoker_statement.setString(1, username2);
                        ResultSet rs_getJoker = getJoker_statement.executeQuery();
                        rs_getJoker.next();

                        if(rs_getJoker.getInt("joker") > 0){
                            db.rollback();
                            output = output + winner + " wins but the looser used the Joker --> Draw!";
                            int jokers = rs_getJoker.getInt("joker") - 1;
                            setJoker_statement.setInt(1,  jokers);
                            setJoker_statement.setString(2, username2);
                            setJoker_statement.executeUpdate();
                            db.commit();
                            return output;
                        }

                        updateElo_statement.setInt(1, db.getUserByUsername(username1).getElo() + 5);
                        updateElo_statement.setString(2, db.getUserByUsername(username1).getUsername());
                        updateElo_statement.executeUpdate();

                        updateElo_statement.setInt(1, db.getUserByToken(token).getElo() - 3);
                        updateElo_statement.setString(2, db.getUserByToken(token).getUsername());
                        updateElo_statement.executeUpdate();


                    }

                    output = output + winner + " wins";

                    reconfigureDeck(winner);
                    db.commit();

                    return output;
                } else{
                    db.rollback();

                    fight_statement.setString(1, username2);
                    fight_statement.setString(2, "draw");
                    fight_statement.setInt(3, battleId);
                    fight_statement.executeUpdate();
                    db.commit();
                    output = output + "Draw!";
                    return output;
                }
            }
        } catch(SQLException ex){
            db.rollback();
            ex.printStackTrace();
            throw new DataAccessException("Database error");
        }
        catch (DataAccessException dax){
            db.rollback();
            dax.printStackTrace();
            throw dax;
        } catch(Exception e){
            e.printStackTrace();
            db.rollback();
            throw new DataAccessException("Database error");
        }
    }

    public int getCard(String username, Card getsCard){
        try(
                PreparedStatement changeOwner_statement = db.getConnection().prepareStatement("""
                    UPDATE cards SET username = ? WHERE cardid = ?;
                """)
                ){
            changeOwner_statement.setString(1, username);
            changeOwner_statement.setString(2, getsCard.getId());
            changeOwner_statement.executeUpdate();
            return 0;
        } catch (SQLException ex){
            ex.printStackTrace();
            return 1;
        }
    }

    public Card Fear(Card c1, Card c2){
        if(c1.getName().contains("Goblin") && c2.getName().contains("Dragon")){
            return c2;
        } else if (c2.getName().contains("Goblin") && c1.getName().contains("Dragon")) {
            return c1;
        } else if (c1.getName().contains("Wizzard") && c2.getName().contains("Ork")) {
            return c1;
        } else if(c1.getName().contains("Ork") && c2.getName().contains("Wizzard")){
            return c2;
        } else if (c1.getName().contains("WaterSpell") && c2.getName().contains("Knight")) {
            return c1;
        } else if (c2.getName().contains("WaterSpell") && c1.getName().contains("Knight")) {
            return c2;
        } else if (c1.getName().contains("Kraken") && c2.getType().equals("spell")) {
            return c1;
        } else if (c1.getName().contains("spell") && c2.getType().equals("Kraken")) {
            return c2;
        } else if (c1.getName().contains("FireElve") && c2.getType().equals("Dragon")) {
            return c1;
        } else if (c2.getName().contains("FireElve") && c1.getType().equals("Dragon")) {
            return c2;
        } else{
            return null;
        }
    }

    public Card Fight(Card c1, Card c2){
        if(!c2.getType().equals("spell") && c1.getType().equals("spell")){
            if(c1.getElement().equals("water") && c2.getElement().equals("fire")){
                if(c1.getDamage() * 2 > c2.getDamage()){
                    return c1;
                } else if(c1.getDamage() * 2 == c2.getDamage()){
                    return null;
                }
                else{
                    return c2;
                }
            } else if (c1.getElement().equals("fire") && c2.getElement().equals("water")) {
                if(c1.getDamage() * 0.5 > c2.getDamage()){
                    return c1;
                }
                else if(c1.getDamage() * 0.5 == c2.getDamage()){
                    return null;
                }
                else{
                    return c2;
                }
            } else{
                if(c1.getDamage() > c2.getDamage()){
                    return c1;
                } else if(c1.getDamage() == c2.getDamage()){
                    return null;
                }
                else {
                    return c2;
                }
            }
        } else if(!c1.getType().equals("spell") && c2.getType().equals("spell")){
            if(c2.getElement().equals("water") && c1.getElement().equals("fire")){

                if(c2.getDamage() * 2 > c1.getDamage()){
                    return c2;
                } else if(c2.getDamage() * 2 == c1.getDamage()){
                    return null;
                }
                else{
                    return c1;
                }
            } else if (c2.getElement().equals("fire") && c1.getElement().equals("water")) {

                if(c2.getDamage() * 0.5 > c1.getDamage()){
                    return c2;
                } else if(c2.getDamage() * 0.5 == c1.getDamage()){
                    return null;
                }
                else{
                    return c1;
                }
            } else{
                if(c2.getDamage() > c1.getDamage()){
                    return c2;
                } else if(c1.getDamage() == c2.getDamage()){
                    return null;
                }
                else {
                    return c1;
                }
            }
        } else{
            if (c1.getElement().equals("water") && c2.getElement().equals("fire")) {
                if (c1.getDamage() * 2 > c2.getDamage() * 0.5) {
                    return c1;
                } else if (c1.getDamage() * 2 == c2.getDamage() * 0.5) {
                    return null;
                } else {
                    return c2;
                }
            } else if (c1.getElement().equals("fire") && c2.getElement().equals("water")) {
                if (c1.getDamage() * 0.5 > c2.getDamage() * 2) {
                    return c1;
                } else if (c1.getDamage() * 0.5 == c2.getDamage() * 2) {
                    return null;
                } else {
                    return c2;
                }
            } else {
                if (c1.getDamage() > c2.getDamage()) {
                    return c1;
                } else if (c1.getDamage() == c2.getDamage()) {
                    return null;
                } else {
                    return c2;
                }
            }
        }
    }

    public List<Card> getDeck(String username){
        try(
                PreparedStatement getUserData = db.getConnection().prepareStatement("""
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
            """);

                ){
                List<Card> user1_cards = new ArrayList<Card>();

                getUserData.setString(1, username);
                ResultSet rs_user1_deck = getUserData.executeQuery();

                while(rs_user1_deck.next() != false){
                    user1_cards.add(new Card(rs_user1_deck.getString("username"), rs_user1_deck.getString("cardid"), rs_user1_deck.getString("name"), rs_user1_deck.getInt("damage"), rs_user1_deck.getString("elementname"), rs_user1_deck.getString("typename")));
                }

                if(user1_cards.size() == 0){
                    throw new DataAccessException("Deck unconfigured");
                }else{
                    return user1_cards;
                }
        } catch (SQLException ex){
            ex.printStackTrace();
            throw new DataAccessException("error getting configured Deck");
        }
    }

    private int reconfigureDeck(String username){
        try(
                PreparedStatement setCardFalse_statement = db.getConnection().prepareStatement("""
                    UPDATE cards SET indeck = FALSE WHERE cardid = ?;
                """);
                ){
                List<Card> deck = getDeck(username);

                for(int i = 0; i < 4; i++){
                    setCardFalse_statement.setString(1, deck.get(i).getId());
                    setCardFalse_statement.executeUpdate();
                    deck.remove(i);
                }

                if(deck.size() != 4) {
                    throw new DataAccessException("Invalid Card amount in the deck");
                }else{
                    return 0;
                }
        } catch (SQLException ex){
            ex.printStackTrace();
            throw new DataAccessException("unable to reconfigure Deck");
        }
    }
}
