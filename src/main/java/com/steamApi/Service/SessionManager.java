package com.steamApi.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.steamApi.Config.EnvConfig;
import com.steamApi.Main.UserSession;

public class SessionManager{


    private final static Path SESSION_FILEPATH = Path.of("/home/harshvardhan/.config/SteamViewer/" , "session.json");
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    //serialize steamid from session to .json
    public static void save(UserSession session){

        try {
            

            Files.createDirectories(SESSION_FILEPATH.getParent());

            try ( BufferedWriter writer = Files.newBufferedWriter(SESSION_FILEPATH)) {

                GSON.toJson(session, writer);
                
                
            } catch (Exception e) {
                e.printStackTrace();
            }

            
            
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        }

    
    }

    //de-serialize from .json to session object and return mainScene
    public static UserSession load() throws IOException{

        if(!Files.exists(SESSION_FILEPATH))
            return null;

        try(BufferedReader reader = Files.newBufferedReader(SESSION_FILEPATH)){

            return GSON.fromJson(reader, UserSession.class);

        }catch( IOException ex ){

           throw new IOException();
           
        }
        
        

    }



}