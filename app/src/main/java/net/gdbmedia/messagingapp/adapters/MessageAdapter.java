package net.gdbmedia.messagingapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.gdbmedia.messagingapp.R;
import net.gdbmedia.messagingapp.models.Message;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Guest on 7/14/16.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
private List<Message> mMessages = new ArrayList<>();
private Context mContext;
private String mCurrentUserId;

public MessageAdapter(Context context, List<Message> messages, String currentUserId){
        this.mMessages = messages;
        this.mContext = context;
        this.mCurrentUserId = currentUserId;
        }

@Override
public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        }

@Override
public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_list_item, viewGroup, false);
        MessageViewHolder mvh = new MessageViewHolder(v);
        return mvh;
        }

@Override
public void onBindViewHolder(MessageViewHolder messageViewHolder, int i) {
        messageViewHolder.time.setText(mMessages.get(i).getTimestamp());
        messageViewHolder.content.setText(mMessages.get(i).getContent());
        if (!mMessages.get(i).getUid().equals(mCurrentUserId)){
            messageViewHolder.mMessageArea.setBackgroundColor(Color.GREEN);
            }
        }


@Override
public int getItemCount() {
        return mMessages.size();
        }

public class MessageViewHolder extends RecyclerView.ViewHolder{

    @Bind(R.id.timeOfMessage) TextView time;
    @Bind(R.id.content) TextView content;
    @Bind(R.id.messageArea) RelativeLayout mMessageArea;

    MessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }

}
}


