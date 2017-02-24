package com.zhangmiao.hailiao;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zhangmiao.hailiao.UI.AddFriendActivity;

import java.util.List;

/**
 * Created by zhangmiao on 2017/2/24.
 */
public class MailListFragment extends Fragment {

    private LinearLayout mAddFriendLL;
    private TextView mFriendListTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mail_list, container, false);
        mAddFriendLL = (LinearLayout) view.findViewById(R.id.add_friend_ll);
        mAddFriendLL.setOnClickListener(addFriendListener);
        mFriendListTv = (TextView) view.findViewById(R.id.friend_list_tv);

        setFriendListener();

        return view;
    }

    private View.OnClickListener addFriendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getContext(), AddFriendActivity.class));
            /*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e("test","onclick");
                        List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                        String userNameString = "";
                        Log.e("test","size = "+usernames.size());
                        for (int i = 0; i<usernames.size();i++){
                            userNameString += usernames;
                        }
                        Log.e("test","username = "+userNameString);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        Log.e("test"," ErrorCode = "+e.getErrorCode());
                    }
                }
            }).start();
            */
        }
    };

    private void setFriendListener() {
        Log.e("test", "setFriendListener");
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            @Override
            public void onContactAdded(String s) {
                Log.e("test", "添加了联系人");
                mFriendListTv.setText(mFriendListTv.getText() + " , " + "添加了联系人");
            }

            @Override
            public void onContactDeleted(String s) {
                Log.e("test", "删除了联系人");
                mFriendListTv.setText(mFriendListTv.getText() + " , " + "删除了联系人");
            }

            @Override
            public void onContactInvited(String s, String s1) {
                Log.e("test", "收到好友邀请");
                mFriendListTv.setText(mFriendListTv.getText() + " , " + "收到好友邀请");
                //在这里处理好友添加
            }

            @Override
            public void onFriendRequestAccepted(String s) {
                Log.e("test", "好友请求被同意");
                mFriendListTv.setText(mFriendListTv.getText() + " , " + "好友请求被同意");
            }

            @Override
            public void onFriendRequestDeclined(String s) {
                Log.e("test", "好友请求被拒绝");
                mFriendListTv.setText(mFriendListTv.getText() + " , " + "好友请求被拒绝");
            }
        });

    }
}
