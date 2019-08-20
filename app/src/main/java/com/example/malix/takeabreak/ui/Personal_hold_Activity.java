package com.example.malix.takeabreak.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malix.takeabreak.R;
import com.example.malix.takeabreak.technicalServices.database.LocalDatabase;
import com.example.malix.takeabreak.ui.customAdapter.NotificationHandlerService;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.datatype.Duration;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class Personal_hold_Activity extends AppCompatActivity {

    //notificaion
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private AlertDialog enableNotificationListenerAlertDialog;

    //Reciever

    BroadcastReceiver sendBroadcastReceiver;
    BroadcastReceiver deliveryBroadcastReceiver;
    PendingIntent sentPI;

    private final BroadcastReceiver mybroadcast = new IncomingCallReceiver();
    //variables
    private CheckBox mCallBlockCheckbox;
    private TextView mAddProfile;
    private ArrayList<String> arrayList , messageList;
    private LocalDatabase mLocalDatabase;
    private String mClickedProfileName;
    private Button mActivteButton;
    private String[] mSelectedProfileApps;
    private String[] mSelectedProfileAppsPackages;
    private Spinner  mProfileSpinner, mMessageSpinner;
    private String profileselected, mDefaultprofile , messageSelected;
    String SENT ;
    private ImageView mBackbutton;
    int size = -1;
    int itemID;
    ListAdapter listAdapter;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_hold_);

        mAddProfile=findViewById(R.id.addprofile);
        mBackbutton = findViewById(R.id.backbutton);
        mActivteButton =findViewById(R.id.activatebutton);
        arrayList = new ArrayList<>();
        messageList = new ArrayList<>();
        mClickedProfileName ="";
        mProfileSpinner =findViewById(R.id.profilesSpinner);
        mLocalDatabase = new LocalDatabase(this);
        mMessageSpinner = findViewById(R.id.messageSpinner);
        mCallBlockCheckbox = findViewById(R.id.callblockcheckBox);
        SENT = "SMS_SENT";
        profileLists();
        messageLists();

        //sending broadcast




        //sharedprefrence
        final SharedPreferences sharedPreferences = getSharedPreferences("TakeABreak",MODE_PRIVATE);
        mClickedProfileName = sharedPreferences.getString("Selected_profile_name","Select Profile");
        itemID = sharedPreferences.getInt("item_ID", -1);
        mActivteButton.setText(sharedPreferences.getString("btnvalue","activate"));
        messageSelected = sharedPreferences.getString("msg","");
        mCallBlockCheckbox.setChecked(sharedPreferences.getBoolean("checkbox",false));
        String activeprofilename = sharedPreferences.getString("profileselected",mDefaultprofile);
        profileselected = activeprofilename;

        //toolbar settings
        mBackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Personal_hold_Activity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Onclick Listeners
        mAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Personal_hold_Activity.this , MyProfile.class);
                startActivity(intent);
                finish();
            }
        });


        //permission
        //Listners

        mProfileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                profileselected = parent.getItemAtPosition(position).toString();
                Cursor data = mLocalDatabase.selectItemId(profileselected); //get the id associated with that name
                itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                SharedPreferences.Editor sharedPreferences = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                sharedPreferences.putString("profileselected","deactivate");
                sharedPreferences.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mMessageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                messageSelected = parent.getItemAtPosition(position).toString().trim();
                SharedPreferences.Editor editor = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                editor.putString("msg",messageSelected);
                editor.commit();
               Toast.makeText(getApplicationContext(),messageSelected,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

         //Buttonstate

            mActivteButton.setEnabled(true);


        if(!isNotificationServiceEnabled()) {
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }
         //Listners
        mProfileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mClickedProfileName = mProfileSpinner.getItemAtPosition(position).toString().trim();

                Cursor data = mLocalDatabase.selectItemId(mClickedProfileName); //get the id associated with that name
                itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }

                                if(itemID > -1){
                                    SharedPreferences.Editor editor = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                                    editor.putString("Selected_profile_name", mClickedProfileName);
                                    editor.putInt("item_ID", itemID);
                                    editor.commit();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"No ID associated with that name",Toast.LENGTH_SHORT).show();
                                }
                                //mlocalDatabase.deleteProfile(mClickedProfileName);
                                Toast.makeText(getApplicationContext() , "Profile Selected" , Toast.LENGTH_SHORT).show();
                              //  profileLists();
                            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mActivteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    mSelectedProfileApps = new String[20];
                    mSelectedProfileApps = currentProfileAppsLists();
                    mSelectedProfileAppsPackages = new String[size];
                    for (int i = 0; i < mSelectedProfileApps.length; i++) {
                        mSelectedProfileAppsPackages[i] = getPackNameByAppName(mSelectedProfileApps[i]);
                    }

                    if(mActivteButton.getText().equals("activate")) {
                        Intent intent = new Intent(Personal_hold_Activity.this, NotificationHandlerService.class);
                        intent.putExtra("packs", mSelectedProfileAppsPackages);
                        intent.putExtra("check",1);
                        intent.putExtra("size",size);
                        startService(intent);
                        if(mCallBlockCheckbox.isChecked()){

                            IntentFilter filter = new IntentFilter();
                            filter.addAction("android.intent.action.PHONE_STATE");
                            filter.addAction("SOME_ACTION");
                            registerReceiver(mybroadcast, filter);
                            sendBroadcasMethod();
                            Toast.makeText(getApplicationContext(),"Blocked",Toast.LENGTH_SHORT).show();
                        }
                        SharedPreferences.Editor sharedPreferences = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                        sharedPreferences.putString("btnvalue","deactivate");
                        if(mCallBlockCheckbox.isChecked()) {
                            sharedPreferences.putBoolean("checkbox", true);
                        }else{
                            sharedPreferences.putBoolean("checkbox", false);
                        }
                        sharedPreferences.commit();
                        mActivteButton.setText("deactivate");

                        Intent msg = new Intent("SOME_ACTION");
                        if(!messageSelected.isEmpty()) {
                            msg.putExtra("msg", messageSelected);
                            sendBroadcast(msg);
                           // Toast.makeText(getApplicationContext(),"msg sent",Toast.LENGTH_SHORT).show();
                        }
                       // Toast.makeText(getApplicationContext(), "activated", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        Intent intent = new Intent(Personal_hold_Activity.this, NotificationHandlerService.class);
                        intent.putExtra("check",0);
                        intent.putExtra("size",size);
                        startService(intent);
                        if(mCallBlockCheckbox.isChecked()){
                            unregisterReceiver(mybroadcast);
                            if (sentPI != null) {
                                unregisterReceiver(sendBroadcastReceiver);
                                sentPI = null;
                            }
                            Toast.makeText(getApplicationContext(),"Unblocked",Toast.LENGTH_SHORT).show();
                        }
                        SharedPreferences.Editor sharedPreferences = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                        sharedPreferences.putString("btnvalue","activate");
                        mCallBlockCheckbox.setChecked(false);
                        sharedPreferences.commit();
                        mActivteButton.setText("activate");
                        mCallBlockCheckbox.setChecked(false);
                       // Toast.makeText(getApplicationContext(), "deactivated", Toast.LENGTH_SHORT).show();
                    }

            }
        });
    }
    public void profileLists(){

        Log.d("profie" , "Populating list view");

        Cursor data = mLocalDatabase.getData();
        while (data.moveToNext()){
            arrayList.add(data.getString(1).trim());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        arrayList); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        mProfileSpinner.setAdapter(spinnerArrayAdapter);

    }

    //message list
    public void messageLists(){

        Log.d("profie" , "Populating list view");

        Cursor data = mLocalDatabase.getMessage();
        while (data.moveToNext()){
            messageList.add(data.getString(1).trim());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        messageList); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        mMessageSpinner.setAdapter(spinnerArrayAdapter);

    }

    public String getPackNameByAppName(String name) {
        PackageManager pm = this.getPackageManager();
        List<ApplicationInfo> l = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        String packName = "";
        for (ApplicationInfo ai : l) {
            String n = (String)pm.getApplicationLabel(ai);
            if (n.contains(name) || name.contains(n)){
                packName = ai.packageName;
            }
        }

        return packName;
    }

    //curent profile apps list
    String[] array ;
    public String[] currentProfileAppsLists(){

        Log.d("profie" , "Populating list view");
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        size=packs.size();

        Cursor data = mLocalDatabase.selctedApps(mClickedProfileName , itemID);
        while (data.moveToNext()){

            array = new String[size];
            String data_list = data.getString(2).trim();
            array = data_list.split(",");
            //  arrayList.add(data.getString(2).trim());
        }
       return array;
    }

    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Notification Service");
        alertDialogBuilder.setMessage("Application need this service to function properly");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton("no",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
    }


    @Override
    protected void onPause() {
        if(mCallBlockCheckbox.isChecked() && mActivteButton.getText().equals("deactivate")){

            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.PHONE_STATE");
            filter.addAction("SOME_ACTION");
            registerReceiver(mybroadcast, filter);
            sendBroadcasMethod();
            Intent msg = new Intent("SOME_ACTION");
            if(!messageSelected.isEmpty()) {
                msg.putExtra("msg", messageSelected);
                sendBroadcast(msg);
                //Toast.makeText(getApplicationContext(),"msg sent",Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(),"Blocked",Toast.LENGTH_SHORT).show();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(mCallBlockCheckbox.isChecked() && mActivteButton.getText().equals("deactivate")){

            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.PHONE_STATE");
            filter.addAction("SOME_ACTION");
            registerReceiver(mybroadcast, filter);
            sendBroadcasMethod();
            Intent msg = new Intent("SOME_ACTION");
            if(!messageSelected.isEmpty()) {
                msg.putExtra("msg", messageSelected);
                sendBroadcast(msg);
               // Toast.makeText(getApplicationContext(),"msg sent",Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(),"Blocked",Toast.LENGTH_SHORT).show();
        }
        super.onResume();
    }

    public void sendBroadcasMethod(){
        Intent intent = new Intent();
        intent.putExtra("message","hi my message");
        sendBroadcast(intent);
    }

}
