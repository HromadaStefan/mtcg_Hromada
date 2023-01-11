package at.fhtw.game_server.service.users;

import at.fhtw.game_server.db.dbconfig.ConnectDB;
import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.db.repos.StatsRepo;
import at.fhtw.game_server.service.models.RegisterUser;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.game_server.controller.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;

public class StatsController extends Controller{
    public StatsController() {}
    public Response getStats(String token){
        try{
            StatsRepo db = new StatsRepo(new ConnectDB());
            RegisterUser user = db.getStats(token);

            String output = user.getUsername() + "\nElo: " + String.valueOf(user.getElo()) + "\nCoins: " + String.valueOf(user.getCoins());
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    output
            );
        } catch (DataAccessException dax){
            dax.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }
    }

}
