package com.example.seungwook.withme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by seungwook on 2015. 12. 2..
 */
public class ChatAdapter extends ArrayAdapter<ChatMessage> {
    private TextView chatText;
    private List<ChatMessage> MessageList = new ArrayList<ChatMessage>();
    private LinearLayout layout;



    public ChatAdapter(Context applicationContext, int chat) {
        super(applicationContext, chat);
    }

    public void add(ChatMessage object) {
        MessageList.add(object);
        super.add(object);
    }

    public int getCount() {
        return this.MessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.MessageList.get(index);
    }


    public View getView(int position, View ConvertView, ViewGroup parent) {
        View v = ConvertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.chat, parent, false);
        }
        layout = (LinearLayout) v.findViewById(R.id.Message1);
        ChatMessage messageobj = getItem(position);
        chatText = (TextView) v.findViewById(R.id.lbl1);
        chatText.setText(messageobj.message);
        chatText.setBackgroundResource(messageobj.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
        layout.setGravity(messageobj.left ? Gravity.LEFT : Gravity.RIGHT);

        return v;
    }

    public Bitmap decodeToByteArray(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

    }
}