package com.lfm.firesample.presenters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lfm.firesample.R;
import com.lfm.firesample.models.Message;
import com.lfm.rvgenadapter.ViewPresenter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Lucas FOULON-MONGAÏ, github.com/LucasFoulonMongai  on 21/10/15.
 */
public class MessagePresenter extends ViewPresenter<Message> {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
    private TextView messageHoraire;
    private TextView messageUser;
    private TextView messageText;
    private View view;

    @Override
    public void initViewPresenter(Context context, ViewGroup parent, Bundle params) {
        android.view.LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_message, parent, false);
        messageHoraire = (TextView) view.findViewById(R.id.messageHoraire);
        messageUser = (TextView) view.findViewById(R.id.messageUser);
        messageText = (TextView) view.findViewById(R.id.messageText);
    }

    @Override
    public void refresh() {
        Message data = getData();
        if (data != null) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }

        messageHoraire.setText(dateFormat.format(new Date(data.getTime())));
        messageUser.setText(data.getUserId());
        messageText.setText(data.getMessage());
    }

    @Override
    public View getView() {
        return view;
    }

}
