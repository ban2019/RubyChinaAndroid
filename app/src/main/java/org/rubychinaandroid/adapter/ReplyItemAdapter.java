package org.rubychinaandroid.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.fragments.ReplyItemOnClickListener;
import org.rubychinaandroid.model.ReplyModel;
import org.rubychinaandroid.view.RichTextView;

import java.util.ArrayList;

public class ReplyItemAdapter extends RecyclerView.Adapter<ReplyItemAdapter.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<ReplyModel> mReplyList;
    private ReplyItemOnClickListener mClickListener;

    public ReplyItemAdapter(Context context, ArrayList<ReplyModel> replyList, ReplyItemOnClickListener onClickListener) {
        mContext = context;
        mReplyList = replyList;
        mLayoutInflater = LayoutInflater.from(context);
        mClickListener = onClickListener;
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
        ImageLoader.getInstance().displayImage(userAvatarUrl, holder.avatar,
                MyApplication.getInstance().getImageLoaderOptions());

        final int floor = position + 1;
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onClick(floor, reply.getUserLogin());
            }
        });
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
