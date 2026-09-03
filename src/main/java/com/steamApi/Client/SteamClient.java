package com.steamApi.Client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SteamClient{


    private final String APIKEY;
    private final String STEAMID;
    private final HttpClient CLIENT;

    //constructor
    public SteamClient(String apiKey, String steamId) {
        this.APIKEY= apiKey;
        this.STEAMID = steamId;
        this.CLIENT = HttpClient.newHttpClient();
    }

    public JsonObject getOwnedGames() throws IOException , InterruptedException{

        
        String url = String.format("https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/" + 
        "?key=%s&steamid=%s&include_appinfo=1&include_played_free_games=1" , APIKEY , STEAMID);

        
        System.out.println(url);

        HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .GET()
        .build();

       
        HttpResponse<String> res = CLIENT.
        send(req, HttpResponse.BodyHandlers.ofString());

        

        //System.out.println(res2.body());
        
        

        return JsonParser.parseString(res.body()).getAsJsonObject();


    }



    public JsonObject getUserInfo() throws IOException , InterruptedException{

        String userInfo = String.format("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=%s&steamids=%s", APIKEY,STEAMID);       

        HttpRequest req2 = HttpRequest.newBuilder()
        .uri(URI.create(userInfo))
        .GET()
        .build();


        HttpResponse<String> res2 = CLIENT.send(req2, HttpResponse.BodyHandlers.ofString());

        return JsonParser.parseString(res2.body()).getAsJsonObject();


    }





}