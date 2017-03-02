package com.zhangmiao.hailiao;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.zhangmiao.hailiao.UI.ChatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*
 * Created by zhangmiao on 2017/2/24.
 */
public class ConversationFragment extends Fragment {

    private Map<String, EMConversation> mConversations;
    ConversationListAdapter adapter;
    private List<String> mConversationUserNames;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversation, container, false);

        TextView textView = (TextView) view.findViewById(R.id.conversation_show);

        ListView mConversationListView = (ListView) view.findViewById(R.id.conversation_list);
        mConversations = EMClient.getInstance().chatManager().getAllConversations();

        mConversationUserNames = new ArrayList<>();

        String message = "";
        for (String key : mConversations.keySet()) {
            mConversationUserNames.add(key);
            Log.e("test", "key = " + key + " , value = " + mConversations.get(key));
            message += "key = " + key + " , value = " + mConversations.get(key);
        }
        textView.setText(message);

        adapter = new ConversationListAdapter(getContext());
        mConversationListView.setAdapter(adapter);
        mConversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("friendname", mConversationUserNames.get(position));
                intent.setClass(getContext(), ChatActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private class ConversationListAdapter extends BaseAdapter {

        private Context mContext;

        public ConversationListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mConversations.size();
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
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.conversation_list_item, null);
            }
            TextView userName = (TextView) convertView.findViewById(R.id.conversation_list_item_user_name);
            TextView lastMessage = (TextView) convertView.findViewById(R.id.conversation_list_item_last_message);
            TextView date = (TextView) convertView.findViewById(R.id.conversation_list_item_date);

            userName.setText(mConversationUserNames.get(position));
            EMConversation conversation = mConversations.get(mConversationUserNames.get(position));
            EMMessage message = conversation.getLastMessage();
            String lastMessageText = "";
            switch (message.getType()) {
                case TXT:
                    lastMessageText = ((EMTextMessageBody) message.getBody()).getMessage();
                    break;
                case IMAGE:
                    lastMessageText = "[image]";
                    break;
                case VIDEO:
                    lastMessageText = "[video]";
                    break;
                case VOICE:
                    lastMessageText = "[voice]";
                    break;
                default:
                    break;
            }
            lastMessage.setText(lastMessageText);

            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm", Locale.US);
            String times = format.format(new Date(message.getMsgTime()));

            date.setText(times);
            return convertView;
        }
    }
}
