package org.rubychinaandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.activity.MainActivity;
import org.rubychinaandroid.activity.PostActivity;
import org.rubychinaandroid.model.ReplyModel;
import org.rubychinaandroid.model.TopicModel;
import org.rubychinaandroid.utils.RubyChinaConstants;
import org.rubychinaandroid.utils.Utility;
import org.rubychinaandroid.view.FootUpdate.OnScrollToBottomListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReplyItemAdapter extends RecyclerView.Adapter<ReplyItemAdapter.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<ReplyModel> mReplyList;
    private OnScrollToBottomListener mListener;

    public ReplyItemAdapter(Context context, ArrayList<ReplyModel> replyList, OnScrollToBottomListener listener) {
        mContext = context;
        mReplyList = replyList;
        mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_reply, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ReplyModel reply = mReplyList.get(position);

        holder.content.setText(reply.getBodyHtml());

        /* 2. load author and publish time */
        holder.time.setText(reply.getCreatedTime());

        /* 3. load avatar */
        String userAvatarUrl = reply.getUserAvatarUrl();
        ImageLoader.getInstance().displayImage(userAvatarUrl, holder.avatar, MyApplication.imageLoaderOptions);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert (mContext instanceof MainActivity);
                MainActivity activity = (MainActivity) mContext;
                Intent intent = new Intent(activity, PostActivity.class);
                Log.d("ReplyItemAdapter", reply.getTopicId());
                intent.putExtra(RubyChinaConstants.TOPIC_ID, reply.getTopicId());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });

        if (mReplyList.size() - position <= 1 && mListener != null) {
            mListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        return mReplyList == null ? 0 : mReplyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView replier;
        public ImageView avatar;
        public TextView content;
        public TextView time;

        ViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.card_container);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            replier = (TextView) view.findViewById(R.id.replier);
            content = (TextView) view.findViewById(R.id.content);
            time = (TextView) view.findViewById(R.id.time);
        }
    }

    /*
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


        ImageLoader.getInstance().displayImage(reply.getUserAvatarUrl(), viewHolder.avatar, MyApplication.imageLoaderOptions);


        String replier = reply.getUserName();
        replier = ("".equals(replier) ? reply.getUserLogin() : replier);
        viewHolder.replier.setText(replier);


        viewHolder.replyTime.setText("创建于 " + reply.getCreatedTime());


        viewHolder.replyContent.setText(Html.fromHtml(reply.getBodyHtml()));

        return view;
    }
    */
}
