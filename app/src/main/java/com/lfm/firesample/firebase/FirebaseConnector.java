package com.lfm.firesample.firebase;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by Lucas FOULON-MONGAÏ, github.com/LucasFoulonMongai  on 21/10/15.
 */
public class FirebaseConnector implements ValueEventListener {
    private static FirebaseConnector instance;
    private Firebase mFirebaseRef;
    private OnConnectionStateListener onConnectionStateListener;

    private FirebaseConnector() {
    }

    private FirebaseConnector(String firebaseUrl) {
        this.mFirebaseRef = new Firebase(firebaseUrl);
        this.mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(this);
    }

    public Firebase getFirebase() {
        return mFirebaseRef;
    }

    public static FirebaseConnector initInstance(String firebaseUrl) {
        if (instance == null) {
            instance = new FirebaseConnector(firebaseUrl);
        }
        return instance;
    }

    public static FirebaseConnector getInstance() {
        return instance;
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (onConnectionStateListener != null) {
            boolean connected = (Boolean) dataSnapshot.getValue();
            onConnectionStateListener.onConnectionStateChanged(connected);
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        onConnectionStateListener.onConnectionError(firebaseError);
    }

    public void finish() {
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(this);
    }

    public void setOnConnectionStateListener(OnConnectionStateListener onConnectionStateListener) {
        this.onConnectionStateListener = onConnectionStateListener;
    }

    public interface OnConnectionStateListener {

        void onConnectionStateChanged(boolean isConnected);

        void onConnectionError(FirebaseError firebaseError);
    }
}
