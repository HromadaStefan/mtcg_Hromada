package at.fhtw.game_server.service.battle;

import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import lombok.Synchronized;

public class BattleService implements Service {
    private final BattleController battleController;

    public BattleService() {this.battleController = new BattleController();}

    public synchronized Response handleRequest(Request request){
        if(request.getMethod() == Method.POST){
            String token = request.getHeaderMap().getHeader("Authorization");
            return this.battleController.battle(token);
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }

}


