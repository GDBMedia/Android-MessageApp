package net.gdbmedia.messagingapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import net.gdbmedia.messagingapp.R;
import net.gdbmedia.messagingapp.adapters.ConvoAdapter;
import net.gdbmedia.messagingapp.adapters.MessageAdapter;
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
    public final String TAG = this.getClass().getSimpleName();
    private User mSearchedUser;
    private DatabaseReference mPostReference;
    private User mCurrentUser;

    private DatabaseReference mDatabase;
    private DatabaseReference mUsersReference;

    private List<Message> mMessages = new ArrayList<>();


    @Bind(R.id.send) ImageButton mSend;
    @Bind(R.id.message) EditText mMessage;
    @Bind(R.id.messages) RecyclerView mRecyclerView;
    private SharedPreferences mSharedPreferences;
    private Conversation mConvo;
    private String mConvoKey;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        Gson gson = new Gson();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mSharedPreferences.getString("currentUser", null);
        mCurrentUser = gson.fromJson(json, User.class);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        mConvo = Parcels.unwrap(getIntent().getParcelableExtra("convo"));

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);

        try{
            getMessages();
        }catch (NullPointerException e){
            Log.d(TAG, "onCreate: " + e);
        }




        mSearchedUser = Parcels.unwrap(getIntent().getParcelableExtra("searchedUser"));
        if(mSearchedUser == null){
            getSearchedUser();
        }else{
            getSupportActionBar().setTitle(mSearchedUser.getName());
        }


        mSend.setOnClickListener(this);
    }

    private void getMessages() {
        mPostReference = FirebaseDatabase.getInstance().getReference("messages");
        Log.d(TAG, "getMessages: "+ mConvo.getId());
        Query queryRef = mPostReference.orderByChild("convoId").equalTo(mConvo.getId());
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                mMessages.add(snapshot.getValue(Message.class));
                setAdapter();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
    }

    private void setAdapter() {
        ChatActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                MessageAdapter adapter = new MessageAdapter(ChatActivity.this, mMessages, mCurrentUser.getId());
                mRecyclerView.setAdapter(adapter);
            }
        });
    }

    private void getSearchedUser() {
        String otherUserId = mConvo.getOtherUserId();
        Log.d(TAG, "getSearchedUser: " + otherUserId);
        mUsersReference = FirebaseDatabase.getInstance().getReference("users");
        Query queryRef = mUsersReference.child(otherUserId);

        queryRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mSearchedUser = dataSnapshot.getValue(User.class);
                        Log.d(TAG, "onDataChange: " + mSearchedUser.getName());
                        getSupportActionBar().setTitle(mSearchedUser.getName());

                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private boolean testMConvo() {
        if(mConvo == null){
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v == mSend){


            Map<String, Object> childUpdates = new HashMap<>();

            if(mConvo == null){
                mConvoKey = mDatabase.child("conversations").push().getKey();
                mConvo = new Conversation(new ArrayList<String>(), mConvoKey, new ArrayList<String>());



                mConvo.addUser(mSearchedUser.getId());

                Log.d(TAG, "onClick: " +mSearchedUser.getId());
                mConvo.addUser(mCurrentUser.getId());

                mSearchedUser.addConversationId(mConvo.getId());
                mCurrentUser.addConversationId(mConvo.getId());

                Map<String, Object> searchedValues = mSearchedUser.toMap();
                Map<String, Object> currentValues = mCurrentUser.toMap();


                childUpdates.put("/users/" + mSearchedUser.getId(), searchedValues);
                childUpdates.put("/users/" + mCurrentUser.getId(), currentValues);
            }

            String keym = mDatabase.child("messages").push().getKey();

            String message = mMessage.getText().toString();
            mMessage.setText("");
            Message newMessage = new Message( keym, message,  mCurrentUser.getId(), (new Date()).toString(), mConvo.getId());



            mConvo.addMessage(newMessage.getId());



            Map<String, Object> conversationValues = mConvo.toMap();
            Map<String, Object> messageValues = newMessage.toMap();


            childUpdates.put("/conversations/" + mConvoKey, conversationValues);
            childUpdates.put("/messages/" + keym, messageValues);

            mDatabase.updateChildren(childUpdates);
        }
    }
}
