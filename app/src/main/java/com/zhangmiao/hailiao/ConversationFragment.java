package com.zhangmiao.hailiao;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.zhangmiao.hailiao.R;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangmiao on 2017/2/24.
 */
public class ConversationFragment extends Fragment {

    private ListView mConversationListView;
    private Map<String, EMConversation> mConversations;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversation, container, false);

        mConversationListView = (ListView) view.findViewById(R.id.conversation_list);
        mConversations = EMClient.getInstance().chatManager().getAllConversations();


        return view;
    }
}
