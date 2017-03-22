package com.paspanaassarasee.spiltbill;

/**
 * Created by paspanaassarasee on 3/22/17.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

//    private SwipeRefreshLayout swipeContainer;

    final Context context = this;

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int RC_SIGN_IN = 1;

    private Button btnPrompt,btnSumUp,btnClearAll;
    private Calendar c;
    private SimpleDateFormat df;

    private ListView mCardListView;
    private CardAdapter mCardAdapter;
    private String mUsername;
    private String photoURL;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseUser user;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private List<Card> cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseCrash.log("Let's see");
        FirebaseCrash.report(new Exception("Oops!"));

        mUsername = ANONYMOUS;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        btnPrompt = (Button) findViewById(R.id.prompt);
        btnSumUp = (Button) findViewById(R.id.sumUp);
        btnClearAll = (Button) findViewById(R.id.clearAll);

        mCardListView = (ListView) findViewById(R.id.messageListView);

        cardList = new ArrayList<>();
        mCardAdapter = new CardAdapter(this, R.layout.item_message, cardList);
        mCardListView.setAdapter(mCardAdapter);

        c = Calendar.getInstance();
        df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        btnPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                final EditText titleEditText = (EditText) promptsView.findViewById(R.id.titleEditText);
                final EditText detailsEditText = (EditText) promptsView.findViewById(R.id.detailsEditText);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String currentDateandTime = df.format(new Date());

                                        String textTitle = titleEditText.getText().toString();
                                        String textDetails = detailsEditText.getText().toString();

                                        if (textTitle.length() == 0) {
                                            textTitle = "No Title";
                                        }
                                        if (textDetails.length() == 0) {
                                            textDetails = "No Details";
                                        }

                                        Card friendlyMessage = new Card(textTitle,textDetails,mUsername,currentDateandTime,photoURL);
//                                        Toast.makeText(MainActivity.this,friendlyMessage.getPhotoUrl(),Toast.LENGTH_SHORT).show();

                                        mMessagesDatabaseReference.push().setValue(friendlyMessage);
                                        // Clear input box
                                        titleEditText.setText("");
                                        detailsEditText.setText("");
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        btnSumUp.setOnClickListener(new View.OnClickListener() {
            Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
            Map<String,Double> result=new HashMap<String,Double>();
            @Override
            public void onClick(View view) {
                int listSize = cardList.size();
                double totalPrice = 0;

                for (int i = 0; i<listSize; i++){
                    String amoungStr = cardList.get(i).getDetails();
                    String nameStr = cardList.get(i).getName();
                    int amoung = Integer.parseInt(amoungStr);

//                    Log.i("Set ", nameStr);
//                    Log.i("Set ", amoungStr);
                    totalPrice += amoung;

                    add(nameStr,amoung);

                }
//                Toast.makeText(MainActivity.this,"Total :"+totalPrice,Toast.LENGTH_SHORT).show();
                Log.i("Set ", "Total Member "+map.size());
                for(Map.Entry<String, List<Integer>> entry: map.entrySet()) {
//                    Log.i("Set", entry.getKey() + " = " + entry.getValue());
                    int i;
                    double sum = 0;
                    for(i = 0; i < entry.getValue().size(); i++) {
                        sum += entry.getValue().get(i);
//                        Log.i("Set", ""+entry.getValue().get(i));
                    }
//                    Log.i("Set ", entry.getKey()+" pay "+sum+" Transaction : "+entry.getValue());
                    result.put(entry.getKey(),sum);
                }
                Log.i("Set ", "TotalPrice "+totalPrice);
                double dividePrice = totalPrice/map.size();
                Log.i("Set ", dividePrice+" Per Each");

                for(Map.Entry m:result.entrySet()){
//                    System.out.println(m.getKey()+" "+m.getValue());
                    double v = (Double) m.getValue();
                    Log.i("Set ", m.getKey()+" pay "+v);
                    Log.i("Set ", "So, "+m.getKey()+" have to pay "+(v - dividePrice));
                }

                map.clear();
                result.clear();
            }

            public void add(String key, Integer val) {
                List<Integer> list = map.get(key);
                if (list == null) {
                    list = new ArrayList<Integer>();
                    map.put(key, list);
                }
                list.add(val);
            }
        });

        btnClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int listSize = cardList.size();
                for (int i = listSize-1; i>=0; i--){
                    remove(cardList.get(i));
                    cardList.remove(i);
                    mCardAdapter.notifyDataSetChanged();
                }
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //User is sign in
                    onSignInInitialize(user.getDisplayName(),user.getPhotoUrl().toString());
                }else{
                    //User is sign out
                    onSignOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.TWITTER_PROVIDER,
                                            AuthUI.FACEBOOK_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mCardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.details, null);

                CircleImageView photoImageViewDetail = (CircleImageView) mView.findViewById(R.id.photoImageViewDetail);
                TextView titleDetail = (TextView) mView.findViewById(R.id.titleDetail);
                TextView detailDetail = (TextView) mView.findViewById(R.id.detailDetail);
                TextView usernameDetail = (TextView) mView.findViewById(R.id.usernameDetail);
                TextView timeDetail = (TextView) mView.findViewById(R.id.timeDetail);
                final Button dismiss = (Button) mView.findViewById(R.id.dismissDetail);

                //Load Image Profile
                photoImageViewDetail.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(cardList.get(position).getPhotoUrl())
                        .override(150,150)
                        .into(photoImageViewDetail);

                //Set Details
                titleDetail.setText(cardList.get(position).getTitle());
                detailDetail.setText(cardList.get(position).getDetails());
                usernameDetail.setText(cardList.get(position).getName());
                timeDetail.setText(cardList.get(position).getTime());

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
            }
        });

        mCardListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete card")
                        .setMessage("Are you sure you want to delete this card?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                remove(cardList.get(i));
                                cardList.remove(i);
                                mCardAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    public void remove(Card card){
        mMessagesDatabaseReference.child(card.getKey()).removeValue();
    }

    public void update(Card card,String newTitle){
        card.setTitle(newTitle);
        mMessagesDatabaseReference.child(card.getKey()).setValue(card);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this,"Sign in",Toast.LENGTH_SHORT).show();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this,"Sign in canceled",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
        mCardAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void onSignInInitialize(String username, String photoURI){
        mUsername = username;
        photoURL = photoURI;
        attachDatabaseReadListener();
    }

    private void onSignOutCleanup(){
        mUsername = ANONYMOUS;
        mCardAdapter.clear();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener(){
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Card friendlyMessages = dataSnapshot.getValue(Card.class);
                    friendlyMessages.setKey(dataSnapshot.getKey());
                    mCardAdapter.add(friendlyMessages);
                    mCardAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    String key = dataSnapshot.getKey();
                    Card updateCard = dataSnapshot.getValue(Card.class);
                    for(Card mm : cardList){
                        if(mm.getKey().equals(key)){
                            mm.setValue(updateCard);
                            mCardAdapter.notifyDataSetChanged();
                        }
                    }

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String key = dataSnapshot.getKey();
                    for(Card mm : cardList){
                        if(mm.getKey().equals(key)){
                            cardList.remove(mm);
                            mCardAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);

        }
    }

    private void detachDatabaseReadListener(){
        if(mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}

