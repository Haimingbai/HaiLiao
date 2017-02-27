package com.zhangmiao.hailiao.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.zhangmiao.hailiao.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangmiao on 2017/2/27.
 */
public class ChatActivity extends Activity {

    private EditText mChatSendMessage;
    private String friendName;
    List<EMMessage> messages;
    List<String> EMMessageUserName;
    List<String> EMMessageText;
    ChatMessageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TextView mFriendName = (TextView) findViewById(R.id.chat_friend_name);
        ListView mChatMessageList = (ListView) findViewById(R.id.chat_message_list);
        mChatSendMessage = (EditText) findViewById(R.id.chat_send_message);
        Button mChatSendButton = (Button) findViewById(R.id.chat_send_button);

        Intent intent = getIntent();
        friendName = intent.getStringExtra("friendname");
        Log.e("test", "chatActivity friendName = " + friendName);
        mFriendName.setText(friendName);
        mChatSendButton.setOnClickListener(sendListener);

        EMMessageUserName = new ArrayList<>();
        EMMessageText = new ArrayList<>();

        EMClient.getInstance().chatManager().addMessageListener(messageListener);

        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(friendName);
        if (conversation != null) {
            messages = conversation.getAllMessages();

            for (int i = 0; i < messages.size(); i++) {
                EMMessage emMessage = messages.get(i);
                Log.e("test", "emMessage.getUserName() = " + emMessage.getUserName());
                Log.e("test", "emMessage = " + emMessage.toString());
                EMMessageUserName.add(emMessage.getFrom());

                String text = emMessage.getBody().toString();
                String[] subText = text.split(":");
                EMMessageText.add(subText[1].substring(1, subText[1].length() - 1));
            }
        }
        adapter = new ChatMessageListAdapter(this);
        mChatMessageList.setAdapter(adapter);
    }

    private View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String content = mChatSendMessage.getText().toString();
            EMMessage message = EMMessage.createTxtSendMessage(content, friendName);
            EMClient.getInstance().chatManager().sendMessage(message);
            mChatSendMessage.setText("");
            adapter.addData(EMClient.getInstance().getCurrentUser(), content);
        }
    };

    private EMMessageListener messageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            Log.e("test", "收到消息");
            for (int i = 0; i < list.size(); i++) {
                EMMessage emMessage = list.get(i);
                String text = emMessage.getBody().toString();
                String[] subText = text.split(":");

                adapter.addData(emMessage.getFrom(), subText[1].substring(1, subText[1].length() - 1));
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
            Log.e("test", "收到透传消息");
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
            Log.e("test", "收到已读回执");
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
            Log.e("test", "收到已送达回执");
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
            Log.e("test", "消息状态变动");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
    }

    private class ChatMessageListAdapter extends BaseAdapter {

        private Context mContext;

        public ChatMessageListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return EMMessageUserName.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView userName;
            TextView conversation;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_message_item, null);
            }
            userName = (TextView) convertView.findViewById(R.id.message_item_user_name);
            conversation = (TextView) convertView.findViewById(R.id.message_item_conversation);
            userName.setText(EMMessageUserName.get(position));
            conversation.setText(EMMessageText.get(position));

            return convertView;
        }

        public void addData(String username, String message) {
            if (EMMessageUserName == null) {
                EMMessageUserName = new ArrayList<>();
            }
            EMMessageUserName.add(username);
            if (EMMessageText == null) {
                EMMessageText = new ArrayList<>();
            }
            EMMessageText.add(message);
            notifyDataSetChanged();
        }
    }

}
