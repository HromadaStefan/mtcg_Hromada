package at.fhtw.game_server.service.users;

import at.fhtw.game_server.controller.Controller;
import at.fhtw.game_server.db.dbconfig.ConnectDB;
import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.db.repos.ScoreRepo;
import at.fhtw.game_server.service.models.RegisterUser;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;

public class ScoreController extends Controller {
    public ScoreController(){}
    public Response getScore(String token){
        try{
            ScoreRepo db = new ScoreRepo(new ConnectDB());
            RegisterUser[] scoreboard = db.getScore(token);

            String output = "";
            for(int i = 0; i < scoreboard.length; i++){
                output = output + scoreboard[i].getUsername() + ": " + scoreboard[i].getElo() + "\n";
            }

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    output
            );
        }
        catch(DataAccessException dax){
            dax.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }
    }
}
