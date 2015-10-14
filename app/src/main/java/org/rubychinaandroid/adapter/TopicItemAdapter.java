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
import org.rubychinaandroid.activity.FavouriteActivity;
import org.rubychinaandroid.activity.MainActivity;
import org.rubychinaandroid.activity.PostActivity;
import org.rubychinaandroid.activity.ProfileActivity;
import org.rubychinaandroid.model.TopicModel;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.view.FootUpdate.OnScrollToBottomListener;

import java.util.ArrayList;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class TopicItemAdapter extends RecyclerView.Adapter<TopicItemAdapter.ViewHolder> {
    private String TAG = "TopicItemAdapter";

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<TopicModel> mTopicList;
    private OnScrollToBottomListener mListener;

    public TopicItemAdapter(Context context, ArrayList<TopicModel> topicList, OnScrollToBottomListener listener) {
        mTopicList = topicList;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_topic, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TopicModel topic = mTopicList.get(position);
        /* 1. load title */
        holder.title.setText(topic.getTitle());
        Log.d(TAG, topic.getTitle());
        /* 2. load author and publish time */
        holder.time.setText(topic.getDetail());
        /* 3. load avatar */
        String userAvatarUrl = topic.getUserAvatarUrl();
        ImageLoader.getInstance().displayImage(userAvatarUrl, holder.avatar, MyApplication.imageLoaderOptions);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert (mContext instanceof MainActivity ||
                        mContext instanceof ProfileActivity ||
                        mContext instanceof FavouriteActivity);

                Intent intent;
                MainActivity mainActivity;
                SwipeBackActivity swipeActivity;
                if (mContext instanceof MainActivity) {
                    mainActivity = (MainActivity) mContext;
                    intent = new Intent(mainActivity, PostActivity.class);
                    intent.putExtra(RubyChinaArgKeys.TOPIC_ID, topic.getTopicId());
                    mainActivity.startActivity(intent);
                    mainActivity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                } else {
                    if (mContext instanceof ProfileActivity) {
                        swipeActivity = (ProfileActivity) mContext;
                    } else {
                        swipeActivity = (FavouriteActivity) mContext;
                    }
                    intent = new Intent(swipeActivity, PostActivity.class);
                    intent.putExtra(RubyChinaArgKeys.TOPIC_ID, topic.getTopicId());
                    swipeActivity.startActivity(intent);
                    swipeActivity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                }
            }
        });

        if (mTopicList.size() - position <= 1 && mListener != null) {
            mListener.onLoadMore();
        }
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