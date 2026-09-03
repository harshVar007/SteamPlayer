package com.steamApi.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.steamApi.Service.SessionManager;

public class EnvConfig {
    
    private Properties prop = new Properties();
    private SessionManager manager;
    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public EnvConfig(){

        prop.setProperty("OS_NAME", System.getProperty("os.name"));
        prop.setProperty("USER_NAME", System.getProperty("user.name"));

        if(prop.get("OS_NAME").equals("Linux") ){
            prop.setProperty("USER_PATH", Path.of("/home/"+prop.getProperty("USER_NAME").toString()).toString()+"/");
            prop.setProperty("CONFIG_PATH", Path.of(prop.getProperty("USER_PATH")+".config/SteamViewer/").toString());
            prop.setProperty("STEAM_APPS_PATH", Path.of(prop.getProperty("USER_PATH")+".local/share/Steam/steamapps/").toString());
            prop.setProperty("STEAM_PATH", Path.of(prop.getProperty("USER_PATH")+".local/share/Steam/").toString());

        }
        else
        {
        prop.setProperty("STEAM_PATH", "C:\\ProgramFiles(x86)\\Steam\\steamapps");
        prop.setProperty("USER_PATH", Path.of("C:\\Users:\\"+System.getProperty("user.name")).toString());
        prop.setProperty("CONFIG_PATH", Path.of("C:\\ProgramFiles(x86)\\Steam\\steamapps").toString());
           

        }

        prop.setProperty("ENV_CONFIG_PATH", prop.getProperty("USER_PATH"));
    }

    public void saveEnvConfig(){

        System.out.println(prop);

        try (BufferedWriter br = Files.newBufferedWriter(Path.of(prop.getProperty("USER_PATH")+"envConfig.json")) ) {

            GSON.toJson(prop,br);
            System.out.println("File Written Successfully");
            
        } catch (Exception e) {

            
            e.printStackTrace();
        }


        
    }
    
    public Properties getEnvProps()
    {

        try(BufferedReader br = Files.newBufferedReader(Path.of(prop.getProperty("USER_PATH")+"envConfig.json")))
        {
            return GSON.fromJson(br, EnvConfig.class).prop;
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        
            return null;
        
    }        
}
