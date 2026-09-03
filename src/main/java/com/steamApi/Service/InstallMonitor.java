package com.steamApi.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.steamApi.Main.JavaFXMain;
import com.steamApi.Model.Game;
import com.steamApi.Parser.SteamLibraries;
import com.steamApi.UI.GameDialog;

import javafx.application.Platform;

public class InstallMonitor {


    public static class DownloadProgress{

        private Long bytesDownloaded;
        private Long bytesToDownload;
        private String status;
        private double progress;

        
        public DownloadProgress(Long bytesDownloaded, Long bytesToDownload, String status, double progress) {
            this.bytesDownloaded = bytesDownloaded;
            this.bytesToDownload = bytesToDownload;
            this.status = status;
            this.progress = progress;
        }
        public Long getBytesDownloaded() {
            return bytesDownloaded;
        }
        public void setBytesDownloaded(Long bytesDownloaded) {
            this.bytesDownloaded = bytesDownloaded;
        }
        public Long getBytesToDownload() {
            return bytesToDownload;
        }
        public void setBytesToDownload(Long bytesToDownload) {
            this.bytesToDownload = bytesToDownload;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public double getProgress() {
            return progress;
        }
        public void setProgress(double progress) {
            this.progress = progress;
        }


    }

    // monitors the .acf file for the specific gameid. if it doesn't exist it asks
    // for a
    // scene refresh else keep checking and stop after some time.

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public void start(Game game, Consumer<DownloadProgress> progressCallback , Runnable onChange , GameDialog dialog) {

        executor.scheduleAtFixedRate(()->{

            try{

            //System.out.println("Inside Try");
            Path manifest = Path.of("/home/harshvardhan/.local/share/Steam/steamapps/" , "appmanifest_" + game.getAppId().toString() + ".acf");
            Path downloadFolder = Path.of("/home/harshvardhan/.local/share/Steam/steamapps/downloading/" + game.getAppId());
            
            if(!Files.exists(manifest)){

                game.setInstalled(false);

                Platform.runLater(()->{


                    progressCallback.accept(new DownloadProgress(0L, 0L, "Uninstalled", 0.0));
                    onChange.run();
                });

                executor.shutdown();    
                return;

            }            
            
            String data = Files.readString(manifest);
            long bytesToDownload = extractData(data , "BytesToStage");
            long bytesDownloaded = getFolderSize(downloadFolder);
            long appSize = extractData(data , "SizeOnDisk");

            System.out.println(bytesDownloaded);
            System.out.println(bytesToDownload);
            System.out.println(appSize);


            if(Files.exists(manifest)){
                
                    if(appSize > 0L){

                        game.setGameSize(String.format("%s", SteamLibraries.bytestoGigabytes(appSize).toString().substring(0, 5)));
                        game.setInstalled(true);

                        
                        Platform.runLater(()->{progressCallback.accept(new DownloadProgress(bytesToDownload, bytesToDownload, "Installed", 1.0));
                        onChange.run();
                        
                    });
                    
                    executor.shutdown();
                    return;
                }
                else{
                    
                        double progress = 0.0;
                    
                        dialog.close();

                        progress = (double)bytesToDownload/bytesDownloaded;
                    
                        DownloadProgress downProgress = new DownloadProgress(bytesDownloaded, bytesToDownload, "Installing", progress); 
                        Platform.runLater(()->{ progressCallback.accept(downProgress);});

                    }
                  
            }

        }catch(IOException  ex){

            ex.printStackTrace();
        }
            
            
    }, 10, 5, TimeUnit.SECONDS);

        
}


public static long extractData(String text , String key)
{
    
    Pattern sizePattern = Pattern.compile("\""+key+"\"\\s+\"(\\d+)\"" , Pattern.MULTILINE);
    Matcher sizeMatcher = sizePattern.matcher(text);

    if(sizeMatcher.find()){

        return Long.parseLong(sizeMatcher.group(1));

    }

    return 0L;

}

public static String extractString(String text , String key)
{
    
    Pattern sizePattern = Pattern.compile("\""+key+"\"\\s+\"(.+)\"" , Pattern.MULTILINE);
    Matcher sizeMatcher = sizePattern.matcher(text);

    if(sizeMatcher.find()){

        return sizeMatcher.group(1);

    }

    return "";

}


private static long getFolderSize(Path path){


    try {

        System.out.println(Paths.get(path.toString()));
        long size = Files.walk(path).filter(p->p.toFile().isFile()).mapToLong(p-> p.toFile().length()).sum();
        return size;
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    return 0L;

}



}