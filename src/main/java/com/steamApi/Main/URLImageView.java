package com.steamApi.Main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import com.google.gson.JsonObject;
import com.steamApi.Client.SteamClient;
import com.steamApi.Comparator.DateComparator;
import com.steamApi.Model.Game;
import com.steamApi.Model.GameCard;
import com.steamApi.Parser.JSONParser;
import com.steamApi.Service.SteamService;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Paint;

public class URLImageView extends ImageView {


    private TilePane pane;
    private ScrollPane scrPane;
    private SteamService steamService;
    private DateComparator dc;


    public ScrollPane createViewBox(UserSession session , Runnable refreshCallback){
        
        System.out.println("Inside createview box");
        SteamClient client = new SteamClient("A5A6206490E64785D3271BC7B3A4A734" ,  session.getSteamId());
        List<Game> games = null;

        try {

            JsonObject json = client.getOwnedGames();
			games= JSONParser.parseOwnedGames(json);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        games = games.stream().sorted(new DateComparator()).toList();
        
        pane = new TilePane();

        //pane.setPrefColumns(10);
         pane.setHgap(10);
         pane.setVgap(10);
         pane.setPadding(new Insets(20));
         
        
        // for (Game game : games) {
            
        
        // String imageUrl = game.getGameImageUrl();
        
        // InputStream is = null;
        
		// try {
            // 	is = new FileInputStream(new File("/home/harshvardhan/Screenshot from 2026-06-11 15-24-53.png"));
            // } catch (FileNotFoundException e) {
                // 	// TODO Auto-generated catch block
                // 	e.printStackTrace();
                // }
                
                // Image image =  new Image(imageUrl);
                
                // ImageView iv = new ImageView();
                
                // try {
                    
                //         Double.parseDouble(game.getGameSize().substring(0,5));
                
                //         }
                
                //      catch (Exception e) {
                    
                //         iv.setEffect(new ColorAdjust(1, -10, 0.5, 0.5));
                //         iv.setOpacity(0.45);
                
                // }
                
                
                // iv.setStyle("-fx-border-radius:5");
                // iv.setFitWidth(150);
                // iv.setFitHeight(225);
                // iv.setImage(image);
                // iv.setPreserveRatio(true);
                // iv.setCache(true);
                
                // pane.getChildren().add(iv);
                
                // }
                
                
                scrPane = new ScrollPane(pane);
                scrPane.setFitToWidth(true);
                scrPane.getStyleClass().add("game-scroll-pane");

                pane.prefWidthProperty().bind(scrPane.widthProperty().subtract(10));
               
                for(Game game : games){
                    
                    pane.getChildren().add(new GameCard(game , new SteamService() , refreshCallback , scrPane));
                   
                }
                
                
                return scrPane;
                
            }
            
            public void refresh(List<Game> games , Runnable refreshCallback){

                games = games.stream().sorted(new DateComparator()).toList();
                pane.getChildren().clear();
                System.out.println("redrawing");
                for(Game game : games){

                    pane.getChildren().add(new GameCard(game , new SteamService() , refreshCallback , scrPane));
                    
                }
        
    }
    
}
