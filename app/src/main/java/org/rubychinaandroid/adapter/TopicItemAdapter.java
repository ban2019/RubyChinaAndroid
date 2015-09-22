package org.rubychinaandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.activity.MainActivity;
import org.rubychinaandroid.activity.PostActivity;
import org.rubychinaandroid.model.TopicModel;
import org.rubychinaandroid.utils.RubyChinaConstants;

import java.util.ArrayList;

public class TopicItemAdapter extends RecyclerView.Adapter<TopicItemAdapter.ViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<TopicModel> mTopicList;

    public TopicItemAdapter(Context context, ArrayList<TopicModel> topicList) {
        mTopicList = topicList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.topic_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final TopicModel topic = mTopicList.get(position);

        /* 1. load title */
        holder.title.setText(topic.getTitle());

        /* 2. load author and publish time */
        holder.time.setText(topic.getDetail());

        /* 3. load avatar */
        String userAvatarUrl = topic.getUserAvatarUrl();
        ImageLoader.getInstance().displayImage(userAvatarUrl, holder.avatar, MyApplication.imageLoaderOptions);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert (mContext instanceof MainActivity);
                MainActivity activity = (MainActivity) mContext;
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra(RubyChinaConstants.TOPIC_ID, topic.getTopicId());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTopicList == null ? 0 : mTopicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView avatar;
        public TextView title;
        public TextView time;

        ViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.card_container);
            avatar = (ImageView) view.findViewById(R.id.image_view);
            title = (TextView) view.findViewById(R.id.topic_text_view);
            time = (TextView) view.findViewById(R.id.created_time_text_view);
        }
    }
}