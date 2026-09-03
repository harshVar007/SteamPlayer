package com.steamApi.Model;


import com.steamApi.Main.JavaFXMain;
import com.steamApi.Service.Monitor;
import com.steamApi.Service.SteamService;
import com.steamApi.UI.GameDialog;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class GameCard extends StackPane{

    private ImageView cover;
    private Label label;
	private Monitor monitor;
    private ScrollPane scrollPane;
    

    public GameCard(Game game , SteamService service , Runnable refreshCallback , ScrollPane scrollPane) 
    {
    

        double imageWidth = 140;

        ContextMenu menu = new ContextMenu();
        MenuItem install = new MenuItem("Install Game");
        BorderPane fallback = new BorderPane();

        
        menu.getStyleClass().add("game-context-menu");

        fallback.setPrefSize(imageWidth , imageWidth * (2/3));

        Label title = new Label(game.getName());

        title.setWrapText(true);
        title.setMaxWidth(imageWidth);
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setStyle("-fx-text-fill:white");

        fallback.setBottom(title);
        fallback.setBackground(Background.fill(Paint.valueOf("#143c77")));
        
        BorderPane.setAlignment(title, Pos.CENTER);
            
        System.out.println("Constructing gameCard of " + game.toString());
        
        Image img = new Image(game.getGameImageUrl() , true);
        
        img.progressProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal.doubleValue() >= 1.0) {

                if (img.isError()) {

                    getChildren().clear();
                    getChildren().setAll(fallback);
                    
                } 
            }
        });
        

        cover = new ImageView(img);
        cover.setSmooth(false);
        cover.setFitWidth(imageWidth);
        cover.setPreserveRatio(true);
        cover.setCache(true);
        
        
        img.progressProperty().addListener((obs,oldval,newVal) ->{
            
            if(newVal.doubleValue() >= 1.0){
                
                javafx.animation.FadeTransition ft = new FadeTransition(Duration.millis(250) , cover);
                ft.setFromValue(0);
                ft.setInterpolator(Interpolator.EASE_IN);
                ft.setToValue(1);
                ft.play();
                
            }
            
            
        });
        
        getChildren().add(cover);
        
        if(monitor == null){
            
            JavaFXMain.currentStatus.setText("No games Playing Currently");
        }
        
        try {
            
                //Parse size ( cause it's a string idk why i did this)
                Double.parseDouble(game.getGameSize().substring(0,5));
                System.out.println(game.toString());
                
                MenuItem play = new MenuItem("Play");
                MenuItem getFiles = new MenuItem("Browse Files");
                MenuItem uninstall = new MenuItem("Uninstall");
                MenuItem properties = new MenuItem("Properties");
                
                menu.getItems().addAll(play,getFiles,uninstall,properties);
                // menu.setPrefWidth(100);
                // menu.setPrefHeight(150);

                
                //calling Steamservice methods
                play.setOnAction(e ->{service.playGame(game, refreshCallback, null);});
                getFiles.setOnAction(e->{service.getFiles(game); });
                uninstall.setOnAction(e->{GameDialog dialog = new GameDialog();
                                            dialog.show(); 
                                        service.uninstall(game , refreshCallback,dialog);});
                properties.setOnAction(e->{service.displayProperties(game);});

                
                setOnMouseClicked(e->{

                    if(e.getButton().equals(MouseButton.PRIMARY)){

                try{
            
                    //Desktop.getDesktop().browse(URI.create("steam steam://run/" + game.getAppId()));
                    ProcessBuilder pb = new ProcessBuilder("steam" , "steam://run/" + game.getAppId());
                    pb.start();
                    
                    //monitor.stop();
                    
                    monitor = new Monitor();
                    
                    //starts monitor gamestatus thread
                    monitor.start(game, running->{

                        
                        String res ="";
                        
                        for(String s : running.keySet()){

                            String status = running.get(s) ? "Running" : "Stopped";
                            res += s + " : " + status + "\n";

                        }

                        JavaFXMain.currentStatus.setText(res);
                        
                        
                        
                    });
                    
                    
                }catch(Exception ex){
                    
                    ex.printStackTrace();
                    
                }
                
            }
            
        });
        
    }
    
    catch (Exception e) {   
        
        //grayscale for non installed games.
        cover.setEffect(new ColorAdjust(0, -1, 0.2, 0.0));

        //menu props
        menu.setPrefWidth(100);
        menu.setPrefHeight(150);
        menu.getItems().add(install);

        //calls play --> install monitor --> refreshes after install
        install.setOnAction(event->{ 
            
            GameDialog dialog = new GameDialog();
            dialog.show();

            service.playGame(game, refreshCallback , dialog);

        });
        
    }

    //animation properties set for all games 
    setOnMouseEntered(e-> animate(1.05));
    setOnMouseExited(e-> animate(1.0));
    setOnMousePressed(e-> animate(0.97));
    setOnMouseReleased(e-> animate(isHover() ? 1.05 : 1.0));
    setOnContextMenuRequested(e-> { menu.show(this, e.getScreenX(), e.getScreenY()); 
        e.consume();
    });
    
}



public boolean isMonitoring(){

    return ( this.monitor == null ) ? true : false;
}

public ImageView getCover() {
    return cover;
}
public void setCover(ImageView imageView) {
    this.cover = imageView;
}
	public Label getLabel() {
		return label;
	}
	public void setLabel(Label label) {
		this.label = label;
	}
    
    private void animate(double scale){

        Timeline timeline = new Timeline(
        new KeyFrame(Duration.millis(250), 
        new KeyValue(scaleXProperty(), scale),
        new KeyValue(scaleYProperty(), scale))
        );

        timeline.play();
    }

}
