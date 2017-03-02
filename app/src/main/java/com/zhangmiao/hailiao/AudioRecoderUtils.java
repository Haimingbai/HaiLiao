package com.zhangmiao.hailiao;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.util.TimeUtils;

import java.io.File;
import java.io.IOException;

/*
 * Created by zhangmiao on 2017/3/1.
 */
public class AudioRecoderUtils {

    private String filePath;
    private String FolderPath;

    private MediaRecorder mMediaRecorder;
    private final String TAG = "AudioRecoderUtils";
    public static final int MAX_LENGTH = 1000 * 60 * 10;

    private OnAudioStatusUpdateListener audioStatusUpdateListener;

    public AudioRecoderUtils() {
        this(Environment.getExternalStorageDirectory() + "/record/");
    }

    public AudioRecoderUtils(String filePath) {
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        this.FolderPath = filePath;
    }

    private long startTime;
    private long endTime;

    public void startRecord() {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            filePath = FolderPath + System.currentTimeMillis() + ".amr";

            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();

            mMediaRecorder.start();
            startTime = System.currentTimeMillis();
            updateMicStatus();
            Log.e("test", "startTime : " + startTime);
        } catch (IllegalStateException e) {
            Log.e("test", "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        } catch (IOException e) {
            Log.e("test", "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
    }

    public long stopRecord() {
        if (mMediaRecorder == null) {
            return 0L;
        }
        endTime = System.currentTimeMillis();
        Log.e("test", "endTime : " + endTime);
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;

        audioStatusUpdateListener.onStop(filePath,(int)(endTime - startTime)/1000);
        filePath = "";

        return endTime - startTime;
    }

    public void cancelRecord() {
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        filePath = "";
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMisStatusTimer = new Runnable() {
        @Override
        public void run() {
            updateMicStatus();
        }
    };

    private int BASE = 1;
    private int SPACE = 100;

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    private void updateMicStatus() {
        if (mMediaRecorder != null) {
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;//分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
                if (null != audioStatusUpdateListener) {
                    audioStatusUpdateListener.onUpdate(db, System.currentTimeMillis() - startTime);
                }
            }
            mHandler.postDelayed(mUpdateMisStatusTimer, SPACE);
        }
    }


    public interface OnAudioStatusUpdateListener {

        public void onUpdate(double db, long time);

        public void onStop(String filePath,int time);
    }
}
