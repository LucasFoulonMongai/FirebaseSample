package com.lfm.firesample.firebase;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas FOULON-MONGAÏ, github.com/LucasFoulonMongai  on 21/10/15.
 */
public abstract class FirebaseManager<T> implements ChildEventListener {

    public enum ChangeType {
        ADDED,
        CHANGED,
        MOVED,
        REMOVED
    }

    private Class<T> genericType;
    private List<T> dataList;
    private List<String> keysList;
    private Firebase firebase;
    private OnErrorListener onErrorListener;

    protected FirebaseManager(String... childs) {
        this.genericType = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        System.out.println("--->" + genericType);
        this.dataList = new ArrayList<>();
        this.keysList = new ArrayList<>();

        FirebaseConnector firebaseConnector = FirebaseConnector.getInstance();

        firebase = firebaseConnector.getFirebase();

        for (String child : childs) {
            firebase = firebase.child(child);
        }

    }

    public void sendData(T data) {
        firebase.push().setValue(data);
    }

    public Query observeDataFirst(int limit) {
        Query query = firebase.limitToFirst(limit);
        query.removeEventListener(this);
        query.addChildEventListener(this);
        return query;
    }

    public Query observeDataLast(int limit) {
        Query query = firebase.limitToLast(limit);
        query.removeEventListener(this);
        query.addChildEventListener(this);
        return query;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {
        T model = dataSnapshot.getValue(genericType);
        String key = dataSnapshot.getKey();

        // Insert into the correct location, based on previousChildName
        if (previousChildKey == null) {
            dataList.add(0, model);
            keysList.add(0, key);
        } else {
            int previousIndex = keysList.indexOf(previousChildKey);
            int nextIndex = previousIndex + 1;
            if (nextIndex == dataList.size()) {
                dataList.add(model);
                keysList.add(key);
            } else {
                dataList.add(nextIndex, model);
                keysList.add(nextIndex, key);
            }
        }

        notifyChange(ChangeType.ADDED);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {
        String key = dataSnapshot.getKey();
        T itemChanged = dataSnapshot.getValue(genericType);
        int index = keysList.indexOf(key);
        dataList.set(index, itemChanged);
        notifyChange(ChangeType.CHANGED);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        int index = keysList.indexOf(key);
        keysList.remove(index);
        dataList.remove(index);

        notifyChange(ChangeType.REMOVED);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildKey) {
        String key = dataSnapshot.getKey();
        T newModel = dataSnapshot.getValue(genericType);
        int index = keysList.indexOf(key);
        dataList.remove(index);
        keysList.remove(index);
        if (previousChildKey == null) {
            dataList.add(0, newModel);
            keysList.add(0, key);
        } else {
            int previousIndex = keysList.indexOf(previousChildKey);
            int nextIndex = previousIndex + 1;
            if (nextIndex == dataList.size()) {
                dataList.add(newModel);
                keysList.add(key);
            } else {
                dataList.add(nextIndex, newModel);
                keysList.add(nextIndex, key);
            }
        }
        notifyChange(ChangeType.MOVED);
    }

    protected abstract void notifyChange(ChangeType changeType);

    public List<T> getDataList() {
        return dataList;
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        if (onErrorListener != null) {
            onErrorListener.onError(firebaseError);
        }
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public interface OnErrorListener {
        void onError(FirebaseError firebaseError);
    }
}
