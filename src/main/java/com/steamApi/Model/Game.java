package com.steamApi.Model;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Game {

    private String name;
    private int minutesPlayed;
    private String lastPlayed;
    private String gameImageUrl;
    private Long appId;
    private String gameSize;
    private boolean isInstalled;
    
    public boolean isInstalled() {
		try{
            
            Double.parseDouble(getGameSize().substring(0,5));
            
            
        }
        catch(Exception e){
            return false;    
        }
        isInstalled = true;
        return isInstalled;
        
	}

	public void setInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
	}
	@Override
    public String toString() {
        return "Game [name=" + name + ", minutesPlayed=" + minutesPlayed + ", lastPlayed=" + lastPlayed + ", appId="
                + appId + ", isInstalled=" + isInstalled +"]";
    }
    public Long getAppId() {
        return appId;
    }
    public void setAppId(Long appId) {
        this.appId = appId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getMinutesPlayed() {
        return minutesPlayed;
    }
    public void setMinutesPlayed(int minutesPlayed) {
        this.minutesPlayed = minutesPlayed;
    }
    public String getLastPlayed() {
        return lastPlayed;
    }
    public void setLastPlayed(String lastPlayed) {
        this.lastPlayed = lastPlayed;
    }
    public String getGameImageUrl() {
        return gameImageUrl;
    }
    public void setGameImageUrl(String gameImageUrl) {
        this.gameImageUrl = gameImageUrl;
    }

    public int getPlaytimeHours() {
        return minutesPlayed / 60;
    }

    public String getLastPlayedFormatted() {
        if (lastPlayed.equals("0")) return "Never";

        LocalDateTime dt = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(Long.parseLong(lastPlayed)),
                ZoneId.systemDefault()
        );
        return dt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
    }

    public String getGameSize() {
        return gameSize;
    }

    public void setGameSize(String gameSize) {
        this.gameSize = gameSize;
    }

    
}
