package com.steamApi.Parser;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.steamApi.Config.EnvConfig;
import com.steamApi.Model.Game;
import com.steamApi.Model.UserInfo;


public class JSONParser {
    
    private static EnvConfig envConfig = new EnvConfig();
//parse JSON returned by STEAMAPI

public static List<Game> parseOwnedGames (JsonObject json) throws Exception{

    List<Game> games = new ArrayList<>();
    Properties props = envConfig.getEnvProps();

    if(!json.has("response")) return games;
    JsonObject response = json.getAsJsonObject("response");

    if (!response.has("games")) return games;
    JsonArray arr = response.getAsJsonArray("games");
    
    //final List<Double> sizeList = new ArrayList<>(); 
    
    final Map<String,String> sizeMap = SteamLibraries.getLibraryPath(Path.of(props.getProperty("STEAM_PATH")));
    
    arr.forEach(element ->{

        JsonObject obj = element.getAsJsonObject();

        Game game = new Game();
        game.setAppId(obj.get("appid").getAsLong());
        game.setLastPlayed(obj.get("rtime_last_played").getAsString());
        game.setName(obj.get("name").getAsString());
        game.setMinutesPlayed(obj.get("playtime_forever").getAsInt());
        
        if(sizeMap.containsKey(game.getAppId().toString())){
            game.setGameSize(sizeMap.get(game.getAppId().toString())+" GB");
        }
        else
            game.setGameSize("Not Installed");

        // Build Logo URL
        //String logoId = obj.get("img_icon_url").getAsString();
        String imageUrl = "https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/"+game.getAppId()+"/library_600x900.jpg";

        game.setGameImageUrl(imageUrl);
        games.add(game);

        // for(Game g : games){
        //     System.out.println(g.toString());
        // }

    });

    return games;


}    


public static List<UserInfo> parseUserInfo(JsonObject userObj){

    List<UserInfo> userList = new ArrayList<>();

    if(!userObj.has("response")) return userList;
    JsonObject response = userObj.getAsJsonObject("response");

    if (!response.has("players")) return userList;
    JsonArray arr = response.getAsJsonArray("players");

    
    arr.forEach(element ->{

        UserInfo info = new UserInfo();

        JsonObject obj = element.getAsJsonObject();
        info.setProfileName(obj.get("personaname").getAsString());
        info.setAvatarLink(obj.get("avatarfull").getAsString());
        info.setRealName(obj.get("realname").getAsString());

        userList.add(info);
    });

    return userList;
    
}


}