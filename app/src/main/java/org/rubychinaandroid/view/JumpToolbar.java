package org.rubychinaandroid.view;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.rubychinaandroid.fragments.PostFragment;

public class JumpToolbar extends Toolbar {
    private final String TAG = "JumpToolbar";
    private static long mLastMilliseconds = System.currentTimeMillis();
    private boolean clickedOnce = false;
    private ScrollCallback mFragment;

    public JumpToolbar(Context context) {
        super(context);
        setDoubleClickListener();
        setLongClickListener();
    }

    public JumpToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDoubleClickListener();
        setLongClickListener();
    }

    public JumpToolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDoubleClickListener();
        setLongClickListener();
    }

    public void attachTo(ScrollCallback fragmentAttachedTo) {
        mFragment = fragmentAttachedTo;
    }

    private void setDoubleClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // If clicking once more than one second ago(such as touch with no intention),
                // reset clickedOnce.
                if (System.currentTimeMillis() - mLastMilliseconds >= 500) {
                    clickedOnce = true;
                }

                if (!clickedOnce) {
                    clickedOnce = true;
                    return;
                }

                if (System.currentTimeMillis() - mLastMilliseconds < 500) {
                    clickedOnce = false;
                    mFragment.scrollTo(View.FOCUS_UP);
                }

                mLastMilliseconds = System.currentTimeMillis();
            }
        });
    }

    private void setLongClickListener() {
        this.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mFragment.scrollTo(View.FOCUS_DOWN);
                return true;
            }
        });
    }
}
