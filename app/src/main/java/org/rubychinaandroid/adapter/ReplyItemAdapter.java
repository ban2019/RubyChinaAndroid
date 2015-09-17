package org.rubychinaandroid.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.model.ReplyModel;
import org.rubychinaandroid.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReplyItemAdapter extends ArrayAdapter<ReplyModel> {

    private int mResourceId;

    public ReplyItemAdapter(Context context, int textViewResourceId, List<ReplyModel> replyList) {
        super(context, textViewResourceId, replyList);
        mResourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ReplyModel reply = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(mResourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.avatar = (ImageView) view.findViewById(R.id.reply_avatar);
            viewHolder.replier = (TextView) view.findViewById(R.id.reply_replier);
            viewHolder.replyTime = (TextView) view.findViewById(R.id.reply_time);
            viewHolder.replyContent = (TextView) view.findViewById(R.id.reply_content);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        /* 1. load avatar */
        ImageLoader.getInstance().displayImage(reply.getUserAvatarUrl(), viewHolder.avatar, MyApplication.imageLoaderOptions);

        /* 2. load replier name */
        String replier = reply.getUserName();
        replier = ("".equals(replier) ? reply.getUserLogin() : replier);
        viewHolder.replier.setText(replier);

        /* 3. load reply time */
        viewHolder.replyTime.setText("创建于 " + reply.getCreatedTime());

        /* 4. load the content */
        viewHolder.replyContent.setText(Html.fromHtml(reply.getBodyHtml()));

        return view;
    }

    class ViewHolder {
        ImageView avatar;
        TextView replier;
        TextView replyTime;
        TextView replyContent;
    }
}
