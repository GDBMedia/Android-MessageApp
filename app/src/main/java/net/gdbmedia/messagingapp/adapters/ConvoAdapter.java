package net.gdbmedia.messagingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gdbmedia.messagingapp.R;
import net.gdbmedia.messagingapp.models.Conversation;
import net.gdbmedia.messagingapp.models.User;
import net.gdbmedia.messagingapp.ui.ChatActivity;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Guest on 7/14/16.
 */
public class ConvoAdapter extends RecyclerView.Adapter<ConvoAdapter.ConvoViewHolder> {
    private List<Conversation> mConversations = new ArrayList<>();
    private Context mContext;

    public ConvoAdapter(Context context, List<Conversation> conversations){
        this.mConversations = conversations;
        this.mContext = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ConvoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.convo_list_item, viewGroup, false);
        ConvoViewHolder cvh = new ConvoViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(ConvoViewHolder convoViewHolder, int i) {
        convoViewHolder.usernameTextView.setText(mConversations.get(i).getOtherUser());
        convoViewHolder.timeTextView.setText(mConversations.get(i).getLastMessageTime());
        convoViewHolder.messageTextView.setText(mConversations.get(i).getLastMessage());
    }


    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    public class ConvoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.usernameTextView) TextView usernameTextView;
        @Bind(R.id.lastMessageTime) TextView timeTextView;
        @Bind(R.id.lastMessage) TextView messageTextView;

        ConvoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int itemPosition = getLayoutPosition();
            Intent intent = new Intent(view.getContext(), ChatActivity.class);
            intent.putExtra("convo", Parcels.wrap(mConversations.get(itemPosition)));
            mContext.startActivity(intent);
        }
    }
}

