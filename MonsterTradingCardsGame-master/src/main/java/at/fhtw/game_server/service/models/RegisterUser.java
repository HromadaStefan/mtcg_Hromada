package at.fhtw.game_server.service.models;

import com.fasterxml.jackson.annotation.JsonAlias;

public class RegisterUser {

    @JsonAlias({"Username"})
    private String username;

    @JsonAlias({"Password"})
    private String passwort;

    @JsonAlias({"Coins"})
    private int coins;

    @JsonAlias({"Elo"})
    private int elo;

    @JsonAlias({"Token"})
    private String token;

    @JsonAlias({"Name"})
    private String name;

    @JsonAlias({"Bio"})
    private String bio;

    @JsonAlias({"Image"})
    private String image;


    public RegisterUser(){}

    public RegisterUser(String username, int elo){
        this.username = username;
        this.elo = elo;
    }

    public RegisterUser(String username, String passwort){
        this.username = username;
        this.passwort = passwort;
    }

    public RegisterUser(String username, String passwort, int coins, int elo, String token){
        this.username = username;
        this.passwort = passwort;
        this.coins = coins;
        this.elo = elo;
        this.token = token;
    }

    public RegisterUser(String username, String passwort, int coins, int elo, String token, String name, String bio, String image){
        this.username = username;
        this.passwort = passwort;
        this.coins = coins;
        this.elo = elo;
        this.token = token;
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public RegisterUser(String name, String bio, String image){
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
