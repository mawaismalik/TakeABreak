package com.example.malix.takeabreak.domain.datalayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malix.takeabreak.R;
import com.example.malix.takeabreak.technicalServices.database.LocalDatabase;
import com.example.malix.takeabreak.ui.MainActivity;
import com.example.malix.takeabreak.ui.Personal_hold_Activity;
import com.example.malix.takeabreak.ui.customAdapter.NotificationHandlerService;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.android.gms.nearby.connection.Strategy.P2P_CLUSTER;
import static java.nio.charset.StandardCharsets.UTF_8;


public class ConnectivityActivity extends AppCompatActivity  {



    /* variable declaration */

    //TAG
    private static final String TAG = "TakeABreak";
    //Buttons
    private Button mSendPayloadButton;

    int index, numberofdevices;
    private LocalDatabase mLocalDatabase;
    private String profileselected, mDefaultprofile;
    private ArrayList<String> arrayList;
    private Spinner  mProfileSpinner;
    //toogle button
    private Switch mHotspotSwitch , mJoinSwitch;
    // ToolBar

    private Toolbar mHotspotToolbar;
    //strings
    private String mserviceId;
    private String  mendpointId;
    private String otherEndpointId;
    private String mMessage;
    private String mRecieve;
    private TextView mDevicesStrength;
    private CardView devicesCardView;
    private CardView profileCardView;
    private String[] mSelectedProfileApps , mSelectedProfileAppsPackages;
    //Layouts
    private LinearLayout mListlayout;
    //progressbar
    private ProgressBar mPointbar;
    //strategy
    private Strategy mClusterStrategy = P2P_CLUSTER;
    //Permissions

    private  int  PERMISSION_REQUEST_CODE ;
    private int size,itemID , payloadCheck;
    //ListView
    ListView mnearbyDevicesList ;
    //textview
   private TextView mdevicesHeading;
   private TextView mwifiLabel;
    //arrayList


    List<String> endpointIds;
    ArrayList<String> devices ;
    ArrayAdapter<String> mArrayAdapter;

    // Byte payload
    Payload bytesPayloadOff ;

    private  PayloadCallback mPayloadCallback;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectivity);


        /* Variables initialization */
        mLocalDatabase = new LocalDatabase(this);
        arrayList = new ArrayList<>();
        mProfileSpinner =findViewById(R.id.profilesSpinner);
        itemID = 0;
        payloadCheck = 0;
        profileCardView = findViewById(R.id.profilecard);
        devicesCardView =findViewById(R.id.devicescard);
        mSendPayloadButton = findViewById(R.id.sendPayloadBtn);
        mdevicesHeading = findViewById(R.id.havailableDevices);
        mListlayout = findViewById(R.id.listLayout);
        mwifiLabel = findViewById(R.id.wifiLabel);
        mDevicesStrength = findViewById(R.id.connecteddevicesnumber);
        mserviceId = "@string/serviceId";
        mendpointId = "";
        otherEndpointId = "";
        mRecieve = "";
        mPointbar = findViewById(R.id.endPointFoundbar);
       // mMessage = mMessageBox.getEditText().getText().toString().trim();
        mMessage = "deactivate";
        bytesPayloadOff = Payload.fromBytes(mMessage.getBytes(UTF_8));



        //Toogle Button

        mHotspotSwitch = findViewById(R.id.hotspotSwitch);
        mJoinSwitch = findViewById(R.id.searchSwitch);
        profileLists();
        //toolbar
        mHotspotToolbar = findViewById(R.id.hotspotToolbar);
        mHotspotToolbar.setNavigationIcon(R.drawable.ic_baseline_keyboard_backspace_24px);
        mHotspotToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectivityActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //switch state
        final SharedPreferences sharedPreferences = getSharedPreferences("TakeABreak",MODE_PRIVATE);
        mHotspotSwitch.setChecked(sharedPreferences.getBoolean("state",false));
        mJoinSwitch.setChecked(sharedPreferences.getBoolean("jstate",false));
        String btnlabel=sharedPreferences.getString("buttonstate","activate");
        mSendPayloadButton.setText(btnlabel);
        String activeprofilename = sharedPreferences.getString("profileselected",mDefaultprofile);
        profileselected = activeprofilename;
        //index
        if(sharedPreferences.getInt("index",0) != 0) {
            index = sharedPreferences.getInt("index",0);
        }else {
            index = 0;
        }

        //visibilites of views
        if(sharedPreferences.getBoolean("state",false)){
            mPointbar.setVisibility(View.VISIBLE );
            mdevicesHeading.setVisibility(View.VISIBLE);
            mListlayout.setVisibility(View.VISIBLE);
            mwifiLabel.setVisibility(View.GONE);
            profileCardView.setVisibility(View.VISIBLE );
            devicesCardView.setVisibility(View.VISIBLE);
            mSendPayloadButton.setVisibility(View.VISIBLE);
            mSendPayloadButton.setEnabled(true);
            mwifiLabel.setVisibility(View.GONE);
            mJoinSwitch.setVisibility(View.GONE);
        }else {

            mdevicesHeading.setVisibility(View.GONE);
            mPointbar.setVisibility(View.GONE);
            mdevicesHeading.setVisibility(View.GONE);
            mListlayout.setVisibility(View.GONE);
            mwifiLabel.setVisibility(View.VISIBLE);
            profileCardView.setVisibility(View.GONE );
            devicesCardView.setVisibility(View.GONE);
            mSendPayloadButton.setVisibility(View.GONE);
            mwifiLabel.setVisibility(View.VISIBLE);
            mJoinSwitch.setVisibility(View.VISIBLE);
        }


        if(sharedPreferences.getBoolean("jstate",false)){
            mPointbar.setVisibility(View.VISIBLE );
            mdevicesHeading.setVisibility(View.VISIBLE);
            mListlayout.setVisibility(View.VISIBLE);
            mwifiLabel.setVisibility(View.GONE);
            mHotspotSwitch.setVisibility(View.GONE);
        }else{
            mdevicesHeading.setVisibility(View.GONE);
            mPointbar.setVisibility(View.GONE);
            mListlayout.setVisibility(View.GONE);
            mwifiLabel.setVisibility(View.VISIBLE);
            mHotspotSwitch.setVisibility(View.VISIBLE);
        }

        mHotspotSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);
                if (isChecked) {
                   // mHotspotSwitch.setText("On");
                    SharedPreferences.Editor editor = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                    editor.putBoolean("state",true);
                    editor.putString("key","On");
                    editor.commit();
                    mSendPayloadButton.setEnabled(true);
                    if(ContextCompat.checkSelfPermission(ConnectivityActivity.this , Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                    {

                        startAdvertising();
                        endpointIds = new ArrayList<>();
                        Toast.makeText(getApplicationContext(),"list "+endpointIds,Toast.LENGTH_SHORT);
                        profileCardView.setVisibility(View.VISIBLE );
                        devicesCardView.setVisibility(View.VISIBLE);
                        mSendPayloadButton.setVisibility(View.VISIBLE);
                        mwifiLabel.setVisibility(View.GONE);
                        mJoinSwitch.setVisibility(View.GONE);
                        mSendPayloadButton.setText("activate");

                    }else{
                        requestCoarseLocationPermission();
                    }
                } else {
                    //do something when unchecked
                  //  mHotspotSwitch.setText("Off");
                    SharedPreferences.Editor editor = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                    editor.putBoolean("state",false);
                    editor.putString("buttonstate","activate");
                    editor.putString("key","Off");
                    editor.commit();
                  //  stopDiscovery();
                    stopAdvertising();
                    profileCardView.setVisibility(View.GONE );
                    devicesCardView.setVisibility(View.GONE);
                    mSendPayloadButton.setVisibility(View.GONE);
                    mwifiLabel.setVisibility(View.VISIBLE);
                    mJoinSwitch.setVisibility(View.VISIBLE);
                    mSendPayloadButton.setText("deactivate");
                    //disconect from all end points
                    Nearby.getConnectionsClient(getApplicationContext()).stopAllEndpoints();
                }
            }

        });

        mJoinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);
                if (isChecked) {
                    // mHotspotSwitch.setText("On");
                    SharedPreferences.Editor editor = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                    editor.putBoolean("jstate",true);
                    editor.commit();
                    if(ContextCompat.checkSelfPermission(ConnectivityActivity.this , Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                    {

                       startDiscovery();
                        mPointbar.setVisibility(View.VISIBLE );
                        mdevicesHeading.setVisibility(View.VISIBLE);
                        mListlayout.setVisibility(View.VISIBLE);
                        mwifiLabel.setVisibility(View.GONE);
                        mHotspotSwitch.setVisibility(View.GONE);
                    }else{
                        requestCoarseLocationPermission();
                    }
                } else {
                    //do something when unchecked
                    //  mHotspotSwitch.setText("Off");
                    SharedPreferences.Editor editor = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                    editor.putBoolean("jstate",false);
                    editor.commit();
                      stopDiscovery();
                    devices.clear();
                    mdevicesHeading.setVisibility(View.GONE);
                    mPointbar.setVisibility(View.GONE);
                    mdevicesHeading.setVisibility(View.GONE);
                    mListlayout.setVisibility(View.GONE);
                    mwifiLabel.setVisibility(View.VISIBLE);
                    mHotspotSwitch.setVisibility(View.VISIBLE);
                    //disconect from all end points
                   // Nearby.getConnectionsClient(getApplicationContext()).stopAllEndpoints();
                }
            }

        });



        //list listner

        //Toolbar
        mHotspotToolbar = findViewById(R.id.hotspotToolbar);
        setSupportActionBar(mHotspotToolbar);
        getSupportActionBar().setTitle("Bluetooth");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        PERMISSION_REQUEST_CODE = 1;
        // ListView
        mnearbyDevicesList = findViewById(R.id.nearbyDevices);
        devices = new ArrayList<String>();
        mArrayAdapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1 , devices);
        mnearbyDevicesList.setAdapter(mArrayAdapter);


        // byte paload send

        mSendPayloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSendPayloadButton.getText().equals("activate")) {
                    mSendPayloadButton.setText("deactivate");
                    SharedPreferences.Editor sharedPreferences = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                    sharedPreferences.putString("buttonstate","deactivate");
                    sharedPreferences.putString("profileseleced",profileselected);
                    sharedPreferences.commit();

                    //geting packages for crrent profile

                    mSelectedProfileApps = new String[20];
                    mSelectedProfileApps = currentProfileAppsLists();
                    mSelectedProfileAppsPackages = new String[size];
                    for (int i = 0; i < mSelectedProfileApps.length; i++) {
                        mSelectedProfileAppsPackages[i] = getPackNameByAppName(mSelectedProfileApps[i]);
                    }

                   // Toast.makeText(getApplicationContext()," "+mSelectedProfileAppsPackages[0],Toast.LENGTH_LONG).show();

                    //converting package name array to byte payload
                    // buffer which grows as needed.
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    // supports basic data types
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(mSelectedProfileAppsPackages);
                        oos.flush();
                        oos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    byte[] bytes = baos.toByteArray();
                    Payload bytesPayload = Payload.fromBytes(bytes);
                    Nearby.getConnectionsClient(getApplicationContext()).sendPayload(endpointIds,bytesPayload);
                    Intent intent = new Intent(ConnectivityActivity.this, NotificationHandlerService.class);
                    intent.putExtra("packs", mSelectedProfileAppsPackages);
                    intent.putExtra("check",1);
                    intent.putExtra("size",size);
                    startService(intent);
                  //  Toast.makeText(getApplicationContext(),"Activated",Toast.LENGTH_SHORT).show();
                }
                else if(mSendPayloadButton.getText().equals("deactivate")){
                    mSendPayloadButton.setText("activate");
                    SharedPreferences.Editor sharedPreferences = getSharedPreferences("TakeABreak",MODE_PRIVATE).edit();
                    sharedPreferences.putString("buttonstate","activate");
                    sharedPreferences.commit();
                    Nearby.getConnectionsClient(getApplicationContext()).sendPayload(endpointIds,bytesPayloadOff);
                    Intent intent = new Intent(ConnectivityActivity.this, NotificationHandlerService.class);
                    intent.putExtra("check",0);
                    intent.putExtra("size",size);
                    startService(intent);
                  //  Toast.makeText(getApplicationContext(),"Deactivated",Toast.LENGTH_SHORT).show();
                }

            }
        });

        mPayloadCallback = new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                size = 20;
                String recievedData;
                byte[] receivedBytes = payload.asBytes();
                try {
                     recievedData = new String(receivedBytes, "UTF-8");

                if ( recievedData.equals("deactivate")) {
                    Intent intent = new Intent(ConnectivityActivity.this, NotificationHandlerService.class);
                    intent.putExtra("check",0);
                    intent.putExtra("size",size);
                    startService(intent);

                } else {
                    String[] packageNames = new String[receivedBytes.length];
                    try {

                        Object object = deserialize(receivedBytes);
                        String[] appList = new String[size];
                        appList = (String[]) object;
                        Intent intent = new Intent(ConnectivityActivity.this, NotificationHandlerService.class);
                        intent.putExtra("packs", appList);
                        intent.putExtra("check", 1);
                        intent.putExtra("size", size);
                        startService(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }


                }  } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
                if (payloadTransferUpdate.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {

                    payloadCheck = 1;
                }
                else if(payloadTransferUpdate.getStatus() == PayloadTransferUpdate.Status.FAILURE){
                    payloadCheck = 0;
                }
            }
        };
      //  Nearby.getConnectionsClient(context).sendPayload(toEndpointId, bytesPayload);



        /* listeners */

        mnearbyDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mendpointId = mnearbyDevicesList.getItemAtPosition(position).toString().trim();
                new AlertDialog.Builder(ConnectivityActivity.this)
                        .setTitle("Connection")
                        .setMessage("Do you want to connect with this device...")
                        .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Nearby.getConnectionsClient(getApplicationContext())
                                        .requestConnection("TakeABreak", mendpointId, connectionLifecycleCallback);

                                Toast.makeText(getApplicationContext(),"connecting...",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();


            }
        });

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

    }

    //Location Method

    private void requestCoarseLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.ACCESS_COARSE_LOCATION)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This Permisssion is needed to locate nearby devices")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ConnectivityActivity.this , new String[]{Manifest.permission.ACCESS_COARSE_LOCATION} , PERMISSION_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }else{
            ActivityCompat.requestPermissions(this , new String[] {Manifest.permission.ACCESS_COARSE_LOCATION} , PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                startDiscovery();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }




    /* Methods */

    private void startAdvertising() {
        endpointIds = new ArrayList<String>();
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(mClusterStrategy).build();
        Nearby.getConnectionsClient(this)
                .startAdvertising(
                        "TakeABreak", mserviceId, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // We're advertising!
                        Toast.makeText(getApplicationContext(), "We're advertising! " , Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // We were unable to start advertising.

                        Toast.makeText(getApplicationContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void stopAdvertising() {
        Nearby.getConnectionsClient(getApplicationContext()).stopAdvertising();

        Toast.makeText(getApplicationContext(), "Advertising Stopped " , Toast.LENGTH_SHORT).show();
    }
    private void startDiscovery() {
        endpointIds = new ArrayList<String>();
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(mClusterStrategy).build();
        Nearby.getConnectionsClient(this)
                .startDiscovery(mserviceId, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "We're discovering! " , Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(), e.getMessage() , Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void stopDiscovery() {
        Nearby.getConnectionsClient(getApplicationContext()).stopDiscovery();
        Toast.makeText(getApplicationContext(), "Discovery Stopped " , Toast.LENGTH_SHORT).show();
    }

    // call backs

    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    // An endpoint was found. We request a connection to it.
                       Log.i(TAG , "Endpoint found");
                       mPointbar.setVisibility(View.INVISIBLE);
                       devices.add(endpointId);
                       mArrayAdapter.notifyDataSetChanged();

                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                    Log.i(TAG, "Endpoint lost");
                      int index =  devices.indexOf(endpointId);
                        devices.remove(index);
                        mArrayAdapter.notifyDataSetChanged();
                }
            };

    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    // Automatically accept the connection on both sides.
                    mendpointId=endpointId;
                    Log.i(TAG, "Accepting connection");

                                    Nearby.getConnectionsClient(getApplicationContext())
                                            .acceptConnection(mendpointId, mPayloadCallback);

                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    switch (result.getStatus().getStatusCode()) {
                        case ConnectionsStatusCodes.STATUS_OK:
                            // We're connected! Can now start sending and receiving data.
                            Toast.makeText(getApplicationContext(),"We're connected with "+endpointId,Toast.LENGTH_SHORT).show();

                            mSendPayloadButton.setEnabled(true);
                            otherEndpointId = endpointId;
                            endpointIds.add(endpointId);
                            numberofdevices++;


                            break;
                        case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                            // The connection was rejected by one or both sides.
                            Toast.makeText(getApplicationContext(),"The connection was rejected by one or both sides."+endpointId,Toast.LENGTH_SHORT).show();

                            break;
                        case ConnectionsStatusCodes.STATUS_ERROR:
                            // The connection broke before it was able to be accepted.
                            Toast.makeText(getApplicationContext(),"The connection broke before it was able to be accepted."+endpointId,Toast.LENGTH_SHORT).show();

                            break;
                        default:
                            // Unknown status code
                            Toast.makeText(getApplicationContext(),"Unknown status code"+endpointId,Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    // We've been disconnected from this endpoint. No more data can be
                    // sent or received.

                    Log.i(TAG, "Disconnected from "+endpointId);
                    Toast.makeText(getApplicationContext(),"Disconnected from "+endpointId,Toast.LENGTH_SHORT).show();
                    endpointIds.remove(endpointIds.indexOf(endpointId));
                    numberofdevices--;

                }
            };

    // for authentication


    static class ReceiveBytesPayloadListener extends PayloadCallback {

        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            // This always gets the full data of the payload. Will be null if it's not a BYTES
            // payload. You can check the payload type with payload.getType().
            byte[] receivedBytes = payload.asBytes();
        }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
        }
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
    }   //curent profile apps list
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
    }
    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException{
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return objectInputStream.readObject();
    }
}
