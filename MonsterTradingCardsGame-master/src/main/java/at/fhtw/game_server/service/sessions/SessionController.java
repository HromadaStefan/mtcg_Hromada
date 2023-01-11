package at.fhtw.game_server.service.sessions;

import at.fhtw.game_server.db.dbconfig.ConnectDB;
import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.db.repos.PackageRepo;
import at.fhtw.game_server.db.repos.SessionRepo;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.game_server.controller.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import at.fhtw.game_server.service.models.Session;

import javax.swing.text.AbstractDocument;
import javax.xml.crypto.Data;
import java.lang.constant.Constable;
import java.net.ConnectException;
import java.sql.Connection;


public class SessionController extends Controller{

    public SessionController(){}

    public Response login(String body){
        try{
            SessionRepo db = new SessionRepo((new ConnectDB()));

            Session create_session = this.getObjectMapper().readValue(body, Session.class);
            String token = db.loginUser(create_session);

            if(token != null){
            String tokenJSON = this.getObjectMapper().writeValueAsString(token);
                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        tokenJSON
                );
            }
        }catch(JsonProcessingException e){
            e.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    e.getMessage()
            );
        } catch(DataAccessException dax){
            dax.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }

}
