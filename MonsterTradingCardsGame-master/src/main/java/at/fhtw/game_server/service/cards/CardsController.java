package at.fhtw.game_server.service.cards;

import at.fhtw.game_server.db.repos.CardsRepo;
import at.fhtw.game_server.service.models.Card;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.game_server.controller.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import at.fhtw.game_server.db.dbconfig.ConnectDB;


import java.util.List;

public class CardsController extends Controller{
    public CardsController() {}

    public Response getCards(String token){
        try{
            CardsRepo db = new CardsRepo((new ConnectDB()));
            Card[] cards = db.getCards(token);
            String cardsJSON = this.getObjectMapper().writeValueAsString(cards);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    cardsJSON
            );

        } catch(JsonProcessingException e){
            e.printStackTrace();
        }
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{\"message\" : \"Error requesting Cards\"}"
        );
    }

}
