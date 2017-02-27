package com.zhangmiao.hailiao;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zhangmiao.hailiao.UI.AddFriendActivity;
import com.zhangmiao.hailiao.UI.NotificationActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangmiao on 2017/2/24.
 */
public class MailListFragment extends Fragment {

    public static final String CONTACT_INVITED = "contact_invited";
    public static final String CONTACT_ADDED = "contact_added";
    public static final String CONTACT_DELECTED = "contact_delected";
    public static final String FRIEND_REQUEST_ACCEPTED = "FriendRequestAccepted";
    public static final String FRIEND_REQUEST_DECLINED = "FriendRequestDeclined";

    private LinearLayout mAddFriendLL;

    private LinearLayout mApplicationAndNotificationLL;
    private ImageView mApplicationAndNotificationRedFlag;

    private ListView mFriendListLV;

    public static ArrayList<Map<String, String>> sNotification;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mail_list, container, false);

        sNotification = new ArrayList<>();

        mAddFriendLL = (LinearLayout) view.findViewById(R.id.add_friend_ll);
        mAddFriendLL.setOnClickListener(addFriendListener);

        mApplicationAndNotificationRedFlag = (ImageView) view.findViewById(R.id.application_notification_red_iv);
        mApplicationAndNotificationLL = (LinearLayout) view.findViewById(R.id.application_notification_ll);
        mApplicationAndNotificationLL.setOnClickListener(applicationAndNotificationListener);

        setFriendListener();

        mFriendListLV = (ListView) view.findViewById(R.id.friend_list_lv);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<String> friendsList = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFriendListLV.setAdapter(new ArrayAdapter<String>(
                                    getContext(),
                                    android.R.layout.simple_expandable_list_item_1,
                                    friendsList
                            ));
                            mFriendListLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            });
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    Log.e("test", " ErrorCode = " + e.getErrorCode());
                }
            }
        }).start();
        return view;
    }

    private View.OnClickListener addFriendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getContext(), AddFriendActivity.class));

        }
    };

    private View.OnClickListener applicationAndNotificationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //mApplicationAndNotificationRedFlag.setVisibility(View.INVISIBLE);
            startActivity(new Intent(getContext(), NotificationActivity.class));
        }
    };


    private void setFriendListener() {
        Log.e("test", "setFriendListener");
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            @Override
            public void onContactAdded(String s) {
                Log.e("test", "添加了联系人");
                //mApplicationAndNotificationRedFlag.setVisibility(View.VISIBLE);
                Map<String, String> map = new HashMap<>();
                map.put("message", "添加了联系人");
                map.put("username", s);
                sNotification.add(map);
            }

            @Override
            public void onContactDeleted(String s) {
                Log.e("test", "删除了联系人");
                //mApplicationAndNotificationRedFlag.setVisibility(View.VISIBLE);
                Map<String, String> map = new HashMap<>();
                map.put("message", "删除联系人");
                map.put("username", s);
                sNotification.add(map);
            }

            @Override
            public void onContactInvited(String s, String s1) {
                //s：用户名，s1：验证信息。
                Log.e("test", "收到好友邀请");
                Log.e("test", "s = " + s + " , s1 = " + s1);
                //在这里处理好友添加
                //mApplicationAndNotificationRedFlag.setVisibility(View.VISIBLE);
                String message = s + "," + s1;
                Map<String, String> map = new HashMap<>();
                map.put("message", "加个好友呗");
                map.put("username", s);
                sNotification.add(map);
            }

            @Override
            public void onFriendRequestAccepted(String s) {
                Log.e("test", "好友请求被同意");
                //mApplicationAndNotificationRedFlag.setVisibility(View.VISIBLE);
                Map<String, String> map = new HashMap<>();
                map.put("message", "好友请求被同意");
                map.put("username", s);
                sNotification.add(map);
            }

            @Override
            public void onFriendRequestDeclined(String s) {
                Log.e("test", "好友请求被拒绝");
                //mApplicationAndNotificationRedFlag.setVisibility(View.VISIBLE);
                Map<String, String> map = new HashMap<>();
                map.put("message", "好友请求被拒绝");
                map.put("username", s);
                sNotification.add(map);
            }
        });
    }
}
