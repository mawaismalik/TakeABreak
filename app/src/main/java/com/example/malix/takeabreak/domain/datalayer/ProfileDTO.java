package com.example.malix.takeabreak.domain.datalayer;

public class ProfileDTO {

 public  ProfileDTO(){}

    //Variables
    private String userName;
    private String pName;
    private String[] selectedApps;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpName() {
        return pName;
    }

    public String[] getSelectedApps() {
        return selectedApps;
    }

    public void setSelectedApps(String[] selectedApps) {
        this.selectedApps = selectedApps;
    }
}
