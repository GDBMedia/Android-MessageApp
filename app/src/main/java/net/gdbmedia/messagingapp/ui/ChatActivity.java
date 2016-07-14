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
    public final String TAG = this.getClass().getSimpleName();
    private User mSearchedUser;
    private DatabaseReference mPostReference;
    private User mCurrentUser;

    private DatabaseReference mDatabase;


    @Bind(R.id.send) ImageButton mSend;
    @Bind(R.id.message) EditText mMessage;
    private SharedPreferences mSharedPreferences;
    private Conversation mConvo;
    private String mConvoKey;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Gson gson = new Gson();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mSharedPreferences.getString("currentUser", null);
        Log.d(TAG, "onCreate: " + json);
        mCurrentUser = gson.fromJson(json, User.class);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        mConvo = Parcels.unwrap(getIntent().getParcelableExtra("convo"));

//       System.out.println(testMConvo());

        ButterKnife.bind(this);

        mSearchedUser = Parcels.unwrap(getIntent().getParcelableExtra("searchedUser"));

        getSupportActionBar().setTitle(mSearchedUser.getName());
        mSend.setOnClickListener(this);
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
