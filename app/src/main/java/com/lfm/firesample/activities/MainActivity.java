package com.lfm.firesample.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.FirebaseError;
import com.lfm.firesample.R;
import com.lfm.firesample.firebase.FirebaseConnector;
import com.lfm.firesample.managers.MessageManager;
import com.lfm.firesample.models.Message;
import com.lfm.firesample.presenters.MessagePresenter;
import com.lfm.rvgenadapter.GenericRecyclerAdapter;

import java.util.List;

/**
 * Created by Lucas FOULON-MONGAÏ, github.com/LucasFoulonMongai  on 21/10/15.
 */
public class MainActivity extends AppCompatActivity {

    private final static String CONST_URL_FIREBASE = "https://YOUR_URL.firebaseio.com/";
    private final static String CONST_ID = "conversation1234";

    private LocalBroadcastManager localBroadcastManager;
    private MessageManager messageManager;

    private RecyclerView messageListview;
    private EditText messageEditText;
    private View sendMessageButton;

    private GenericRecyclerAdapter<Message> messageAdapter;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MessageManager.NOTIFICATION_UPDATE.equals(intent.getAction())) {
                List<Message> messageList = messageManager.getDataList();
                if (messageAdapter == null) {
                    Bundle params = new Bundle();
                    params.putString("user", Build.DEVICE);
                    messageAdapter = new GenericRecyclerAdapter<>(context, messageList, MessagePresenter.class, null, params);
                    messageListview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    messageListview.setAdapter(messageAdapter);
                } else {
                    messageAdapter.setItems(messageList);
                    messageListview.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                }
            }
        }
    };

    private FirebaseConnector firebaseConnector;
    private FirebaseConnector.OnConnectionStateListener connectionStateListener = new FirebaseConnector.OnConnectionStateListener() {
        @Override
        public void onConnectionStateChanged(boolean isConnected) {
            if (isConnected) {
                Toast.makeText(MainActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
            }
        }

        public void onConnectionError(FirebaseError firebaseError) {
            Toast.makeText(MainActivity.this, "FirebaseError:" + firebaseError, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        messageListview = (RecyclerView) findViewById(R.id.messageListview);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);
    }

    @Override
    protected void onStart() {
        super.onStart();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        firebaseConnector = FirebaseConnector.initInstance(CONST_URL_FIREBASE);
        firebaseConnector.setOnConnectionStateListener(connectionStateListener);
        messageManager = MessageManager.getInstance(this, CONST_ID);

        IntentFilter intentFilter = new IntentFilter(MessageManager.NOTIFICATION_UPDATE);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
        messageManager.observeDataLast(20);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageManager.sendData(new Message(Build.DEVICE, System.currentTimeMillis(), messageEditText.getText().toString()));
                messageEditText.getText().clear();
            }
        });
    }

    @Override
    protected void onStop() {
        if (localBroadcastManager != null) {
            localBroadcastManager.unregisterReceiver(broadcastReceiver);
        }
        if (firebaseConnector != null) {
            firebaseConnector.finish();
        }
        super.onStop();
    }
}
