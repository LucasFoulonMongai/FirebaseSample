package com.lfm.firesample.managers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.lfm.firesample.firebase.FirebaseManager;
import com.lfm.firesample.models.Message;

/**
 * Created by Lucas FOULON-MONGAÏ, github.com/LucasFoulonMongai  on 21/10/15.
 */
public class MessageManager extends FirebaseManager<Message> {
    public static final String NOTIFICATION_UPDATE = "notification_update";

    private static MessageManager instance;
    private final LocalBroadcastManager localBroadcastManager;

    private MessageManager(Context context, String conversationId) {
        super(conversationId);
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public static MessageManager getInstance(Context context, String conversationId) {
        if (instance == null) {
            instance = new MessageManager(context, conversationId);
        }
        return instance;
    }

    @Override
    protected void notifyChange(ChangeType changeType) {
        localBroadcastManager.sendBroadcast(new Intent(NOTIFICATION_UPDATE).putExtra("changeType", changeType));

    }
}
