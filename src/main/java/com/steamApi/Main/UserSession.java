/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.steamApi.Main;

public class UserSession {

    private final String STEAM_ID;
    private String personaName;
    private String realName;
    private String imageLink;
    
    public UserSession(String steamID) {
    
            this.STEAM_ID = steamID;
    }

    public String getSteamId(){
        return STEAM_ID;
    }

    public UserSession(String sTEAM_ID, String personaName, String realName , String imageLink) {
        this.STEAM_ID = sTEAM_ID;
        this.personaName = personaName;
        this.realName = realName;
        this.imageLink = imageLink;
    }

    public String getPersonaName() {
        return personaName;
    }

    public String getRealName() {
        return realName;
    }

    public String getImageLink() {
        return imageLink;
    }

    

    


}
