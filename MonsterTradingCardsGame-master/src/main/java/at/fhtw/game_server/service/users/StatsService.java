package at.fhtw.game_server.service.users;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.game_server.service.users.UserController;

import java.util.List;


public class StatsService implements Service{
    private final StatsController statsController;
    public StatsService(){this.statsController = new StatsController();}
    @Override
    public Response handleRequest(Request request){
        if(request.getMethod() == Method.GET){
            String token = request.getHeaderMap().getHeader("Authorization");
            return this.statsController.getStats(token);
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }

}
