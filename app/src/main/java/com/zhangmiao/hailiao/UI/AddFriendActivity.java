package com.zhangmiao.hailiao.UI;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zhangmiao.hailiao.R;

/**
 * Created by zhangmiao on 2017/2/24.
 */
public class AddFriendActivity extends Activity {

    EditText userNameEt;
    EditText reasonEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        userNameEt = (EditText) findViewById(R.id.add_friend_username_et);
        reasonEt = (EditText) findViewById(R.id.add_friend_reason_et);
        Button addFriendBt = (Button) findViewById(R.id.add_friend_bt);
        addFriendBt.setOnClickListener(addFriendListener);
    }

    private View.OnClickListener addFriendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                String userName = userNameEt.getText().toString();
                String reason = reasonEt.getText().toString();
                EMClient.getInstance().contactManager().addContact(userName, reason);
            } catch (HyphenateException e) {
                Log.e("test", "errorCode = " + e.getErrorCode());
            }
            finish();
        }
    };
}
