package com.steamApi.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.steamApi.Model.Game;

import javafx.application.Platform;

public class Monitor {
    
    private static Map<String,Boolean> running = new HashMap<>();
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private boolean hasStarted = false;

    public void start( Game game , Consumer<Map<String,Boolean>> callback ){

      service.scheduleAtFixedRate( ()->{

        running = isRunning(game);

        System.out.println(hasStarted + " : " + !running.get(game.getName()));

        if(running.get(game.getName())){
          
          hasStarted = true;
          
        }

        if(hasStarted && !isRunning(game).get(game.getName())){
         
          stop();
        }
        
        Platform.runLater(()-> callback.accept(running));
        
      }, 10, 2, TimeUnit.SECONDS);

      
    }

    public void stop(){
        
        if(service != null && !service.isShutdown()){
          service.shutdown();
        }
    }

    private Map<String,Boolean> isRunning(Game game){
        
        Stream<ProcessHandle> stream = ProcessHandle.allProcesses();
        
        running.put(game.getName(),stream.anyMatch(process -> process.info().commandLine().toString().contains(game.getAppId().toString())));

        return running;

    }
}
