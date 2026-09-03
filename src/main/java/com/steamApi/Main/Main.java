
// import java.io.IOException;
// import java.time.Instant;
// import java.time.LocalDateTime;
// import java.time.ZoneId;
// import java.time.format.DateTimeFormatter;
// import java.util.List;

// import com.google.gson.JsonObject;
// import com.steamApi.Client.SteamClient;
// import com.steamApi.Model.Game;
// import com.steamApi.Parser.JSONParser;


// public class Main {

//     @SuppressWarnings("CallToPrintStackTrace")
//     public static void main(String[] args) {
//         System.out.println("Started");

//         try {

//             SteamClient client = new SteamClient("BA7E0A066DC0DCBFFC91944B9F5F039F", "76561198389143187");

//             JsonObject json = client.getOwnedGames();

//             List<Game> games = JSONParser.parseOwnedGames(json);

//             System.out.print("Total Games Owned : " + games.size()+ "\n");

//             for (Game g : games) {
//                 System.out.println(g.getAppId() + " - " + g.getName() + " - " + g.getLastPlayed() +
//                         " (" + g.getMinutesPlayed() / 60 + " Hours)" + " Last Played : " + timestampToDateTime(g.getLastPlayed()));
//             }


//         } catch (IOException | InterruptedException e) {

//             e.printStackTrace();
//         }

//     }


//     private static String timestampToDateTime(String unixTimestamp){

//         LocalDateTime date = LocalDateTime.ofInstant
//         (Instant.ofEpochSecond(Long.parseLong(unixTimestamp)), ZoneId.systemDefault());


//         DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");


//         return date.format(format);
        
//     }

    

// }

package com.steamApi.Main;

import java.io.IOException;

import com.steamApi.Config.EnvConfig;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static EnvConfig envConfig;

    @Override
    public void start(Stage stage) throws IOException {

        // LoginView loginView = new LoginView(
            
        //     getHostServices(), 
        //     session->{

        //     try {
        //         JavaFXMain mainView = new JavaFXMain();
        //         Scene mainScene = mainView.createScene(session);
        //         stage.setScene(mainScene);
                
        //     } catch (Exception e) {
        //         System.out.println("Initialization Failed... \n");
        //         e.printStackTrace();
        //     }
        // });

        showLogin(stage);
        

    }

    public void showLogin(Stage stage){

        
        LoginView loginView = new LoginView(
            
            getHostServices(), 
            session->{

            try {
                showMain(stage, session);        
            } catch (Exception e) {
                System.out.println("Initialization Failed... \n");
                e.printStackTrace();
            }
        });

        
        stage.setTitle("Steam Game Viewer");
        stage.setScene(loginView.createScene());
        stage.show();
       

    }

    public void showMain(Stage stage , UserSession session){


        JavaFXMain main = new JavaFXMain(session,()->showLogin(stage));
        try {
            stage.setScene(main.createScene(session));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.version"));
        System.out.println(System.getProperty("java.home"));

        envConfig = new EnvConfig();
        envConfig.saveEnvConfig();

        launch(args);
    }
}