package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.tictactoe.R.raw.gaming;
import static com.example.tictactoe.R.raw.song;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener , View.OnClickListener{

    RadioGroup rGroup ;
    EditText etX, etO ;
    Button [] btActivity;
    SharedPreferences sp;
    int rgid =0 ;
    String user1 , user2 ;
    Button btBack;
    SharedPreferences.Editor  editor;
    String mode = "human";
    int counter = 0;
    MediaPlayer ring ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rGroup = findViewById(R.id.rGroup);
        etX = findViewById(R.id.etX);
        etO = findViewById(R.id.etO);
        btActivity = new Button[3];

        btActivity[1] = findViewById(R.id.btStart);
        btActivity[2] = findViewById(R.id.btAbout);
        btActivity[0] = findViewById(R.id.btplay);
        for (Button bt:btActivity)
        {
            bt.setOnClickListener(this);
            bt.setBackground(getDrawable(R.drawable.b2));
        }

        Intent intent=getIntent();//get keys from last page. for counter
        if(intent.getExtras()!=null )//for situation intent empty And may cause a crash
            counter = intent.getIntExtra("keyCounter", 1);//get counter from main2

//        Toast.makeText(this,"counter = " + counter,Toast.LENGTH_LONG);
play();//play, pause or reset music

        sp = getSharedPreferences("details", MODE_PRIVATE);//create file for save my keys
        rgid = sp.getInt("keyRg", rgid);//get radio group key
        rGroup.check(rgid);//set the radio chacked
        rGroup.setOnCheckedChangeListener(this);

//get key users to variable for set on the edit text
        user1 = sp.getString("key_user1" , "");
        etX.setText(user1);
        user2 = sp.getString("key_user2","");
        etO.setText(user2);

// if this is bot mode disable and change txt color and etc'
        if (rgid == R.id.rbBot){
            etO.setHint("Bot Mode");
            etO.setEnabled(false);
            etO.setHintTextColor(Color.RED);
        }
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int Id) {
    sheardPreference(Id);//insert values in details file
    intentsend(Id);//refresh this file
    }

    @Override
    public void onClick(View v) {
        if (v == btActivity[2]){//create page dialog
            createAbout_PageDialog();
        }
        else if(v == btBack){//return to the main page from about page

            sheardPreference(v.getId());
            intentsend(v.getId());

        }
        else  if(v == btActivity[0])
            play();//play or pause

        else
        {
            sheardPreference(v.getId());
            //check if users fill their names
            if (rgid == R.id.rbBot && !etX.getText().toString().equals("")  ||
                    rgid == R.id.rbHuman && !etX.getText().toString().equals("")
                            && !etO.getText().toString().equals("")){
//                ring.release();//release object MediaPlayer
                intentsend(v.getId());
        }
            else alertDialog();
        }
    }

    private void createAbout_PageDialog() {
        Dialog d = new Dialog(this);// initial dialog object
        d.setContentView(R.layout.about_dialog);//apply to use in xml about page
        d.show();//show page
        btBack = d.findViewById(R.id.btBack);
        btBack.setOnClickListener(this);//back button
        }

    private void intentsend(int Id){
            Intent intent;
            if(Id != R.id.btStart){//intent for others back from details and mode game
             intent=new Intent(MainActivity.this,MainActivity.class);}
            else{//intent for start game
                intent=new Intent(MainActivity.this,MainActivity2.class);}
            counter++;
        intent.putExtra("keyCounter", counter);
            if(Id ==  R.id.btStart || Id == R.id.btBack)//intent for all except mode game
            {

                intent.putExtra("keyRg", mode);//keys for transmit to play page
                intent.putExtra("key_user1", etX.getText().toString());
                intent.putExtra("key_user2", etO.getText().toString());
            }
            startActivity(intent);
            finish();
        }

    private void alertDialog(){
            //alert dialog fill name!
            AlertDialog.Builder builder = new AlertDialog.Builder(this);//initial alert dialog
            builder.setMessage("fill name please!");//get question for user
            builder.setCancelable(true);//allowed to cancel way background

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {//button that do nothing for only ok
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    private void sheardPreference(int Id){
            editor = sp.edit();
            if (Id == R.id.rbBot || Id == R.id.rbHuman)//if this check changer
                editor.putInt("keyRg", Id);//put id of check changer
            editor.putString("key_user1", etX.getText().toString());//put key users in details file
            editor.putString("key_user2", etO.getText().toString());
            //for transmite keys and start the game with the mode
            if (Id == R.id.rbBot ||Id == R.id.btStart && rgid == R.id.rbBot) {//for mode bot. delete text from key
                editor.putString("key_user2", "");
                mode = "Bot";
            }
            editor.commit();
        }
        private void play(){
            if(ring == null && counter % 2 == 0){//create new object ring only if ring equals to null and turn to play
            ring = MediaPlayer.create(MainActivity.this, gaming);
            ring.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {//listener if the music complete - reset
                @Override
                public void onCompletion(MediaPlayer mp) {
                     stop();
                     play();
                }
            });
            }
            if (counter % 2 == 0)//algorithm for click for play and pause
            ring.start();
            else pause();
            counter++;// counter for algorithm
        }
    private void pause(){//pause music
        if (ring != null)
        ring.pause();
    }
    private void stop(){//stop music
        if(ring != null){
            ring.release();
            ring = null;;
        }
    }
    @Override
    protected void onStop(){//stop if we exit from page
        super.onStop();
        stop();
    }
}