package at.fhtw.game_server.service.users;

import at.fhtw.httpserver.server.Service;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;

public class ScoreService implements Service {
    private final ScoreController scoreController;
    public ScoreService(){this.scoreController = new ScoreController();}
    @Override
    public Response handleRequest(Request request){
        if(request.getMethod() == Method.GET){
            String token = request.getHeaderMap().getHeader("Authorization");
            return this.scoreController.getScore(token);
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
