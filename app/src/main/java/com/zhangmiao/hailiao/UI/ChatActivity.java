package com.zhangmiao.hailiao.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.chat.adapter.message.EMAVoiceMessageBody;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.zhangmiao.hailiao.AudioRecoderUtils;
import com.zhangmiao.hailiao.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by zhangmiao on 2017/2/27.
 */
public class ChatActivity extends Activity {

    private EditText mChatSendMessage;
    private String friendName;
    List<EMMessage> messages;
    List<String> EMMessageUserName;
    List<String> EMMessageBody;
    List<EMMessage> emMessages;
    ChatMessageListAdapter adapter;
    TableLayout mTableLayout;
    MediaPlayer mediaPlayer;
    MediaRecorder recorder;

    private AudioRecoderUtils mAudioRecoderUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TextView mFriendName = (TextView) findViewById(R.id.chat_friend_name);
        ListView mChatMessageList = (ListView) findViewById(R.id.chat_message_list);
        mChatSendMessage = (EditText) findViewById(R.id.chat_send_message);
        Button mChatSendButton = (Button) findViewById(R.id.chat_send_button);
        mTableLayout = (TableLayout) findViewById(R.id.chat_table_layout);

        Intent intent = getIntent();
        friendName = intent.getStringExtra("friendname");
        Log.e("test", "chatActivity friendName = " + friendName);
        mFriendName.setText(friendName);
        mChatSendButton.setOnClickListener(sendListener);

        Button chatSendMore = (Button) findViewById(R.id.chat_send_more);
        chatSendMore.setOnClickListener(sendMoreListener);

        LinearLayout chatSendPhoto = (LinearLayout) findViewById(R.id.chat_send_photo);
        LinearLayout chatSendVideo = (LinearLayout) findViewById(R.id.chat_send_video);
        LinearLayout chatSendVoice = (LinearLayout) findViewById(R.id.chat_send_voice);
        chatSendPhoto.setOnClickListener(sendPhotoListener);
        chatSendVideo.setOnClickListener(sendVideoListener);
        chatSendVoice.setOnClickListener(sendVoiceListener);
        chatSendVoice.setOnTouchListener(sendVoiceTouchListener);

        recorder = new MediaRecorder();

        mAudioRecoderUtils = new AudioRecoderUtils();
        mAudioRecoderUtils.setOnAudioStatusUpdateListener(audioStatusUpdateListener);

        EMMessageUserName = new ArrayList<>();
        EMMessageBody = new ArrayList<>();
        emMessages = new ArrayList<>();

        EMClient.getInstance().chatManager().addMessageListener(messageListener);

        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(friendName);
        if (conversation != null) {
            messages = conversation.getAllMessages();

            for (int i = 0; i < messages.size(); i++) {
                EMMessage emMessage = messages.get(i);
                Log.e("test", "emMessage.getUserName() = " + emMessage.getUserName());
                Log.e("test", "emMessage = " + emMessage.toString());
                EMMessageUserName.add(emMessage.getFrom());

                String text = emMessage.getBody().toString();
                EMMessageBody.add(text);
                emMessages.add(emMessage);
            }
        }
        adapter = new ChatMessageListAdapter(this);
        mChatMessageList.setAdapter(adapter);
        mChatMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMMessage message = emMessages.get(position);
                switch (message.getType()) {
                    case TXT:
                        break;
                    case IMAGE:
                        break;
                    case VOICE:
                        EMVoiceMessageBody body = (EMVoiceMessageBody) message.getBody();
                        String filePath = body.getLocalUrl();

                        if (!(new File(filePath).exists())) {
                            Log.e("test", "!(new File(filePath).exists())");
                            return;
                        }
                        try {
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            mediaPlayer.setDataSource(filePath);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (Exception e) {
                            System.out.println();
                        }
                        break;
                    default:
                        Toast.makeText(ChatActivity.this, "message = " + message.toString(), Toast.LENGTH_SHORT);
                        break;
                }
            }
        });

    }

    private View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String content = mChatSendMessage.getText().toString();
            EMMessage message = EMMessage.createTxtSendMessage(content, friendName);
            EMClient.getInstance().chatManager().sendMessage(message);
            mChatSendMessage.setText("");
            adapter.addData(EMClient.getInstance().getCurrentUser(), message);
        }
    };

    private View.OnClickListener sendMoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mTableLayout.getVisibility() == View.VISIBLE) {
                mTableLayout.setVisibility(View.GONE);
            } else {
                mTableLayout.setVisibility(View.VISIBLE);
            }
        }
    };

    private View.OnClickListener sendPhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent picture = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(picture, 3);
        }
    };

    private AudioRecoderUtils.OnAudioStatusUpdateListener audioStatusUpdateListener = new AudioRecoderUtils.OnAudioStatusUpdateListener() {
        @Override
        public void onUpdate(double db, long time) {
            Toast.makeText(ChatActivity.this, "录音时长：" + time, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStop(String filePath, int time) {
            Toast.makeText(ChatActivity.this, "录音保存在：" + filePath, Toast.LENGTH_SHORT).show();
            Log.e("test", "time = " + time);
            EMMessage message = EMMessage.createVoiceSendMessage(filePath, time, friendName);
            EMClient.getInstance().chatManager().sendMessage(message);
            adapter.addData(EMClient.getInstance().getCurrentUser(), message);
        }
    };

    private View.OnClickListener sendVoiceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnTouchListener sendVoiceTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Toast.makeText(ChatActivity.this, "正在录音，松开保存", Toast.LENGTH_SHORT).show();
                    //mAudioRecoderUtils.startRecord();
                    if(recorder == null)
                    {
                        recorder = new MediaRecorder();
                    }
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                    String filePath = Environment.getExternalStorageDirectory() + "/record/" + System.currentTimeMillis() + ".amr";
                    recorder.setOutputFile(filePath);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        recorder.prepare();
                    } catch (IOException e) {
                        Log.e("test","recorder.prepare() failed");
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    Toast.makeText(ChatActivity.this, "录音完成", Toast.LENGTH_SHORT).show();
                    //mAudioRecoderUtils.stopRecord();
                    recorder.stop();
                    recorder.reset();
                    recorder.release();
                    recorder = null;
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private View.OnClickListener sendVideoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToNext();

            String path = cursor.getString(column_index);
            EMMessage message = EMMessage.createImageSendMessage(path, false, friendName);
            EMClient.getInstance().chatManager().sendMessage(message);
            adapter.addData(EMClient.getInstance().getCurrentUser(), message);
        }
    }

    private EMMessageListener messageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            Log.e("test", "收到消息");
            for (int i = 0; i < list.size(); i++) {
                EMMessage emMessage = list.get(i);
                adapter.addData(emMessage.getFrom(), emMessage);
                adapter.notifyAll();
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
            Log.e("test", "收到透传消息");
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
            Log.e("test", "收到已读回执");
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
            Log.e("test", "收到已送达回执");
        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
            Log.e("test", "消息状态变动");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
    }

    private class ChatMessageListAdapter extends BaseAdapter {

        private Context mContext;

        public ChatMessageListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return EMMessageUserName.size();
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
            TextView userName;
            TextView conversation_text;
            ImageView conversation_image;
            ImageView conversation_video;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_message_item, null);
            }
            userName = (TextView) convertView.findViewById(R.id.message_item_user_name);
            conversation_text = (TextView) convertView.findViewById(R.id.message_item_conversation_text);
            conversation_image = (ImageView) convertView.findViewById(R.id.message_item_conversation_image);
            conversation_video = (ImageView) convertView.findViewById(R.id.message_item_conversation_video);
            userName.setText(EMMessageUserName.get(position));

            final EMMessage msg = emMessages.get(position);
            EMMessage.Type type = msg.getType();
            switch (type) {
                case TXT:
                    String content = ((EMTextMessageBody) msg.getBody()).getMessage();
                    conversation_text.setText(content);
                    break;
                case IMAGE:
                    conversation_text.setVisibility(View.INVISIBLE);
                    conversation_image.setVisibility(View.VISIBLE);
                    EMImageMessageBody body = (EMImageMessageBody) msg.getBody();

                    if (new File(body.getLocalUrl()).exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(body.getLocalUrl());
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        if (height > 160) {
                            options.inSampleSize = width > height ? width / 160 : height / 160;
                        } else {
                            options.inSampleSize = 1;
                        }
                        conversation_image.setImageBitmap(BitmapFactory.decodeFile(body.getLocalUrl(), options));
                        break;
                    }
                    if (msg.direct() == EMMessage.Direct.RECEIVE) {
                        if (body.thumbnailDownloadStatus() ==
                                EMFileMessageBody.EMDownloadStatus.DOWNLOADING
                                || body.thumbnailDownloadStatus() ==
                                EMFileMessageBody.EMDownloadStatus.PENDING
                                ) {
                            conversation_image.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            String thumbPath = body.thumbnailLocalPath();
                            if (!new File(thumbPath).exists()) {
                                thumbPath = EaseImageUtils.getThumbnailImagePath(body.getLocalUrl());
                            }
                            showImageView(body, thumbPath, conversation_image, body.getLocalUrl(), msg);
                        }
                        String filePath = body.getLocalUrl();
                        String thumbPath = EaseImageUtils.getThumbnailImagePath(body.getLocalUrl());
                        showImageView(body, thumbPath, conversation_image, filePath, msg);
                    }
                    break;

                case VOICE:
                    conversation_text.setVisibility(View.INVISIBLE);
                    conversation_video.setVisibility(View.VISIBLE);
                    break;
                default:
                    String defaultMessage = msg.toString();
                    conversation_text.setText(defaultMessage);
                    break;
            }
            return convertView;
        }

        private boolean showImageView(final EMImageMessageBody body, final String thumbernailPath, final ImageView iv, final String localFullSizePath, final EMMessage message) {
            Bitmap bitmap = EaseImageCache.getInstance().get(thumbernailPath);
            if (bitmap != null) {
                iv.setImageBitmap(bitmap);
                return true;
            } else {
                new AsyncTask<Object, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Object... params) {
                        File file = new File(thumbernailPath);
                        if (file.exists()) {
                            return EaseImageUtils.decodeScaleImage(thumbernailPath, 160, 160);
                        } else if (new File(body.thumbnailLocalPath()).exists()) {
                            return EaseImageUtils.decodeScaleImage(body.thumbnailLocalPath(), 160, 160);
                        } else {
                            if (message.direct() == EMMessage.Direct.SEND) {
                                if (localFullSizePath != null && new File(localFullSizePath).exists()) {
                                    return EaseImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
                                } else {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                        }
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        if (bitmap != null) {
                            iv.setImageBitmap(bitmap);
                            EaseImageCache.getInstance().put(thumbernailPath, bitmap);
                        } else {
                            if (message.status() == EMMessage.Status.FAIL) {
                                if (EaseCommonUtils.isNetWorkConnected(ChatActivity.this)) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            EMClient.getInstance().chatManager().downloadThumbnail(message);
                                        }
                                    }).start();
                                }
                            }
                        }
                    }
                }.execute();
            }
            return true;
        }

        public void addData(String username, EMMessage message) {
            if (EMMessageUserName == null) {
                EMMessageUserName = new ArrayList<>();
            }
            EMMessageUserName.add(username);
            if (EMMessageBody == null) {
                EMMessageBody = new ArrayList<>();
            }
            emMessages.add(message);
            notifyDataSetChanged();
        }

    }
}
