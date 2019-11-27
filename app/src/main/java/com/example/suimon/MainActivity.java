package com.example.suimon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ArcMotion;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.transitionseverywhere.extra.Scale;

import static com.example.suimon.App.CHANNEL_1_ID;


public class MainActivity extends AppCompatActivity {

    //Kontrol
    SeekBar seek_bar;

    //database
    TextView distance, distanceUp;
    DatabaseReference dref;
    String status, adwalstatus, progress_dbstr;
    int progress_db, waterlvl_db ;
    FirebaseUser user;
    //transisi
    ViewGroup  tContainer;
    TextView fromtext, distext,cmtext, jaraktext,penjelasan1, penjelasan2, penjelasan3, keterangan1, presentase, persen;
    ImageView img,waterup, water;
    ImageButton moreButton, profile;

    //transisi profil

    TextView profile1, profile2, profile3;
    ImageView kotakutama, foto, logout;

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationManager = NotificationManagerCompat.from(this);


       final ImageButton btnTest = findViewById(R.id.imageButtonadwal);
       final boolean[] flag = {false, false};
       tContainer = findViewById(R.id.transitionContainer);
       img = findViewById(R.id.imageViewMore);
       waterup = findViewById(R.id.imageViewWaterUp);
       water = findViewById(R.id.imageView2);
       fromtext= findViewById(R.id.textView3Up);
       distext= findViewById(R.id.textView6Up);
       cmtext = findViewById(R.id.textViewCmUp);
       jaraktext = findViewById(R.id.textViewUsUp);
       penjelasan1 = findViewById(R.id.textView4);
       penjelasan2 = findViewById(R.id.textViewExp1);
       penjelasan3 = findViewById(R.id.textViewExp2);
       keterangan1 = findViewById(R.id.textViewHowmuch);
       presentase = findViewById(R.id.textViewPresentase);
       persen = findViewById(R.id.textViewPersen);
       seek_bar = findViewById(R.id.seekBar);
       logout = findViewById(R.id.imageLogout);
       moreButton = findViewById(R.id.imageButtonMore);
       profile1 = findViewById(R.id.textProfile);
       profile2 = findViewById(R.id.textProfile2);
       profile3 = findViewById(R.id.textProfile3);
       kotakutama = findViewById(R.id.imageView);
       foto = findViewById(R.id.imageViewPhoto);
       profile = findViewById(R.id.imageButtonProfile);


       profile.setOnClickListener(new View.OnClickListener() {
           boolean visibleProfile;
           @Override
           public void onClick(View viewProfile) {
                TransitionManager.beginDelayedTransition(tContainer, new Fade());
                visibleProfile =!visibleProfile;
                profile1.setVisibility(visibleProfile ? View.VISIBLE : View.GONE);
                profile2.setVisibility(visibleProfile ? View.VISIBLE : View.GONE);
                profile3.setVisibility(visibleProfile ? View.VISIBLE : View.GONE);
                foto.setVisibility(visibleProfile ? View.VISIBLE : View.GONE);
                kotakutama.setVisibility(visibleProfile ? View.VISIBLE : View.GONE);
                logout.setVisibility(visibleProfile ? View.VISIBLE : View.GONE);


           }
       });

       logout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               FirebaseAuth.getInstance().signOut();
               Intent i = new Intent(MainActivity.this, LoginActivity.class);
               i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(i);
               finish();
           }
       });

       moreButton.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(tContainer, new Slide(Gravity.BOTTOM));
                visible = !visible;
                img.setVisibility(visible ? View.VISIBLE : View.GONE);
                waterup.setVisibility(visible ? View.VISIBLE : View.GONE);
                fromtext.setVisibility(visible ? View.VISIBLE : View.GONE);
                distext.setVisibility(visible ? View.VISIBLE : View.GONE);
                cmtext.setVisibility(visible ? View.VISIBLE : View.GONE);
                jaraktext.setVisibility(visible ? View.VISIBLE : View.GONE);
                penjelasan1.setVisibility(visible ? View.VISIBLE : View.GONE);
                penjelasan2.setVisibility(visible ? View.VISIBLE : View.GONE);
                penjelasan3.setVisibility(visible ? View.VISIBLE : View.GONE);
                keterangan1.setVisibility(visible ? View.VISIBLE : View.GONE);
                persen.setVisibility(visible ? View.VISIBLE : View.GONE);
                presentase.setVisibility(visible ? View.VISIBLE : View.GONE);
                seek_bar.setVisibility(visible ? View.VISIBLE : View.GONE);

                if(!flag[1]){
                    moreButton.setImageResource(R.drawable.less);
                    flag[1] = true;
                }else if (flag[1]){
                    moreButton.setImageResource(R.drawable.more);
                    flag[1] = false;
                }

            }

        });

        distance = (TextView)findViewById(R.id.textViewUs);
        distanceUp = (TextView)findViewById(R.id.textViewUsUp);
        dref= FirebaseDatabase.getInstance().getReference();
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //ultrasonic
                status=dataSnapshot.child("ultrasonik").getValue().toString();
                distance.setText(status);
                distanceUp.setText(status);
                waterlvl_db = Integer.parseInt(status);

                //adwal
                adwalstatus = dataSnapshot.child("adwal").getValue().toString();
                if(adwalstatus.equals("1") ){
                    btnTest.setImageResource(R.drawable.adwalon);
                    flag[0] = true;

                }else if (adwalstatus.equals("0")){
                    btnTest.setImageResource(R.drawable.adwal);
                    flag[0] = false;

                }
                //controll
                progress_dbstr = dataSnapshot.child("gate").getValue().toString();
                progress_db = Integer.parseInt(progress_dbstr);
                //distance.setText(progress_dbstr);
                seek_bar.setProgress(progress_db);

                ///watercolorchange
                if(waterlvl_db < 10){
                    waterup.setImageResource(R.drawable.water_red);
                    water.setImageResource(R.drawable.water_red);
                }else{
                    waterup.setImageResource(R.drawable.water);
                    water.setImageResource(R.drawable.water);
                }

                //notif
                if(adwalstatus.equals("0")) {
                    if(progress_db < 50) {
                        if (waterlvl_db < 10) {
                            addNotification();

                        }
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"Please Connect to Internet", Toast.LENGTH_LONG).show();
            }
        });


        presentase.setText(""+seek_bar.getProgress()+"");
        seek_bar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress_value;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        progress_value = i;
                        presentase.setText("" + progress_value+ "");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        presentase.setText("" + progress_value+ "");
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRefGate = database.getReference("gate");
                        myRefGate.setValue(progress_value);
                        presentase.setText("" + progress_value+ "");

                    }
                }
        );




        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTest.setSelected(!btnTest.isPressed());
                if (btnTest.isPressed()) {
                    if(!flag[0]){
                        btnTest.setImageResource(R.drawable.adwalon);
                        flag[0] = true;
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("adwal");
                        myRef.setValue(1);


                    }else if(flag[0]){
                        btnTest.setImageResource(R.drawable.adwal);
                        flag[0] = false;
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("adwal");
                        myRef.setValue(0);
                    }

                }

            }
        });



       /* final ImageButton btnMore = findViewById(R.id.imageButtonMore);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewMore) {
                btnMore.setSelected(!btnMore.isPressed());

                if (btnMore.isPressed()) {
                    if(!flag[1]){
                        btnMore.setImageResource(R.drawable.less);
                        flag[1] = true;
                    }else if (flag[1]){
                        btnMore.setImageResource(R.drawable.more);
                        flag[1] = false;
                    }

                }
                else {
                    btnMore.setImageResource(R.drawable.less);
                }
            }
        });
*/


    }

    public void addNotification() {

        Intent activityIntent = new Intent(this, LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Bitmap largeicon = BitmapFactory.decodeResource(getResources(),R.drawable.water_red);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Suimon Allert!")
                .setContentText("Hey Suimon Patrol, you have to control the Floodgate immediately!")
                .setLargeIcon(largeicon)
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText("Hey Suimon Patrol, you have to control the Floodgate immediately!")
                    .setBigContentTitle("it's " +status+ " cm from surface!")
                        .setSummaryText("Suimon sense Allert")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();
        notificationManager.notify(1, notification);

    }


}