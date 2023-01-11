package at.fhtw.game_server.service.users;

import at.fhtw.game_server.db.dbconfig.ConnectDB;
import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.db.repos.UserRepo;
import at.fhtw.game_server.service.models.RegisterUser;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.game_server.controller.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.xml.crypto.Data;
import java.sql.DataTruncation;

public class UserController extends Controller{
    public UserController(){}
    public Response register(String body){
        try{
            UserRepo db = new UserRepo((new ConnectDB()));
            RegisterUser reg_user = this.getObjectMapper().readValue(body, RegisterUser.class);

            if(db.registerUser(reg_user) == 0){
                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "{\"message\" : \"sucessfully registered User\"}"
                );
            }
            return null;

        } catch(JsonProcessingException e){
            e.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        } catch(DataAccessException dax){
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }
    }


    public Response getUser(String username, String token){
        try{
            UserRepo db = new UserRepo((new ConnectDB()));

            RegisterUser reg_user = db.getUserData(username, token);

            String userDataJSON = this.getObjectMapper().writeValueAsString(reg_user);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userDataJSON
            );

        } catch(JsonProcessingException e){
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        } catch(DataAccessException dax){
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }
    }

    public Response updateUser(String username, String body, String token){
        try{
            UserRepo db = new UserRepo((new ConnectDB()));
            RegisterUser update_user = this.getObjectMapper().readValue(body, RegisterUser.class);

                if(db.updateUser(update_user, username, token) == 0){
                    return new Response(
                            HttpStatus.ACCEPTED,
                            ContentType.JSON,
                            "{ message: \"Successfully updated userdata\" }"
                    );
                }
                else{
                    return new Response(
                            HttpStatus.ACCEPTED,
                            ContentType.JSON,
                            "{ message: \"Failed\" }"
                    );
                }
        }
        catch(JsonProcessingException e){
            e.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ message: \"Failed mapping body\" }"
            );
        } catch(DataAccessException dax){
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }
    }
}
