package org.rubychinaandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import org.rubychinaandroid.R;
import org.rubychinaandroid.utils.RubyChinaArgKeys;

import in.uncod.android.bypass.Bypass;


public class PreviewActivity extends BaseActivity {

    @Override
    public void configToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("预览");
        setToolbarBackButton();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        Intent intent = getIntent();
        String content = intent.getStringExtra(RubyChinaArgKeys.POST_CONTENT);

        TextView preview = (TextView) findViewById(R.id.text_view);
        Bypass bypass = new Bypass();
        CharSequence string = bypass.markdownToSpannable(content);
        preview.setText(string);
        preview.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
