package at.fhtw.game_server.service.users;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.game_server.service.users.UserController;

import java.util.List;

public class UserService implements Service{
    private final UserController userController;
    public UserService(){this.userController = new UserController();}
    @Override
    public Response handleRequest(Request request){
        if(request.getMethod() == Method.POST){
            return this.userController.register(request.getBody());
        }

        if(request.getMethod() == Method.GET){
            String token = request.getHeaderMap().getHeader("Authorization");
            return this.userController.getUser(request.getPathParts().get(1), token);
        }

        if(request.getMethod() == Method.PUT){
            String token = request.getHeaderMap().getHeader("Authorization");
            return this.userController.updateUser(request.getPathParts().get(1), request.getBody(), token);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
