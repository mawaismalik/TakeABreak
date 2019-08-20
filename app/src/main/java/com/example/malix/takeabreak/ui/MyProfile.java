package com.example.malix.takeabreak.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.malix.takeabreak.R;
import com.example.malix.takeabreak.domain.datalayer.ProfileDTO;
import com.example.malix.takeabreak.technicalServices.database.LocalDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyProfile extends AppCompatActivity {

    private Context context;
    private LocalDatabase localDatabase;
    private TextInputLayout mProfileName;
    private ListView minstalledAppsList;
    private ArrayList<String> mSelectedApps;
    private AppAdapter appAdapter;
    private List<AppList> installedApps;
    private String mProfileNameEntered;
    private FloatingActionButton mConfirmButton;
    private AppAdapter installedAppAdapter;
    private ArrayList<String> result;
    ProfileDTO profileDTO;
    private ImageView mBackbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        mProfileName = findViewById(R.id.etProfilename);
        minstalledAppsList = findViewById(R.id.installedAppsList);
        mConfirmButton = findViewById(R.id.btnConfirm);
        mSelectedApps = new ArrayList<>();
        appAdapter = new AppAdapter();

        profileDTO = new ProfileDTO();
     //   controller = new Controller();
        localDatabase = new LocalDatabase(this);
        mProfileNameEntered = mProfileName.getEditText().getText().toString().trim();
        context = getApplicationContext();
        result = new ArrayList<>();
        mBackbutton = findViewById(R.id.backbutton);
        mBackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfile.this , ProfileMaking.class);
                startActivity(intent);
                finish();
            }
        });

        //setiing focus of field
        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        //end
        try{
            installedApps = getInstalledApps();
            installedAppAdapter = new AppAdapter(getApplicationContext(), installedApps);
            minstalledAppsList.setAdapter(installedAppAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }



        minstalledAppsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AppList appList = installedApps.get(position);
                 int checkedStatesSize = 0;
                if(appList.isSelected){
                   // mSelectedApps.remove(position);
                    checkedStatesSize = checkedStatesSize-1;
                    appList.setSelected(false);

                    String mSelectedAppName =  installedApps.get(position).getName().trim();
                    mSelectedApps.remove(mSelectedApps.indexOf(mSelectedAppName));
                    Toast.makeText(getApplicationContext() , "unselected",Toast.LENGTH_SHORT).show();
                }else {
                    appList.setSelected(true);
                    checkedStatesSize = checkedStatesSize+1;
                   String mSelectedAppName =  installedApps.get(position).getName().trim();
                   mSelectedApps.add(mSelectedAppName);

                    Toast.makeText(getApplicationContext() , "selected",Toast.LENGTH_SHORT).show();
                }

                installedAppAdapter.updateView();
            }
        });


        //confirm button listner


        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] list = new String[mSelectedApps.size()];
                for(int i = 0 ; i < mSelectedApps.size() ; i++ ){
                    list[i] = mSelectedApps.get(i);
                }
                try {

                    if(mProfileName.getEditText().getText().toString().trim().isEmpty()){
                        Toast.makeText(getApplicationContext() , "enter name" , Toast.LENGTH_SHORT).show();
                    }else if(mSelectedApps.size() == 0){
                        Toast.makeText(getApplicationContext() , "please select apps" , Toast.LENGTH_SHORT).show();
                    }else{

                        profileDTO.setpName(mProfileName.getEditText().getText().toString());
                        profileDTO.setSelectedApps(list);
                        if(localDatabase.addProfile(profileDTO)){
                            Toast.makeText(getApplicationContext(),"Profile created",Toast.LENGTH_SHORT ).show();
                            Intent intent = new Intent(getApplicationContext() , ProfileMaking.class);
                            startActivity(intent);
                            finish();
                        }
                    }




                    /*
                    localDatabase.setContext(context);
                    controller.createProfile(context, mProfileNameEntered, list, localDatabase);
                    setContext(context);

                    if(controller.result.isEmpty())
                    {
                        String msg = controller.success.toString();
                        databaseMessage(msg);
                        Intent intent = new Intent(context, ProfileMaking.class);
                        startActivity(intent);


                    }else {
                        result = controller.result;
                        int size = result.size();
                        for (int index = 0; index < size; index++) {
                            String message = result.get(index);
                            databaseMessage(message);
                        }
                    } */

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    private List<AppList> getInstalledApps() {
        List<AppList> res = new ArrayList<AppList>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((isSystemPackage(p) == false)) {
                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                String packageName = p.packageName;
                res.add(new AppList(appName, icon , packageName));
            }
        }
        return res;
    }


    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public void databaseMessage( CharSequence text1 )
    {
        Context context  = getContext();
        CharSequence text = text1;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}

