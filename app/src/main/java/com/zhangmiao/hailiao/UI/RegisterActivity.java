package com.zhangmiao.hailiao.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zhangmiao.hailiao.R;

/**
 * Created by zhangmiao on 2017/2/23.
 */
public class RegisterActivity extends Activity {

    private EditText mUserNameET;
    private EditText mPasswordFirstET;
    private EditText mPasswordSecondET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUserNameET = (EditText) findViewById(R.id.register_username);
        mPasswordFirstET = (EditText) findViewById(R.id.register_password_first);
        mPasswordSecondET = (EditText) findViewById(R.id.register_password_second);

        Button regiter = (Button) findViewById(R.id.register_button);
        regiter.setOnClickListener(registerListener);

    }

    private View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String userName = mUserNameET.getText().toString();
                    String passwordFirst = mPasswordFirstET.getText().toString();
                    String passwordSecond = mPasswordSecondET.getText().toString();

                    if (!passwordFirst.equals(passwordSecond)) {
                        Toast.makeText(RegisterActivity.this, "前后密码不一致，请重新填写！", Toast.LENGTH_SHORT);
                        Log.e("test", "前后密码不一致，请重新填写！");
                        return;
                    }
                    try {
                        EMClient.getInstance().createAccount(userName, passwordFirst);
                        Log.e("test", "register success ");
                        Intent intent = new Intent();
                        intent.putExtra("username", userName);
                        intent.putExtra("password", passwordFirst);
                        intent.setClass(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        Log.e("test", "register fail e " + e.toString() + " " + e.getErrorCode() + " " + e.getDescription());
                        Toast.makeText(RegisterActivity.this, "注册失败，原因是：" + e.toString(), Toast.LENGTH_SHORT);
                    }

                }
            }).start();
        }
    };

}
