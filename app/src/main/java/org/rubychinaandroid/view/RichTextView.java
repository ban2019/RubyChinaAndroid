package org.rubychinaandroid.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.rubychinaandroid.activity.PhotoViewActivity;
import org.rubychinaandroid.utils.AsyncImageGetter;

import java.util.ArrayList;

/**
 * Created by yw on 2015/5/10.
 */
public class RichTextView extends TextView {

    private final String TAG = "RichTextView";
    public RichTextView(Context context) {
        super(context);
    }

    public RichTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setRichText(String text, boolean isDisplayImage) {

        //移动网络情况下如果设置了不显示图片,则遵命
        if (!isDisplayImage) {
            super.setText(Html.fromHtml(text));
            setMovementMethod(LinkMovementMethod.getInstance());
            return;
        }

        Spanned spanned = Html.fromHtml(text, new AsyncImageGetter(getContext(), this), null);
        SpannableStringBuilder htmlSpannable;
        if (spanned instanceof SpannableStringBuilder) {
            htmlSpannable = (SpannableStringBuilder) spanned;
        } else {
            htmlSpannable = new SpannableStringBuilder(spanned);
        }

        ImageSpan[] spans = htmlSpannable.getSpans(0, htmlSpannable.length(), ImageSpan.class);
        final ArrayList<String> imageUrls = new ArrayList<String>();
        final ArrayList<String> imagePositions = new ArrayList<String>();
        for (ImageSpan span : spans) {
            final String imageUrl = span.getSource();
            final int start = htmlSpannable.getSpanStart(span);
            final int end = htmlSpannable.getSpanEnd(span);
            imagePositions.add(start + "/" + end);
            imageUrls.add(imageUrl);
        }

        for (ImageSpan span : spans) {
            final int start = htmlSpannable.getSpanStart(span);
            final int end = htmlSpannable.getSpanEnd(span);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    PhotoViewActivity.launch(getContext(), imagePositions.indexOf(start + "/" + end), imageUrls);
                }
            };

            ClickableSpan[] clickSpans = htmlSpannable.getSpans(start, end, ClickableSpan.class);
            if (clickSpans != null && clickSpans.length != 0) {

                for (ClickableSpan c_span : clickSpans) {
                    htmlSpannable.removeSpan(c_span);
                }
            }

            htmlSpannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Replace all the URLSpans with self-defined DefensiveSpans, to handle the hyperlinks of the
        // floor number(such as #1, #2, etc), and user login(such as @username), which contain
        // invalid urls in href
        URLSpan[] urlSpans = htmlSpannable.getSpans(0, htmlSpannable.length(), URLSpan.class);
        for (URLSpan span : urlSpans) {
            int start = htmlSpannable.getSpanStart(span);
            int end = htmlSpannable.getSpanEnd(span);
            htmlSpannable.removeSpan(span);
            htmlSpannable.setSpan(new DefensiveURLSpan(span.getURL()), start, end, 0);
        }

        super.setText(spanned);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    private static class DefensiveURLSpan extends URLSpan {
        private final String TAG = "DefensiveURLSpan";
        public DefensiveURLSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View widget) {
            try {
                super.onClick(widget);
            } catch (ActivityNotFoundException e) {
                Log.d(TAG, "caught");
            }
        }
    }
}
