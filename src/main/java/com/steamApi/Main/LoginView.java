package com.steamApi.Main;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.steamApi.Client.SteamClient;
import com.steamApi.Model.UserInfo;
import com.steamApi.Parser.JSONParser;
import com.steamApi.Server.SteamCallback;
import com.steamApi.Service.SessionManager;

import javafx.animation.ScaleTransition;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class LoginView {

    // callback server to extract SteamId
    private SteamCallback callbackServer;

    // declare scene
    public Font font = new Font("Roboto", 20);

    // object create to open browser
    public HostServices hostServices;
    
    //Avatar Image
    public static Image avatarImage = null;

    // User Session Consumer
    private final Consumer<UserSession> onLoginSuccess;

    private static final String STEAM_OPENID_URL = "https://steamcommunity.com/openid/login" +
            "?openid.ns=http://specs.openid.net/auth/2.0" +
            "&openid.mode=checkid_setup" +
            "&openid.return_to=http://localhost:8088/callback" +
            "&openid.realm=http://localhost:8088/" +
            "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select" +
            "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select";

    public LoginView(HostServices hostServices, Consumer<UserSession> onLoginSuccess){

        this.hostServices = hostServices;
        this.onLoginSuccess = onLoginSuccess;

    }

    public Scene createScene(){

        // Label
        Label steamLabel = new Label("Steam Library Viewer");
        steamLabel.setFont(font);
        steamLabel.setStyle("-fx-text-fill:white");

        // Login Button
        Button loginButton = new Button("Login With Steam");
        
        // Offline Button
        Button offlineButton = new Button();
        

        //Set Button Animation
        offlineButton.setOnMouseEntered(e->{

            ScaleTransition transition = new ScaleTransition(javafx.util.Duration.millis(300) , offlineButton);
            transition.setToX(1.2);
            transition.setToY(1.2);
            transition.play();
        });

        offlineButton.setOnMouseExited(e->{

            ScaleTransition transition = new ScaleTransition(javafx.util.Duration.millis(300) , offlineButton);
            transition.setToX(1.0);
            transition.setToY(1.0);
            transition.setDelay(Duration.millis(50));
            transition.play();
        });
        
        // LoginStatus
        Label statusLabel = new Label("");
        
        // Vertical Layout
        VBox root = new VBox(20, steamLabel, statusLabel, offlineButton , loginButton);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color : #0A1B3F");
        
        
        
        // button event
        loginButton.setOnAction(e -> {
            
            System.out.println("clicked");
            
            statusLabel.setText("Waiting for Steam Login");
            statusLabel.setStyle("-fx-text-fill : #ffffff");
            statusLabel.setTextAlignment(TextAlignment.CENTER);
            
            callbackServer = new SteamCallback();
            
            try {
                callbackServer.start(steamid -> {
                    
                    System.out.println("Steam Id recieved is : " + steamid);
                    
                    UserSession session = new UserSession(steamid);
                    
                    //save session in .json
                    SessionManager.save(session);
                    
                    Platform.runLater(() -> {
                        onLoginSuccess.accept(session);
                        
                        UserSession session2;
                        JsonObject userData = null;
                        
                        try {
                            session2 = SessionManager.load();
                            userData = new SteamClient("A5A6206490E64785D3271BC7B3A4A734", session.getSteamId()).getUserInfo();
                        } catch (IOException | InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        
                        List<UserInfo> userList = JSONParser.parseUserInfo(userData);
                        
                        session2 = new UserSession(session.getSteamId(), userList.get(0).getProfileName(), userList.get(0).getRealName() , userList.get(0).getAvatarLink());

                        
                        offlineButton.setStyle("""
                            -fx-background-color : transparent;
                            -fx-padding : 0;
                            -fx-border-width : 0;
                            -fx-cursor : hand;
                        """);
                        
                        SessionManager.save(session2);
                        
                        
                    });
                    
                });
                
            } catch (IOException ex) {
                
                statusLabel.setText("Failed to Start server");
            }
            
            hostServices.showDocument(STEAM_OPENID_URL);
        });
        
        
        try {
            
            
            UserSession session = SessionManager.load();
            
            if(session == null){
                
                offlineButton.setDisable(true);

                offlineButton.setStyle("""
                    -fx-background-color: transparent;
                    -fx-padding: 0;
                    -fx-border-width: 0;
                    -fx-appearance: none;
                """);

                statusLabel.setText("No Saved login Found.Please login once to save");
                statusLabel.setStyle("-fx-text-fill:#ff0000");

                }  
            else
                {
                
                // Circle Avatar Image
                Circle avatar = new Circle(60);
                avatar.setFill(Paint.valueOf("#1b5299"));
                
                Tooltip.install(offlineButton, new Tooltip("Continue as " + session.getPersonaName()));

               
                
                avatarImage = new Image(session.getImageLink(), true);
                
                avatarImage.progressProperty().addListener((obs,oldVal,newVal)->{
                    
                    if(newVal.doubleValue() >= 1.0 && !avatarImage.isError()){
                        
                        avatar.setFill(new ImagePattern(avatarImage));
                    }
                    
                });


                offlineButton.setGraphic(avatar);

                offlineButton.setStyle("""
                    -fx-background-color: transparent;
                    -fx-padding: 0;
                    -fx-border-width: 0;
                    -fx-cursor: hand;
                """);
                
                statusLabel.setText("Saved Login Found with Profile Name : " + session.getPersonaName());
                statusLabel.setStyle("-fx-text-fill : #0fdf4d");
                statusLabel.setPadding(new Insets(10));

                loginButton.setText("Change Account");
                
                offlineButton.setOnAction(e -> {
                onLoginSuccess.accept(session);
        
        });
    }       


        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
         

       return new Scene(root, 400, 400);

    }

}
