package com.zhangmiao.hailiao;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhangmiao.hailiao.R;

/**
 * Created by zhangmiao on 2017/2/24.
 */
public class ConversationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversation, container, false);
        return view;
    }
}
