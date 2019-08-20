package com.example.malix.takeabreak.domain.datalayer;

import android.content.Context;
import android.widget.Toast;

import com.example.malix.takeabreak.technicalServices.database.LocalDatabase;

import java.util.ArrayList;

public class Controller {

  Validator validator;
  Controller controller;
  ProfileDTO profileDTO;
  ResultContainer resultContainer;
  ArrayList<String> result , success;

  public Controller(){
      validator = new Validator();
      controller = new Controller();
      profileDTO = new ProfileDTO();
      resultContainer = new ResultContainer();
      result = new ArrayList<>();
      success = new ArrayList<>();

  }

 public void createProfile( Context context , String profileName , String[] selectesApps , LocalDatabase localDatabase ){

       result = validator.validateRecords(profileName , selectesApps );

       try {
           if (result.isEmpty()){

               profileDTO.setpName(profileName);
               profileDTO.setSelectedApps(selectesApps);
                if(localDatabase.addProfile(profileDTO)){

                    success.add("profile created successfully");
                    Toast.makeText( context , "profile created" , Toast.LENGTH_SHORT).show();

                }



           }else {
               Toast.makeText( context , "profile creation failed" , Toast.LENGTH_SHORT).show();
               resultContainer.results(result);
           }

       }catch (Exception e){
           e.printStackTrace();
       }
 }

}
