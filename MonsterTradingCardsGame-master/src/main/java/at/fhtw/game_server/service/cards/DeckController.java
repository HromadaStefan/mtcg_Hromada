package at.fhtw.game_server.service.cards;

import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.db.repos.DeckRepo;
import at.fhtw.game_server.service.models.Card;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.game_server.controller.Controller;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import at.fhtw.game_server.db.dbconfig.ConnectDB;
import com.fasterxml.jackson.core.type.TypeReference;


import javax.xml.crypto.Data;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class DeckController extends Controller{
    public DeckController() {}

    public Response getDeck(String token, String format){
        try{
            DeckRepo db = new DeckRepo((new ConnectDB()));
            Card[] cards = db.getDeck(token);

            String cardsJSON = "";

            if (format.equals("plain")) {
                for(int i = 0; i < cards.length; i++){
                    cardsJSON = cardsJSON + cards[i].getName() + ": " + cards[i].getDamage() + " Type: " + cards[i].getType() + " Element: " + cards[i].getElement() + "\n";
                }
            } else{
                cardsJSON = this.getObjectMapper().writeValueAsString(cards);

            }

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    cardsJSON
            );

        } catch(JsonProcessingException e){
            e.printStackTrace();
        }
        catch(DataAccessException dax){
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{\"message\" : \"Error requesting Cards\"}"
        );
    }

    public Response configureDeck(String body, String token){
        ConnectDB uow = new ConnectDB();
        try{
            DeckRepo db = new DeckRepo(uow);

            List<String> list = new ArrayList<>();
            try {
                list = this.getObjectMapper().readValue(body, ArrayList.class);
            } catch (JacksonException e) {
                e.printStackTrace();
            }

            String[] card_ids = new String[list.size()];
            list.toArray(card_ids);

            if(db.configureDeck(card_ids, token) == 0){
                uow.commit();
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "Deck configured successfully"
                );
            }
            else{
                uow.rollback();
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "Database error"
                );
            }
        } catch(DataAccessException dax){
            uow.rollback();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }
    }

}
