package com.zhangmiao.hailiao.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.zhangmiao.hailiao.R;
import com.zhangmiao.hailiao.SharedPreferencesDBManager;
import com.zhangmiao.hailiao.User;

/**
 * Created by zhangmiao on 2017/2/23.
 */
public class LoginActivity extends Activity {

    private EditText mUsernameET;
    private EditText mPasswordET;
    public SharedPreferencesDBManager mSPMnager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameET = (EditText) findViewById(R.id.login_username);
        mPasswordET = (EditText) findViewById(R.id.login_password);

        Button register = (Button) findViewById(R.id.register_button);
        register.setOnClickListener(registerListener);

        Button login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(loginListener);

        Intent intent = getIntent();
        if (intent != null) {
            String username = intent.getStringExtra("username");
            String password = intent.getStringExtra("password");
            mUsernameET.setText(username);
            mPasswordET.setText(password);
        }

        mSPMnager = new SharedPreferencesDBManager(this);
    }

    private View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    };

    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final String username = mUsernameET.getText().toString();
            final String password = mPasswordET.getText().toString();

            EMClient.getInstance().login(username, password, new EMCallBack() {
                @Override
                public void onSuccess() {
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    Log.e("test", "login success");

                    User user = new User(username, password);
                    mSPMnager.writeData(user);

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }

                @Override
                public void onError(int i, String s) {
                    //Toast.makeText(LoginActivity.this,"登录服务器失败，错误是:"+s,Toast.LENGTH_SHORT);
                    Log.e("test", "login error:" + s);
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }
    };

}
