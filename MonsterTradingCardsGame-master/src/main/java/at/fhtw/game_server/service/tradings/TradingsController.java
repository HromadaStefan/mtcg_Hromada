package at.fhtw.game_server.service.tradings;

import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.db.repos.TradingsRepo;
import at.fhtw.game_server.service.models.trading;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.game_server.controller.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import at.fhtw.game_server.db.dbconfig.ConnectDB;

import javax.swing.text.AbstractDocument;
import javax.xml.crypto.Data;

public class TradingsController extends Controller{
    public TradingsController(){}

    public Response createTradingDeal(String body, String token){
        try{
            TradingsRepo db = new TradingsRepo((new ConnectDB()));

            trading trading = this.getObjectMapper().readValue(body, trading.class);
            if(db.createTradingDeal(trading, token) == 0){
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \" message \" : \"Trading deal created\"}"
                );
            }
        } catch(JsonProcessingException e){
            e.printStackTrace();
        } catch(DataAccessException dax){
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }
        return null;
    }

    public Response acceptTradingDeal(String tradeId, String body, String token){
        try{
            TradingsRepo db = new TradingsRepo((new ConnectDB()));
            String cardId = this.getObjectMapper().readValue(body, String.class);

            if(db.acceptTradingDeal(tradeId, cardId, token) == 0){
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \" message \" : \"Trading deal accepted\"}"
                );
            }
        } catch(JsonProcessingException e){
            e.printStackTrace();
        } catch(DataAccessException dax){
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }
        return null;
    }

    public Response deleteTradingDeal(String tradeId, String token){
        try{
            TradingsRepo db = new TradingsRepo(new ConnectDB());
            if(db.deleteTradingDeal(tradeId, token) == 0){
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \" message \" : \"Trading deal deleted\"}"
                );
            } else{
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \" message \" : \"Failed deleting tradingdeal\"}"
                );
            }
        } catch(DataAccessException dax){
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }
    }

    public Response getTradingDeals(String token){
        try{
            TradingsRepo db = new TradingsRepo(new ConnectDB());

            trading[] deals = db.getTradingDeals(token);
            String dealsJSON = this.getObjectMapper().writeValueAsString(deals);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    dealsJSON
            );
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{\"message\" : \"Error requesting Deals\"}"
        );
    }
}
