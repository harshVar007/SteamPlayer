package com.steamApi.UI;

import java.util.concurrent.atomic.AtomicInteger;

import com.steamApi.Model.Game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.css.converter.DurationConverter;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class GameDialog {
    
    private Stage stage = new Stage();
    
    public GameDialog(){
        
        AtomicInteger timeTotal = new AtomicInteger(10);
        Label timer = new Label("Time Remaining : " +  String.valueOf(timeTotal));
        timer.setStyle("-fx-text-fill : white");
        
        Timeline timeline = new Timeline(
            
            new KeyFrame(Duration.seconds(1),e->{  

            int timeRem = timeTotal.decrementAndGet();
            timer.setText("Time Remaining : " +  String.valueOf(timeRem));

        } ));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        Label title = new Label("Steam Dialog");
        Label message = new Label("Waiting for Steam...");
        title.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill : #a4d0f8");
        message.setStyle("-fx-text-fill : white");
        
        
        VBox root = new VBox(15,title,message,timer);
        root.setAlignment(Pos.CENTER);
        
        root.setStyle("-fx-background-color : #0d60ad; -fx-text-fill : white;");
        
        Scene scene = new Scene(root , 500 , 200);
        
        stage.setScene(scene);
        stage.initModality(Modality.NONE);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Steam Installation");
        
    }
    
    public void show()
    {

        stage.show();

    }
    
     
    public void close()
    {
        Platform.runLater(()->{
        
        if(stage.isShowing()){
            stage.close();
        }
    });
}

}
