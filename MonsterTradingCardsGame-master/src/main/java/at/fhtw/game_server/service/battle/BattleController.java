package at.fhtw.game_server.service.battle;

import at.fhtw.game_server.db.dbconfig.DataAccessException;
import at.fhtw.game_server.db.repos.TradingsRepo;
import at.fhtw.game_server.service.models.trading;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.game_server.controller.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import at.fhtw.game_server.db.dbconfig.ConnectDB;

import at.fhtw.game_server.db.repos.BattleRepo;

import javax.xml.crypto.Data;

public class BattleController extends Controller{
    public BattleController(){}

    public Response battle(String token){
        try{
            BattleRepo db = new BattleRepo(new ConnectDB());
            String winner = db.battle(token);
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        winner
                );

        } catch (DataAccessException dax){
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    dax.getMessage()
            );
        }
    }
}
