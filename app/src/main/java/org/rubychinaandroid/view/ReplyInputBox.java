package org.rubychinaandroid.view;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.rubychinaandroid.R;
import org.rubychinaandroid.api.RubyChinaApiListener;
import org.rubychinaandroid.api.RubyChinaApiWrapper;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.utils.oauth.OAuthManager;


public class ReplyInputBox extends LinearLayout {

    private final String LOG_TAG = "BottomReplyBar";
    private String mTopicId;
    private EditText mBottomReplyEditText;
    private Button mSendButton;

    public ReplyInputBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.reply_input_box, this);

        mBottomReplyEditText = (EditText) findViewById(R.id.edit_text);
        mSendButton = (Button) findViewById(R.id.send_button);

        mBottomReplyEditText.addTextChangedListener(new ReplyInputBoxTextChangeListener() {

            @Override
            public void afterTextChanged(Editable s) {
                if (mBottomReplyEditText.getText().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }
        });

        mSendButton.setEnabled(false);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!OAuthManager.getInstance().isLoggedIn()) {
                    Utility.showToast("还没有在主页面登录哦");
                    return;
                }

                String replyContent = mBottomReplyEditText.getText().toString();

                RubyChinaApiWrapper.replyToPost(mTopicId, replyContent, new RubyChinaApiListener() {

                    @Override
                    public void onSuccess(Object data) {
                        Utility.showToast("回复成功");
                    }

                    @Override
                    public void onFailure(String error) {
                        Utility.showToast("回复失败");
                    }
                });
            }
        });
    }

    public void setTopicId(String topicId) {
        this.mTopicId = topicId;
    }

    public void hintReplyTo(int floor, String name) {
        String text = "#" + floor + "楼" + " " + "@" + name + " ";
        mBottomReplyEditText.setText(text);
        mBottomReplyEditText.setSelection(text.length());
    }
}
