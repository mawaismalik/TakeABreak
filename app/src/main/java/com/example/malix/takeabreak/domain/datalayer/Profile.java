package com.example.malix.takeabreak.domain.datalayer;

public class Profile {


    public String mProfilename;
    public String[] mSelectedApps;

    public Profile(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Profile( String profileName , String[] selectedApps){
        this.mProfilename = profileName;
        this.mSelectedApps = selectedApps;
    }
}
