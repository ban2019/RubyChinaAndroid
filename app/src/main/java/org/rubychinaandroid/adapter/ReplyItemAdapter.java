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
import org.rubychinaandroid.view.RichTextView;

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

        holder.replier.setText("".equals(reply.getUserName()) ? reply.getUserLogin() : reply.getUserName());
        holder.content.setRichText(reply.getBodyHtml(), true);
        holder.time.setText(reply.getCreatedTime());

        String userAvatarUrl = reply.getUserAvatarUrl();
        ImageLoader.getInstance().displayImage(userAvatarUrl, holder.avatar, MyApplication.imageLoaderOptions);

        if (mReplyList.size() - position <= 1 && mListener != null) {
            mListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        return mReplyList == null ? 0 : mReplyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView replier;
        public ImageView avatar;
        public RichTextView content;
        public TextView time;

        ViewHolder(View view) {
            super(view);

            avatar = (ImageView) view.findViewById(R.id.avatar);
            replier = (TextView) view.findViewById(R.id.replier);
            content = (RichTextView) view.findViewById(R.id.content);
            time = (TextView) view.findViewById(R.id.time);
        }
    }
}
