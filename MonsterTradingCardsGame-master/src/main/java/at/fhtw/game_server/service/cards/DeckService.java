package at.fhtw.game_server.service.cards;

import at.fhtw.game_server.controller.Controller;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import net.bytebuddy.agent.builder.AgentBuilder;

import java.lang.module.ModuleDescriptor;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class DeckService implements Service {
    private final DeckController deckController;

    public DeckService(){this.deckController = new DeckController();}

    public Response handleRequest(Request request){
        if(request.getMethod() == Method.GET){
            String format = "normal";
            if(request.getParams() != null){
                String[] param = request.getParams().split("=");
                format = param[1];
            }
            String token = request.getHeaderMap().getHeader("Authorization");
            return this.deckController.getDeck(token, format);
        } else if (request.getMethod() == Method.PUT) {
            String token = request.getHeaderMap().getHeader("Authorization");
            return this.deckController.configureDeck(request.getBody(), token);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\" : \"unknown Service\"}"
        );
    }
}
