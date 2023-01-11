package at.fhtw.game_server.service.transactions;

import at.fhtw.game_server.db.dbconfig.ConnectDB;
import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.db.repos.PackageRepo;
import at.fhtw.game_server.db.repos.TransactionRepo;
import at.fhtw.game_server.service.models.RegisterUser;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.game_server.controller.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.xml.crypto.Data;
import java.sql.Connection;

public class TransactionController extends Controller{
    public TransactionController() {}

    public Response aquire_package(String token){
        TransactionRepo db = new TransactionRepo((new ConnectDB()));

        try{
            int aquire = db.aquirePackage(token);
            if(aquire == 0){
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{\"message\" : \"Success\"}"
                );
            } else if (aquire == 1) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{\"message\" : \"Success, received Joker!\"}"
                );
            } else{
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{\"message\" : \"Error aquiring package\"}"
                );
            }

        }catch (DataAccessException dax){
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }
    }

}
