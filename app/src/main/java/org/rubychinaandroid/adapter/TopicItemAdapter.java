package org.rubychinaandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.model.TopicModel;

import java.util.List;


public class TopicItemAdapter extends ArrayAdapter<TopicModel> {

    public String LOG_TAG = "TopicAdapter";

    private int mResourceId;

    public TopicItemAdapter(Context context, int textViewResourceId, List<TopicModel> objects) {
        super(context, textViewResourceId, objects);
        mResourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TopicModel topic = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(mResourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.authorAvatar = (ImageView) view.findViewById(R.id.image_view);
            viewHolder.topicTitle = (TextView) view.findViewById(R.id.topic_text_view);
            viewHolder.createdTime = (TextView) view.findViewById(R.id.created_time_text_view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        /* 1. load title */
        viewHolder.topicTitle.setText(topic.getTitle());

        /* 2. load author and publish time */
        viewHolder.createdTime.setText(topic.getDetail());

        /* 3. load avatar */
        String userAvatarUrl = topic.getUserAvatarUrl();
        ImageLoader.getInstance().displayImage(userAvatarUrl, viewHolder.authorAvatar, MyApplication.imageLoaderOptions);

        return view;
    }

    class ViewHolder {
        ImageView authorAvatar;
        TextView topicTitle;
        TextView createdTime;
    }
}
