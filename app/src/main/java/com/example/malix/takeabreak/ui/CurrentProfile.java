package com.example.malix.takeabreak.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.malix.takeabreak.R;
import com.example.malix.takeabreak.domain.datalayer.ProfileDTO;
import com.example.malix.takeabreak.technicalServices.database.LocalDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.example.malix.takeabreak.technicalServices.database.LocalDatabase.strSeparator;

public class CurrentProfile extends AppCompatActivity {

    private Context context;
    private ArrayList<String> mSelectedApps;
    private AppAdapter appAdapter;
    private List<AppList> installedApps;
    private AppAdapter installedAppAdapter;
    private ArrayList<String> result;
    ProfileDTO profileDTO;
    private Toolbar toolbar;

    //--------------
    private LocalDatabase mLocalDatabase;
    private ArrayAdapter listAdapter;
    private ArrayList<String> arrayList;
    private ListView mProfileListView;
    private TextInputLayout mProfileName;
    private String CurrentProfileName;
    private ListView mPreviuosApps;
    private Button mUpdate;
    int id = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_profile);

        //variables
        context = getApplicationContext();
        mSelectedApps = new ArrayList<>();
        appAdapter = new AppAdapter();
        result = new ArrayList<>();
        profileDTO = new ProfileDTO();


        //variable intialization
        mLocalDatabase = new LocalDatabase(this);
        arrayList = new ArrayList<>();
        mProfileListView = findViewById(R.id.curentProfileAppsList);
        mProfileName = findViewById(R.id.etEditProfileName);
        final Intent intent = getIntent();
        CurrentProfileName =  intent.getStringExtra("profileName");
        mUpdate = findViewById(R.id.btnUpdate);
        mProfileName.getEditText().setText(CurrentProfileName);
        id = intent.getIntExtra("id",-1);
        mPreviuosApps = findViewById(R.id.tvprofileappslist);
        currentProfileAppsLists();
        //setting focus

        toolbar = findViewById(R.id.editProfileToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_keyboard_backspace_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CurrentProfile.this , ProfileMaking.class);
                startActivity(intent);
                finish();
            }
        });
        //list population
        try{
            installedApps = getInstalledApps();
            installedAppAdapter = new AppAdapter(getApplicationContext(), installedApps);
            mProfileListView.setAdapter(installedAppAdapter);

            int max = installedApps.size();
            for(int i=0 ; i<max ;i++ ){

             int mApp_selected =   installedApps.indexOf(array[i]);

                    AppList appList = installedApps.get(mApp_selected);
                    appList.setSelected(true);

            }
            installedAppAdapter.updateView();

        }catch (Exception e){
            e.printStackTrace();
        }

        //Listview listner
        mProfileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AppList appList = installedApps.get(position);
                int checkedStatesSize = 0;
                if(appList.isSelected){
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


        //listner
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = mProfileName.getEditText().getText().toString().trim();
                String[] list = new String[mSelectedApps.size()];
                for(int i=0 ; i<mSelectedApps.size() ; i++){
                    list[i] = mSelectedApps.get(i);
                }
                if(newName.isEmpty() ){
                    Toast.makeText(getApplicationContext() , "enter name" , Toast.LENGTH_SHORT).show();
                }else if(mSelectedApps.size() == 0){
                    Toast.makeText(getApplicationContext() , "please select apps" , Toast.LENGTH_SHORT).show();
                }else {

                    profileDTO.setpName(mProfileName.getEditText().getText().toString());
                    profileDTO.setSelectedApps(list);


                    if (mLocalDatabase.updateRecord(newName, CurrentProfileName, profileDTO , id)) {
                        Toast.makeText(CurrentProfile.this, "record Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CurrentProfile.this, ProfileMaking.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Updated failed", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
    //curent profile apps list
    String[] array ;
    public void currentProfileAppsLists(){

        Log.d("profie" , "Populating list view");

        Cursor data = mLocalDatabase.selctedApps(CurrentProfileName , id);
        while (data.moveToNext()){

                array = new String[getInstalledApps().size()];
            String data_list = data.getString(2).trim();
             array = data_list.split(",");
          //  arrayList.add(data.getString(2).trim());
        }
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , array);
        mPreviuosApps.setAdapter(listAdapter);
    }

    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }

    int size=0;
    private List<AppList> getInstalledApps() {
        List<AppList> res = new ArrayList<AppList>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        size=packs.size();
        for (int i = 0; i < size; i++) {
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
