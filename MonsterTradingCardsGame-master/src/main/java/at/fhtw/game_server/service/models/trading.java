package at.fhtw.game_server.service.models;

import com.fasterxml.jackson.annotation.JsonAlias;

public class trading {

    @JsonAlias({"Id"})
    private String tradeId;

    @JsonAlias({"CardToTrade"})
    private String cardId;

    @JsonAlias({"Type"})
    private String type;

    @JsonAlias({"MinimumDamage"})
    private int mindamage;

    public trading(){}

    public trading(String tradeId, String cardId, String type, int mindamage){
        this.tradeId = tradeId;
        this.cardId = cardId;
        this.type = type;
        this.mindamage = mindamage;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMindamage() {
        return mindamage;
    }

    public void setMindamage(int mindamage) {
        this.mindamage = mindamage;
    }
}
