package org.rubychinaandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.rubychinaandroid.MyApplication;
import org.rubychinaandroid.R;
import org.rubychinaandroid.activity.ProfileActivity;
import org.rubychinaandroid.activity.ReplyActivity;
import org.rubychinaandroid.fragments.ReplyItemOnClickListener;
import org.rubychinaandroid.model.ReplyModel;
import org.rubychinaandroid.utils.RubyChinaArgKeys;
import org.rubychinaandroid.view.RichTextView;

import java.util.ArrayList;

public class ReplyItemAdapter extends RecyclerView.Adapter<ReplyItemAdapter.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<ReplyModel> mReplyList;
    private ReplyItemOnClickListener mClickListener;

    public ReplyItemAdapter(Context context, ArrayList<ReplyModel> replyList, ReplyItemOnClickListener onClickListener) {
        mContext = context;
        assert (mContext instanceof ReplyActivity);
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
        final String userName = reply.getUserName();
        final String userLogin = reply.getUserLogin();
        final String replierName = "".equals(userName) ? userLogin : userLogin + "(" + userName + ")";
        holder.replier.setText(replierName);
        holder.content.setRichText(reply.getBodyHtml(), true);
        holder.time.setText(reply.getCreatedTime());
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra(RubyChinaArgKeys.USER_LOGIN, userLogin);
                mContext.startActivity(intent);
                ((ReplyActivity) mContext).
                        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });

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

        holder.floorNumber.setText("#" + floor);
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
        public TextView floorNumber;

        ViewHolder(View view) {
            super(view);

            avatar = (ImageView) view.findViewById(R.id.avatar);
            replier = (TextView) view.findViewById(R.id.replier);
            content = (RichTextView) view.findViewById(R.id.content);
            time = (TextView) view.findViewById(R.id.time);
            floorNumber = (TextView) view.findViewById(R.id.floorNumber);
        }
    }
}
