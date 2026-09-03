package com.steamApi.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.steamApi.Main.JavaFXMain;
import com.steamApi.Model.Game;
import com.steamApi.Parser.SteamLibraries;
import com.steamApi.UI.GameDialog;


public class SteamService {
    
    private ProcessBuilder pb;
    private Monitor monitor;
    private InstallMonitor installMonitor = new InstallMonitor();
    

    //start the game
    public void playGame(Game g , Runnable refreshCallback , GameDialog dialog){

        System.out.println("Game Recieved " + g.toString());
        pb = new ProcessBuilder("steam" , "steam://run/" + g.getAppId());
        
        if(!g.isInstalled()){
            
                try {

                    pb = new ProcessBuilder("steam" , "steam://install/" + g.getAppId());
                    pb.start();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                

                installMonitor.start( 
                g, 
                progress->{

                    if(!g.isInstalled()){

                    
                        JavaFXMain.downloadProgress.setVisible(true);
                        JavaFXMain.downloadProgress.setPrefWidth(250);
                        
                        Double downloaded = SteamLibraries.bytestoGigabytes(progress.getBytesDownloaded());
                        Double total = SteamLibraries.bytestoGigabytes(progress.getBytesToDownload());
                        
                        JavaFXMain.downloadProgress.setProgress(downloaded/total);
                        JavaFXMain.downloadStatus.setText( String.format("Installing %.2f %%",(( downloaded/total )*100))); 
                    }
                    else{
                        
                    JavaFXMain.downloadProgress.setVisible(false);
                    JavaFXMain.downloadProgress.setPrefWidth(0);
                                                
                    JavaFXMain.downloadStatus.setText(g.getName() + " : " + progress.getStatus()); 


                    }

                }, 
                refreshCallback,
                dialog);

        }

        monitor = new Monitor();

            if(g.isInstalled()){

                    try {
                        pb.start();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    monitor.start(g, running->{
                        
                        

                        String res ="";
                        
                        for(String s : running.keySet()){

                            String status = running.get(s) ? "Running" : "Stopped";
                            res += s + " : " + status + "\n";

                        }

                        //System.out.println(res);
                        JavaFXMain.currentStatus.setText(res);
                                
                            
        });
    }

    }

    public void uninstall(Game g , Runnable refreshCallback , GameDialog dialog){

        pb = new ProcessBuilder("steam" , "steam://uninstall/" + g.getAppId());
        
        try {
			pb.start();

            installMonitor.start(g, progress->{

                JavaFXMain.downloadProgress.setPrefWidth(0);
                JavaFXMain.downloadStatus.setText(progress.getStatus() + " : " +g.getName()); 

            },refreshCallback,dialog);
            
            
		} catch (IOException e) {
			
			e.printStackTrace();
		}



    }
    public void displayProperties(Game g){}

    //open installation folder
    public void getFiles(Game g){

        Path manifest = Path.of("/home/harshvardhan/.local/share/Steam/steamapps/" , "appmanifest_" + g.getAppId().toString() + ".acf");

        String data = "";
        try {
            data = Files.readString(manifest);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(InstallMonitor.extractString(data, "installdir"));
        String gamePath = "/home/harshvardhan/.local/share/Steam/steamapps/common/" + InstallMonitor.extractString(data, "installdir");

        System.out.println("Opening Path : " + gamePath);
        
        pb = new ProcessBuilder("xdg-open" , gamePath);
        
        try {
			pb.start();
		} catch (IOException e) {
			
			e.printStackTrace();
		}



    }


}
