package org.rubychinaandroid.activity;

import android.content.Intent;
import android.os.Bundle;

import org.rubychinaandroid.utils.RubyChinaConstants;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import us.feras.mdv.MarkdownView;


public class PreviewActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MarkdownView markdownView = new MarkdownView(this);

        setContentView(markdownView);

        Intent intent = getIntent();
        String content = intent.getStringExtra(RubyChinaConstants.POST_CONTENT);
        markdownView.loadMarkdown(content);
    }
}
