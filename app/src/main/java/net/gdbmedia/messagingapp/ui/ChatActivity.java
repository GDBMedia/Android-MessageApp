package net.gdbmedia.messagingapp.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import net.gdbmedia.messagingapp.R;
import net.gdbmedia.messagingapp.models.Conversation;
import net.gdbmedia.messagingapp.models.Message;
import net.gdbmedia.messagingapp.models.User;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{
    private User mSearchedUser;
    private DatabaseReference mPostReference;
    private User mCurrentUser;

    private DatabaseReference mDatabase;


    @Bind(R.id.send) ImageButton mSend;
    @Bind(R.id.message) EditText mMessage;
    private SharedPreferences mSharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Gson gson = new Gson();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mSharedPreferences.getString("currentUser", null);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Log.d("nothing", json.length() +"");
        mCurrentUser = gson.fromJson(json, User.class);

        Log.d("nothing", mCurrentUser.getName());

        ButterKnife.bind(this);

        mSearchedUser = Parcels.unwrap(getIntent().getParcelableExtra("searchedUser"));

        getSupportActionBar().setTitle(mSearchedUser.getName());
        mSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mSend){

            DatabaseReference mConversationPushReference = mDatabase.push();
            String key = mConversationPushReference.getKey();
            String keym = mDatabase.push().getKey();

            String message = mMessage.getText().toString();
            Message newMessage = new Message( keym, message,  mCurrentUser.getId(), (new Date()).toString(), key);


            Conversation newConversation = new Conversation(new ArrayList<String>(), key, new ArrayList<String>());
            newConversation.addMessage(newMessage.getId());
            newConversation.addUser(mSearchedUser.getId());
            newConversation.addUser(mCurrentUser.getId());

            mSearchedUser.addConversationId(newConversation.getId());
            mCurrentUser.addConversationId(newConversation.getId());

            Map<String, Object> searchedValues = mSearchedUser.toMap();
            Map<String, Object> currentValues = mCurrentUser.toMap();
            Map<String, Object> conversationValues = newConversation.toMap();
            Map<String, Object> messageValues = newMessage.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/users/" + mSearchedUser.getId(), searchedValues);
            childUpdates.put("/users/" + mCurrentUser.getId(), currentValues);
            childUpdates.put("/conversations/" + key, conversationValues);
            childUpdates.put("/messages/" + keym, messageValues);

            mDatabase.updateChildren(childUpdates);
        }
    }
}
