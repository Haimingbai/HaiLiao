package com.zhangmiao.hailiao.UI;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zhangmiao.hailiao.MailListFragment;
import com.zhangmiao.hailiao.R;

import java.util.ArrayList;
import java.util.Map;

/*
 * Created by zhangmiao on 2017/2/27.
 */
public class NotificationActivity extends Activity {

    ArrayList<Map<String, String>> mNotificationDataList = MailListFragment.sNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ListView mNotificationListView = (ListView) findViewById(R.id.notification_list);

        NotificationListAdapter adapter = new NotificationListAdapter(this, R.layout.notification_item);
        mNotificationListView.setAdapter(adapter);
    }

    public class NotificationListAdapter extends BaseAdapter {

        private Context mContext;
        private int mResourceId;

        public NotificationListAdapter(Context context, int resourceId) {
            mContext = context;
            mResourceId = resourceId;
        }

        @Override
        public int getCount() {
            return mNotificationDataList.size();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView userNameTV;
            TextView messageTV;
            Button agreeButton;
            Button refuseButton;

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(mResourceId, null);
                Map<String, String> notificationMap = mNotificationDataList.get(position);
                final String userName = notificationMap.get("username");
                String message = notificationMap.get("message");

                userNameTV = (TextView) convertView.findViewById(R.id.notification_item_user_name);
                messageTV = (TextView) convertView.findViewById(R.id.notification_item_validation_information);
                userNameTV.setText(userName);
                messageTV.setText(message);

                agreeButton = (Button) convertView.findViewById(R.id.notification_item_agree);
                agreeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("test", "同意好友邀请");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    EMClient.getInstance().contactManager().acceptInvitation(userName);
                                    Log.e("test", "好友邀请成功 username=" + userName);
                                } catch (HyphenateException e) {
                                    e.printStackTrace();
                                    Log.e("test", "errorCode = " + e.getErrorCode());
                                }
                            }
                        }).start();
                    }
                });
                refuseButton = (Button) convertView.findViewById(R.id.notification_item_refuse);
                refuseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            EMClient.getInstance().contactManager().declineInvitation(userName);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            return convertView;
        }
    }


}
