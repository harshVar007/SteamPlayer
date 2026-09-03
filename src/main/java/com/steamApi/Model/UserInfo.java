package com.steamApi.Model;

public class UserInfo {
    
    private String profileName;
    private String avatarLink;
    private String realName;

    public UserInfo(String profileName, String avatarLink, String realName) {
        this.profileName = profileName;
        this.avatarLink = avatarLink;
        this.realName = realName;
    }

    
    @Override
    public String toString() {
        return "UserInfo [profileName=" + profileName + ", avatarLink=" + avatarLink + ", realName=" + realName + "]";
    }


    public UserInfo() {
        //TODO Auto-generated constructor stub
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    
    
}
