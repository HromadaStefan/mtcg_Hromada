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

public class CardsService implements Service {
    private final CardsController cardsController;

    public CardsService(){this.cardsController = new CardsController();}

    public Response handleRequest(Request request){
        if(request.getMethod() == Method.GET){
            String token = request.getHeaderMap().getHeader("Authorization");
            return this.cardsController.getCards(token);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{\"message\" : \"unknown Service\"}"
        );
    }
}
