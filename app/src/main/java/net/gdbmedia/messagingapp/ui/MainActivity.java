package net.gdbmedia.messagingapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import net.gdbmedia.messagingapp.adapters.FirebaseConvoViewHolder;
import net.gdbmedia.messagingapp.models.Conversation;
import net.gdbmedia.messagingapp.models.Message;
import net.gdbmedia.messagingapp.models.User;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mPostReference;
    private User mSearcheUser;
    private User mCurrentUser;
    private String otherUserId;
    private String otherUsername;
    private String lastMessage;
    private String lastMessageTime;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private List<Conversation> mConversations= new ArrayList<>();

    private DatabaseReference mConversationsReference;
    private DatabaseReference mUsersReference;
    private DatabaseReference mMessagesReference;

    private FirebaseRecyclerAdapter mFirebaseAdapter;

    @Bind(R.id.convoList) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Gson gson = new Gson();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        String json = mSharedPreferences.getString("currentUser", null);
        mCurrentUser = gson.fromJson(json, User.class);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);


        mUsersReference = FirebaseDatabase.getInstance().getReference("users");
        mMessagesReference = FirebaseDatabase.getInstance().getReference("messages");

        Query queryRef = mUsersReference.child(mCurrentUser.getId());

        queryRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        mCurrentUser = dataSnapshot.getValue(User.class);
                        Gson gson = new Gson();
                        String json = gson.toJson(mCurrentUser);
                        mEditor.putString("currentUser", json).apply();

                        try{
                            getConvos();
                        } catch (NullPointerException e){
                            Log.d(TAG, "onDataChange: "+ e);
                        }


                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
//        setUpFirebaseAdapter();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    getSupportActionBar().setTitle("Welcome, " + user.getDisplayName() + "!");
                } else {

                }
            }
        };


    }

    private void getConvos() {
        mConversationsReference = FirebaseDatabase.getInstance().getReference("conversations");
        for (int i = 0; i < mCurrentUser.getConversationIds().size(); i++) {
            Query queryRef = mConversationsReference.child(mCurrentUser.getConversationIds().get(i));
            queryRef.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            Conversation conversation = dataSnapshot.getValue(Conversation.class);
                            int indexOfCurrentUser = conversation.getUserIds().indexOf(mCurrentUser.getId());
                            for(int i=0; i<conversation.getUserIds().size(); i++){
                                //if its not the user, set otherUserId (
                                if(conversation.getUserIds().indexOf(conversation.getUserIds().get(i)) != indexOfCurrentUser){
                                    otherUserId = conversation.getUserIds().get(i);
                                }
                            }
                            getOtherUsername(conversation.getMessages().get(conversation.getMessages().size() -1));

                            // ...
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }
        }

    private void getOtherUsername(final String lastMessageId) {
        Query queryRef = mUsersReference.child(otherUserId);

        queryRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       String name = dataSnapshot.getValue(User.class).getName();
                        Log.d(TAG, "onDataChange: " + otherUsername);
                        getMessageDeets(lastMessageId, name);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private void getMessageDeets(final String lastMessageId, final String name) {
        mMessagesReference = FirebaseDatabase.getInstance().getReference("messages");

        Query queryRef = mMessagesReference.child(lastMessageId);

        queryRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        lastMessage = dataSnapshot.getValue(Message.class).getContent();
                        lastMessageTime = dataSnapshot.getValue(Message.class).getTimestamp();
                        Log.d(TAG, "Last Message: " + lastMessage);
                        Log.d(TAG, "last Message time: " + lastMessageTime);

                        Conversation conversation = new Conversation(name, lastMessage, lastMessageTime);
                        mConversations.add(conversation);
                        setAdapter();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private void setAdapter() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ConvoAdapter adapter = new ConvoAdapter(MainActivity.this, mConversations);
                mRecyclerView.setAdapter(adapter);
            }
        });

    }


//    private void setUpFirebaseAdapter() {
//        mFirebaseAdapter = new FirebaseRecyclerAdapter<Conversation, FirebaseConvoViewHolder>
//                (Conversation.class, R.layout.convo_list_item, FirebaseConvoViewHolder.class, mConversationsReference) {
//
//            @Override
//            protected void populateViewHolder(FirebaseConvoViewHolder viewHolder,
//                                              Conversation model, int position) {
//                viewHolder.bindConvo(model);
//            }
//        };
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setAdapter(mFirebaseAdapter);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mFirebaseAdapter.cleanup();
//    }

    private void getUser(String query){
        mPostReference = FirebaseDatabase.getInstance().getReference("users");
        Query queryRef = mPostReference.orderByChild("email").equalTo(query);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                mSearcheUser = snapshot.getValue(User.class);
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("searchedUser", Parcels.wrap(mSearcheUser));
                startActivity(intent);
//                Log.d("name", mSearcheUser.getName());

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);


        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!query.equals(mCurrentUser.getEmail())){
                    getUser(query);
                }else{
                    Toast.makeText(getApplicationContext(), "You Can't Message Yourself", Toast.LENGTH_LONG).show();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
