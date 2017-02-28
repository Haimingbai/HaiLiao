package com.zhangmiao.hailiao.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.zhangmiao.hailiao.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
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
    private Button mChatSendPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        TextView mFriendName = (TextView) findViewById(R.id.chat_friend_name);
        ListView mChatMessageList = (ListView) findViewById(R.id.chat_message_list);
        mChatSendMessage = (EditText) findViewById(R.id.chat_send_message);
        Button mChatSendButton = (Button) findViewById(R.id.chat_send_button);

        Intent intent = getIntent();
        friendName = intent.getStringExtra("friendname");
        Log.e("test", "chatActivity friendName = " + friendName);
        mFriendName.setText(friendName);
        mChatSendButton.setOnClickListener(sendListener);

        mChatSendPhoto = (Button) findViewById(R.id.chat_send_photo);
        mChatSendPhoto.setOnClickListener(sendPhotoListener);

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
    }

    private View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String content = mChatSendMessage.getText().toString();
            EMMessage message = EMMessage.createTxtSendMessage(content, friendName);
            EMClient.getInstance().chatManager().sendMessage(message);
            mChatSendMessage.setText("");
            //adapter.addData(EMClient.getInstance().getCurrentUser(), content);
            adapter.addData(EMClient.getInstance().getCurrentUser(), message);
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
            //adapter.addData(EMClient.getInstance().getCurrentUser(), message.getBody().toString());
            adapter.addData(EMClient.getInstance().getCurrentUser(), message);
        }
    }

    private EMMessageListener messageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            Log.e("test", "收到消息");
            for (int i = 0; i < list.size(); i++) {
                EMMessage emMessage = list.get(i);
                String body = emMessage.getBody().toString();
                //adapter.addData(emMessage.getFrom(), body);
                adapter.addData(emMessage.getFrom(), emMessage);
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
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_message_item, null);
            }
            userName = (TextView) convertView.findViewById(R.id.message_item_user_name);
            conversation_text = (TextView) convertView.findViewById(R.id.message_item_conversation_text);
            conversation_image = (ImageView) convertView.findViewById(R.id.message_item_conversation_image);
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
                            Log.e("test", "if ...");
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
                default:
                    String defaultMessage = ((EMTextMessageBody) msg.getBody()).getMessage();
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

        public void addData(String username, String message) {
            if (EMMessageUserName == null) {
                EMMessageUserName = new ArrayList<>();
            }
            EMMessageUserName.add(username);
            if (EMMessageBody == null) {
                EMMessageBody = new ArrayList<>();
            }
            EMMessageBody.add(message);
            notifyDataSetChanged();
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
