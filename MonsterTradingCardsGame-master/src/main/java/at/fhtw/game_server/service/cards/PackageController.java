package at.fhtw.game_server.service.cards;

import at.fhtw.game_server.db.dbconfig.ConnectDB;
import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.db.repos.CardsRepo;
import at.fhtw.game_server.db.repos.PackageRepo;
import at.fhtw.game_server.service.models.Card;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.game_server.controller.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.swing.text.AbstractDocument;
import javax.xml.crypto.Data;

public class PackageController extends Controller{
    private PackageRepo db;
    public PackageController() {}

    public Response addPackage(String body, String token){
        try{
            this.db = new PackageRepo((new ConnectDB()));

            Card[] cardobjects = this.getObjectMapper().readValue(body, Card[].class);
            System.out.printf(body);
            if(db.createPackage(cardobjects, token) == 0){
                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "{ \" message \" : \"Package created\"}"
                );
            }
        } catch(JsonProcessingException e){
            e.printStackTrace();
        } catch (DataAccessException dax){
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }
        return null;
    }
}
