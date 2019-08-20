package com.example.malix.takeabreak.ui;

public class User {

    public String mUsername;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User( String userName){
        this.mUsername = userName;
    }
}
