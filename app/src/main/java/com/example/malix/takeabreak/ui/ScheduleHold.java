package com.example.malix.takeabreak.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.MenuPopupWindow;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.malix.takeabreak.R;
import com.example.malix.takeabreak.technicalServices.database.LocalDatabase;
import com.example.malix.takeabreak.ui.customAdapter.NotificationHandlerService;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

public class ScheduleHold extends AppCompatActivity {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Button alarmToggle;
    private TextView mAddProfile;
    private ArrayList<String> arrayList,messageList;
    private LocalDatabase mLocalDatabase;
    ListAdapter listAdapter;
    private ListView mAvailableProfilesList;
    private Spinner mHoursSpinner , mMinutesSpinner, mProfileSpinner,mMessageSpinner;
    private int mMinutes;
    private int mHours;
    private int mTime;
    private CheckBox mCallBlockCheckbox;
    public TextView mStarttimelabel,mStarttime , mEndTimelabel,mEndtime ,mNoifierlabel, mProfilelabel;
    public LinearLayout mHoldnotifier;
    private int itemID, size;
    private String profileselected, mDefaultprofile,messageSelected;
    private String[] mSelectedProfileApps , mSelectedProfileAppsPackages;
    private ImageView mBackbutton;
    //Reciever

    private final BroadcastReceiver mybroadcast = new IncomingCallReceiver();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_hold);
        alarmToggle =  findViewById(R.id.alarmbtn);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mLocalDatabase = new LocalDatabase(this);
        arrayList = new ArrayList<>();
        messageList = new ArrayList<>();
        mHoursSpinner =findViewById(R.id.hoursSpinner);
        mMinutesSpinner = findViewById(R.id.minutesSpinner);
        mProfileSpinner =findViewById(R.id.profilesSpinner);
        mStarttime = findViewById(R.id.starttime);
        mAddProfile=findViewById(R.id.addprofile);
        mMessageSpinner = findViewById(R.id.messageSpinner);
        mEndtime = findViewById(R.id.endtime);
        mHoldnotifier = findViewById(R.id.activeholdnotifier);
        mNoifierlabel= findViewById(R.id.notifierlabel);
        mStarttimelabel =findViewById(R.id.slabel);
        mEndTimelabel = findViewById(R.id.endlabel);
        mProfilelabel = findViewById(R.id.profilelabel);
        mBackbutton = findViewById(R.id.backbutton);
        mCallBlockCheckbox =findViewById(R.id.callblockcheckBox);
        mTime = 0;
        mMinutes = 0;
        mMinutes = 0;
        itemID = 0;

        profileLists();
        messageLists();

        //logic

        mBackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleHold.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }); //Onclick Listeners
        mAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleHold.this , MyProfile.class);
                startActivity(intent);
                finish();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("TakeABreak",MODE_PRIVATE);
        long ptime = sharedPreferences.getLong("ctime",System.currentTimeMillis());
        long ctime = System.currentTimeMillis();
        int  stime = sharedPreferences.getInt("time",00);
        long tdiff = ctime-ptime;
        int hours = sharedPreferences.getInt("hours",0);
        int minutes =sharedPreferences.getInt("minutes",0);
        String btnlabel=sharedPreferences.getString("buttonstate","activate");
        messageSelected = sharedPreferences.getString("msg","");
        mCallBlockCheckbox.setChecked(sharedPreferences.getBoolean("checkbox",false));
        String activeprofilename = sharedPreferences.getString("profileselected",mDefaultprofile);
        profileselected = activeprofilename;
        if(tdiff < stime){

            alarmToggle.setText(btnlabel);
            mNoifierlabel.setText("Schedule Hold Activated");
            mProfilelabel.setText(activeprofilename);
            DateFormat df = new SimpleDateFormat(" h:mm a");
            String date = df.format(Calendar.getInstance().getTime());
            mStarttime.setText(date);
            mStarttime.setVisibility(View.VISIBLE);
            mEndtime.setText(hours+" : "+minutes);
            mEndtime.setVisibility(View.VISIBLE);
            mStarttimelabel.setVisibility(View.VISIBLE);
            mEndTimelabel.setVisibility(View.VISIBLE);
            mProfilelabel.setText(profileselected);
            mProfilelabel.setVisibility(View.VISIBLE);
            mHoldnotifier.setBackgroundColor(getResources().getColor(R.color.red));
            Handler handler = new Handler();
            long handtime = stime-tdiff;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ScheduleHold.this, NotificationHandlerService.class);
                    intent.putExtra("check",0);
                    intent.putExtra("size",size);
                    startService(intent);
                    mStarttime.setVisibility(View.INVISIBLE);
                    mStarttimelabel.setVisibility(View.INVISIBLE);
                    mEndtime.setVisibility(View.INVISIBLE);
                    mEndTimelabel.setVisibility(View.INVISIBLE);
                    mProfilelabel.setVisibility(View.INVISIBLE);
                    mHoldnotifier.setBackgroundColor(getResources().getColor(R.color.primaryDark));
                    mNoifierlabel.setText("No Hold Activated");
                    alarmToggle.setText("activate");
                }
            },handtime);
        }
        else {
            mStarttime.setVisibility(View.INVISIBLE);
            mStarttimelabel.setVisibility(View.INVISIBLE);
            mEndtime.setVisibility(View.INVISIBLE);
            mEndTimelabel.setVisibility(View.INVISIBLE);
            mProfilelabel.setVisibility(View.INVISIBLE);
            mHoldnotifier.setBackgroundColor(getResources().getColor(R.color.primaryDark));
            mNoifierlabel.setText("No Hold Activated");
            alarmToggle.setText("activate");
        }


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

        mHoursSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String opt = parent.getItemAtPosition(position).toString().trim();
                mHours = Integer.parseInt(opt);
                mHours = mHours*60*60;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mMinutesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String opt = parent.getItemAtPosition(position).toString().trim();
                mMinutes = Integer.parseInt(opt);
                mMinutes = mMinutes*60;

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

        alarmToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTime = (mHours+mMinutes)*1000;
                if (alarmToggle.getText().equals("activate") && mTime>0) {

                    alarmToggle.setText("deactivate");
                    SharedPreferences.Editor sharedPreferences = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                    sharedPreferences.putString("buttonstate","deactivate");
                    sharedPreferences.putInt("time",mTime);
                    sharedPreferences.putInt("hours",mHours);
                    sharedPreferences.putInt("minutes",mMinutes);
                    sharedPreferences.putLong("ctime", System.currentTimeMillis());
                    sharedPreferences.putString("profileseleced",profileselected);
                    if(mCallBlockCheckbox.isChecked()){
                        sharedPreferences.putBoolean("checkbox",true);
                    }else {
                        sharedPreferences.putBoolean("checkbox", false);
                    }
                        sharedPreferences.commit();

                    //geting packages for crrent profile

                    mSelectedProfileApps = new String[20];
                    mSelectedProfileApps = currentProfileAppsLists();
                    mSelectedProfileAppsPackages = new String[size];
                    for (int i = 0; i < mSelectedProfileApps.length; i++) {
                        mSelectedProfileAppsPackages[i] = getPackNameByAppName(mSelectedProfileApps[i]);
                    }

                    //Activating hold
                    Intent intent1 = new Intent(ScheduleHold.this, NotificationHandlerService.class);
                    intent1.putExtra("packs", mSelectedProfileAppsPackages);
                    intent1.putExtra("check",1);
                    intent1.putExtra("size",size);
                    startService(intent1);
                    //activating callblock
                    if(mCallBlockCheckbox.isChecked()){
                        IntentFilter filter = new IntentFilter();
                        filter.addAction("android.intent.action.PHONE_STATE");
                        filter.addAction("SOME_ACTION");
                        registerReceiver(mybroadcast, filter);
                        Toast.makeText(getApplicationContext(),"Blocked",Toast.LENGTH_SHORT).show();
                        Intent msg = new Intent("SOME_ACTION");
                        if(!messageSelected.isEmpty()) {
                            msg.putExtra("msg", messageSelected);
                            sendBroadcast(msg);
                            // Toast.makeText(getApplicationContext(),"msg sent",Toast.LENGTH_SHORT).show();
                        }
                    }


                    /*final Intent intent = new Intent(ScheduleHold.this, AlarmReciever.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis()+ mTime , pendingIntent);*/
                    mNoifierlabel.setText("Schedule Hold Activated");
                    DateFormat df = new SimpleDateFormat(" h:mm a");
                    String date = df.format(Calendar.getInstance().getTime());
                    mStarttime.setText(date);
                    mStarttime.setVisibility(View.VISIBLE);
                    mEndtime.setText(mHours/(60*60) +" : "+ mMinutes/(60));
                    mEndtime.setVisibility(View.VISIBLE);
                    mStarttimelabel.setVisibility(View.VISIBLE);
                    mEndTimelabel.setVisibility(View.VISIBLE);
                    mHoldnotifier.setBackgroundColor(getResources().getColor(R.color.red));
                    mProfilelabel.setText(profileselected);
                    mProfilelabel.setVisibility(View.VISIBLE);

                    //end date

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(ScheduleHold.this, NotificationHandlerService.class);
                            intent.putExtra("check",0);
                            intent.putExtra("size",size);
                            startService(intent);
                            if(mCallBlockCheckbox.isChecked()){

                                unregisterReceiver(mybroadcast);
                                Toast.makeText(getApplicationContext(),"Deactivated",Toast.LENGTH_SHORT).show();
                            }
                            mCallBlockCheckbox.setChecked(false);
                            mStarttime.setVisibility(View.INVISIBLE);
                            mStarttimelabel.setVisibility(View.INVISIBLE);
                            mEndtime.setVisibility(View.INVISIBLE);
                            mEndTimelabel.setVisibility(View.INVISIBLE);
                            mHoldnotifier.setBackgroundColor(getResources().getColor(R.color.primaryDark));
                            mNoifierlabel.setText("No Hold Activated");
                            mProfilelabel.setText(profileselected);
                            mProfilelabel.setVisibility(View.INVISIBLE);
                            alarmToggle.setText("activate");

                        }
                    },mTime);
                } else {
                        alarmToggle.setText("activate");
                        Intent intent = new Intent(ScheduleHold.this, NotificationHandlerService.class);
                        intent.putExtra("check",0);
                        intent.putExtra("size",size);
                        startService(intent);
                         if(mCallBlockCheckbox.isChecked() && alarmToggle.getText().equals("deactivate")){
                             unregisterReceiver(mybroadcast);
                             Toast.makeText(getApplicationContext(),"Deactivated",Toast.LENGTH_SHORT).show();
                          }
                        SharedPreferences.Editor sharedPreferences = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                        sharedPreferences.putInt("time",0);
                        sharedPreferences.commit();
                        mStarttime.setVisibility(View.INVISIBLE);
                        mStarttimelabel.setVisibility(View.INVISIBLE);
                        mEndtime.setVisibility(View.INVISIBLE);
                        mEndTimelabel.setVisibility(View.INVISIBLE);
                        mHoldnotifier.setBackgroundColor(getResources().getColor(R.color.primaryDark));
                        mNoifierlabel.setText("No Hold Activated");
                        mProfilelabel.setText(profileselected);
                        mProfilelabel.setVisibility(View.INVISIBLE);
                        alarmToggle.setText("activate");
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
        if(arrayList.size() !=  0) {
            mDefaultprofile = arrayList.get(0);
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
    //curent profile apps list
    String[] array ;
    public String[] currentProfileAppsLists(){

        Log.d("profie" , "Populating list view");
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        size=packs.size();

        Cursor data = mLocalDatabase.selctedApps(profileselected , itemID);
        while (data.moveToNext()){

            array = new String[size];
            String data_list = data.getString(2).trim();
            array = data_list.split(",");
            //  arrayList.add(data.getString(2).trim());
        }
        return array;
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
    }  @Override
    protected void onPause() {
        if(mCallBlockCheckbox.isChecked() && alarmToggle.getText().equals("deactivate")){

            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.PHONE_STATE");
            filter.addAction("SOME_ACTION");
            registerReceiver(mybroadcast, filter);
            Intent msg = new Intent("SOME_ACTION");
            if(!messageSelected.isEmpty()) {
                msg.putExtra("msg", messageSelected);
                sendBroadcast(msg);
                // Toast.makeText(getApplicationContext(),"msg sent",Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(),"Blocked",Toast.LENGTH_SHORT).show();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(mCallBlockCheckbox.isChecked() && alarmToggle.getText().equals("deactivate")){

            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.PHONE_STATE");
            filter.addAction("SOME_ACTION");
            registerReceiver(mybroadcast, filter);
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
}
