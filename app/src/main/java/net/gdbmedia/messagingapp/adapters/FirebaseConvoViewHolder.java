package net.gdbmedia.messagingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.gdbmedia.messagingapp.R;
import net.gdbmedia.messagingapp.models.Conversation;

/**
 * Created by Guest on 7/14/16.
 */
public class FirebaseConvoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;

    View mView;
    Context mContext;

    public FirebaseConvoViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindConvo(Conversation conversation) {
        TextView usernameTextView = (TextView) mView.findViewById(R.id.usernameTextView);
        TextView timeTextView = (TextView) mView.findViewById(R.id.lastMessageTime);
        TextView messageTextView = (TextView) mView.findViewById(R.id.lastMessage);


        usernameTextView.setText(conversation.getOtherUser());
        timeTextView.setText(conversation.getLastMessageTime());
        messageTextView.setText(conversation.getLastMessage());
    }

    @Override
    public void onClick(View view) {

    }
}
