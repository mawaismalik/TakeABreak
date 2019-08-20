package com.example.malix.takeabreak.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.malix.takeabreak.R;
import com.example.malix.takeabreak.domain.datalayer.MessageDTO;
import com.example.malix.takeabreak.technicalServices.database.LocalDatabase;

import java.util.ArrayList;

public class CustomMessage extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView mMessageList;
    private Button mAddMessagebtn;
    private EditText mMessageentered;
    private TextView mActiveMessage;
    private LocalDatabase mlocalDatabase;
    private MessageDTO messageDTO;
    ArrayList<String> arrayList;
    ListAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_message);


        messageDTO = new MessageDTO();
        mToolbar = findViewById(R.id.messageToolbar);
        mMessageList = findViewById(R.id.messageLis);
        mAddMessagebtn = findViewById(R.id.addbtn);
        mMessageentered = findViewById(R.id.newmessage);
        mActiveMessage =findViewById(R.id.activemessage);
        mlocalDatabase = new LocalDatabase(this);
        arrayList = new ArrayList<String>();

        //sharedprefrenes
        SharedPreferences sharedPreferences = getSharedPreferences("TakeABreak",MODE_PRIVATE);
        mActiveMessage.setText(sharedPreferences.getString("message","not selected"));

        mToolbar.setTitle("Messages");
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_keyboard_backspace_24px);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomMessage.this , SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        messageLists();
        mAddMessagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageentered.getText().toString().trim();
                messageDTO.setMessage(message);
                if(mlocalDatabase.addMessage(messageDTO)){
                    reloadData();
                    messageLists();
                    Toast.makeText(getApplicationContext(),"message added",Toast.LENGTH_SHORT ).show();
                }

            }
        });

        mMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String messageselected = mMessageList.getItemAtPosition(position).toString().trim();
                new AlertDialog.Builder(CustomMessage.this)
                        .setTitle("Messages")
                        .setMessage("Select what you want to do with this message")
                        .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mActiveMessage.setText(messageselected);
                                SharedPreferences.Editor editor = getSharedPreferences("TakeABreak", MODE_PRIVATE).edit();
                                editor.putString("message",messageselected);
                                editor.commit();
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mlocalDatabase.deleteMessage(messageselected);
                                reloadData();
                                messageLists();
                            }
                        }).create().show();
            }
        });
    }
    public void reloadData(){

        Cursor data = mlocalDatabase.getMessage();
        while (data.moveToNext()){
            arrayList.add(data.getString(1).trim());
        }
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , arrayList);
        ((ArrayAdapter) listAdapter).clear();
        ((ArrayAdapter) listAdapter).addAll(arrayList);
        ((ArrayAdapter) listAdapter).notifyDataSetChanged();
        mMessageList.setAdapter(listAdapter);
    }
    public void messageLists(){

        Log.d("profie" , "Populating list view");

        Cursor data = mlocalDatabase.getMessage();
        while (data.moveToNext()){
            arrayList.add(data.getString(1).trim());
        }
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 , arrayList);
        mMessageList.setAdapter(listAdapter);
    }
}
