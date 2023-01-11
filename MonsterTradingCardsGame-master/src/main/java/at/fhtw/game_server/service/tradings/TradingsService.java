package at.fhtw.game_server.service.tradings;

import at.fhtw.game_server.service.models.trading;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class TradingsService implements Service{
    private final TradingsController tradingsController;

    public TradingsService(){this.tradingsController = new TradingsController();}

    @Override
    public Response handleRequest(Request request){
        if(request.getMethod() == Method.POST){
            String token = request.getHeaderMap().getHeader("Authorization");
            if (request.getPathParts().size() > 1) {
                return this.tradingsController.acceptTradingDeal(request.getPathParts().get(1), request.getBody(), token);
            } else{
                return this.tradingsController.createTradingDeal(request.getBody(), token);
            }
        } else if (request.getMethod() == Method.DELETE) {
            String token = request.getHeaderMap().getHeader("Authorization");
            if(request.getPathParts().size() > 1){
                return this.tradingsController.deleteTradingDeal(request.getPathParts().get(1), token);
            }
            else{
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \" message \" : \"Missing tradeId\"}"
                );
            }
        } else if (request.getMethod() == Method.GET){
            String token = request.getHeaderMap().getHeader("Authorization");
            return this.tradingsController.getTradingDeals(token);
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
