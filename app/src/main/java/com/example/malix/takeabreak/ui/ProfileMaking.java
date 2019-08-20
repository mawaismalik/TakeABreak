package com.example.malix.takeabreak.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malix.takeabreak.R;
import com.example.malix.takeabreak.technicalServices.database.LocalDatabase;

import java.util.ArrayList;

public class ProfileMaking extends AppCompatActivity {

    private LocalDatabase mlocalDatabase;
    private ImageView addProfile;
    private ImageView editProfile;
    private ListView  mAvailableProfilesList;
    Animation animation ;
    private TextView mActivatedProfileView;
    private String mClickedProfileName;
    private ArrayAdapter<String> arrayAdapter;
    private  String mListItem;
    private ArrayList<String> arrayList;
    ListAdapter listAdapter;
    int itemID = -1;
    int mActive_item_ID = -1;
    private TextView mUserName;
    private ImageView mbackButton;
    private static final String TAG = "ProfileMaking";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_making);


        //intialization
        mListItem = "";
        mUserName = findViewById(R.id.currentUserName);
        mActivatedProfileView = findViewById(R.id.tvActivatedprofile);
        mAvailableProfilesList = findViewById(R.id.availableProfilesList);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomin);
        addProfile = findViewById(R.id.imgAddprofile);
        editProfile = findViewById(R.id.imgEditprofile);
        mlocalDatabase = new LocalDatabase(this);
        arrayList = new ArrayList<>();
        mbackButton = findViewById(R.id.backbutton);

        mbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileMaking.this,SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Active Profile Name Allotment

        SharedPreferences sharedPreferences = getSharedPreferences("TakeABreak",MODE_PRIVATE);
        mActivatedProfileView.setText(sharedPreferences.getString("Active_profile_name", "Current Profile"));
        mActive_item_ID = sharedPreferences.getInt("item_ID",-1);
        profileLists();
        if (arrayList.isEmpty()) {
            Intent intent = new Intent(ProfileMaking.this, MyProfile.class);
            startActivity(intent);
            finish();
        }


        addProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(ProfileMaking.this, MyProfile.class);
                startActivity(intent);
                finish();


            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProfileMaking.this, CurrentProfile.class);
                intent.putExtra("profileName" , mActivatedProfileView.getText().toString().trim());
                intent.putExtra("id",mActive_item_ID);
                startActivity(intent);
                finish();

            }
        });

        mAvailableProfilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mClickedProfileName = mAvailableProfilesList.getItemAtPosition(position).toString().trim();

                Cursor data = mlocalDatabase.selectItemId(mClickedProfileName); //get the id associated with that name
                 itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }


                new AlertDialog.Builder(ProfileMaking.this)
                        .setTitle("Profile")
                        .setMessage("Select what you want to do with this profile")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(itemID > -1){
                                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
                                    mlocalDatabase.deleteProfile(mClickedProfileName , itemID);

                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"No ID associated with that name",Toast.LENGTH_SHORT).show();
                                }
                                //mlocalDatabase.deleteProfile(mClickedProfileName);
                                Toast.makeText(ProfileMaking.this , "Profile Deleted" , Toast.LENGTH_SHORT).show();
                                reloadData();
                                profileLists();
                            }
                        })
                        .setNegativeButton("Activate", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences.Editor editor = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                                editor.putString("Active_profile_name", mClickedProfileName);
                                editor.putInt("item_ID", itemID);
                                editor.commit();
                                        mActivatedProfileView.setText(mClickedProfileName);
                                        mActive_item_ID = itemID;
                            }
                        }).create().show();


            }
        });
    }



    public void profileLists(){

        Log.d("profie" , "Populating list view");

        Cursor data = mlocalDatabase.getData();
        while (data.moveToNext()){
            arrayList.add(data.getString(1).trim());
        }
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , arrayList);
        mAvailableProfilesList.setAdapter(listAdapter);
    }
    public void reloadData(){

        Cursor data = mlocalDatabase.getData();
        while (data.moveToNext()){
            arrayList.add(data.getString(1).trim());
        }
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , arrayList);
        ((ArrayAdapter) listAdapter).clear();
        ((ArrayAdapter) listAdapter).addAll(arrayList);
        ((ArrayAdapter) listAdapter).notifyDataSetChanged();
        mAvailableProfilesList.setAdapter(listAdapter);
    }


}
