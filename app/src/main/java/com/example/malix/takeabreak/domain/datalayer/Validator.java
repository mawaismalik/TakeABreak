package com.example.malix.takeabreak.domain.datalayer;

import java.util.ArrayList;

public class Validator {

    boolean dcom;
    boolean rate;
    int len = 0;
    ArrayList<String> result;

    public ArrayList<String> validateRecords(String profileName , String[] selectedApps){

        result = new ArrayList<>();

        try {

            if(profileName.isEmpty()){
                result.add("please enter profile name");
            }else if (selectedApps.length == 0){
                result.add("Please select some apps");
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
