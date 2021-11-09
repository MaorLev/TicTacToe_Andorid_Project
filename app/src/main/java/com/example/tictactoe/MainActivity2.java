package com.example.tictactoe;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import static com.example.tictactoe.R.raw.gaming;


public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    TextView tvXname, tvOname , tvTurn, tvnameTurn;
    Button btNew,btBack,btplay;
    public String Xname ,Oname,gameMode;
    String X = "X";
    String O = "O";
    String [] arrGame;//array of x and O strings values, for compare steps
    Boolean turn = true;
    ImageButton [] arriB;
    int counter = 0;
    MediaPlayer ring;
    int coun3 =0;
    Animation rotateAnimation , rotatebyclick;
    GridLayout grid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        tvOname = findViewById(R.id.tvOname);
        tvXname = findViewById(R.id.tvXname);
        tvTurn = findViewById(R.id.tvTurn);
        tvnameTurn = findViewById(R.id.tvnameTurn);
        btNew = findViewById(R.id.btNew);
        btBack = findViewById(R.id.btBack);
        btplay = findViewById(R.id.btplay);
        btplay.setOnClickListener(this);
        grid = findViewById(R.id.GridLayout1);
        arriB = new ImageButton[9];//array of board buttons
        arriB[0] = findViewById(R.id.iB1);
        arriB[1] =findViewById(R.id.iB2);
        arriB[2] =findViewById(R.id.iB3);
        arriB[3] =findViewById(R.id.iB4);
        arriB[4] =findViewById(R.id.iB5);
        arriB[5] =findViewById(R.id.iB6);
        arriB[6] =findViewById(R.id.iB7);
        arriB[7] =findViewById(R.id.iB8);
        arriB[8] = findViewById(R.id.iB9);
        rotateAnimation = AnimationUtils.loadAnimation(this,R.anim.rotate);
        rotatebyclick = AnimationUtils.loadAnimation(this,R.anim.rotatebyclick);
        for (int i = 0; i<arriB.length;i++) {//apply click listener and background on the central buttons
            arriB[i].setOnClickListener(this);
            arriB[i].setBackground(getDrawable(R.drawable.mark));
        }
        btBack.setOnClickListener(this);
        btNew.setOnClickListener(this);
        arrGame = new String[9];//arrey of string X and O
        tvTurn.setText(X);//show turn
        Intent intent=getIntent();//get keys from last page
        if(intent.getExtras()!=null )//for situation intent empty And may cause a crash
        {

            coun3 = intent.getIntExtra("keyCounter",1);//continue counter from activity1

            play();//play or pause music
            //get game mode and keys and set them in the names
            gameMode = intent.getStringExtra("keyRg");
            Xname=intent.getStringExtra("key_user1");
            tvXname.setText(Xname);
            if(!gameMode.equals("Bot") ) {//if human mode
                Oname = intent.getStringExtra("key_user2");
                tvOname.setText(Oname);
            }
            else {//set txt bot mode and for using turn bot mode in continue
                Oname = gameMode;
                tvOname.setText(Oname);
            }

        }

    }

    private void rotateAnimation() {

//        for (ImageButton bt: arriB)

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        if (v == btNew){

            // if press new game

            newGame("do you want restart this game?");
        }
        else  if(v == btplay){//if click play/pause music
        play();

        }
        else if (v == btBack)
        {//if press back
            newGame("Are you sure,\n so do you want to back and finish the game?");
        }
        else {
            v.startAnimation(rotatebyclick);
            // algoritem for human vs human
            int location = Integer.valueOf(v.getTooltipText().toString());//location of button by tooltip
            if (!Oname.equals("Bot")) {//if this human
                if (turn) {
                    tvTurn.setText(O);// for the next turn change turn name to O
                    arrGame[location] = X;//insert string x to location of button in array for check steps
                    ((ImageButton) v).setImageResource(R.drawable.x);//set image of X in button from drawable
                    ((ImageButton) v).setEnabled(false);//disabled button
                    turn = false;// for enter next turn
                    counter++;//counter for check are buttons full
                } else {
                    // same above only with O
                    tvTurn.setText(X);
                    arrGame[location] = O;
                    ((ImageButton) v).setImageResource(R.drawable.circle);
                    ((ImageButton) v).setEnabled(false);
                    turn = true;
                    counter++;
                }
            } else {
                //Algoritem for Bot vs human
                Random rand = new Random();//initial object random
                //step of human until...
                arrGame[location] = X;
                ((ImageButton) v).setImageResource(R.drawable.x);
                ((ImageButton) v).setEnabled(false);
                //ear not include the counter
                if (!CheckGameOver()) {//if x not win then..
                    if (counter != 8) {//check if this the last step
                        //step Bot
                        int num = rand.nextInt(9);//get to var num numbers than 0-8 included

                        while (arrGame[num] == "X" || arrGame[num] == "O") //find empty location
                            num = rand.nextInt(9);

                        arrGame[num] = O;//insert string O to location in array
                        arriB[num].setImageResource(R.drawable.circle);
                        arriB[num].setEnabled(false);
                        counter += 2;//if Bot played plus 2 to counter
                    } else
                        counter++;//if Bot did'nt played plus 1 to counter


                }
            }

//            try {
//                Thread.sleep(1500);
//            }
//            catch (InterruptedException e){
//                e.printStackTrace();
//            }
            if(counter == 9 || CheckGameOver()){//check we finish the game and hold the button
                for (int i = 0; i<arriB.length;i++) {
                    arriB[i].setEnabled(false);//apply hold button
                }

            }

            CheckGameOver();//check if we finish the game and show result
        }

    }
    private void newGame(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//initial alert dialog
        builder.setMessage(text);//set question for user
        builder.setCancelable(true);//for be impressed from the Frame and the lock button etc'

        if (text != "Are you sure,\n so do you want to back and finish the game?"){//for any dialog except button back
        builder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //new game with the same keys

                Intent intent=new Intent(MainActivity2.this,MainActivity2.class);
                intent.putExtra("keyRg",gameMode);
                intent.putExtra("key_user1",Xname);
                intent.putExtra("key_user2",Oname);
                coun3++;
                intent.putExtra("keyCounter",coun3);
                startActivity(intent);
                finish();
            }
        });}
        else {//for dialog button back
            builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }//for any dialog except new game
        if (text != "do you want restart this game?") {
            builder.setNegativeButton("Back to menu", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    //finish the page and back the main page
                    Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                    coun3++;
                    intent.putExtra("keyCounter",coun3);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else {//for dialog button new game
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//nothing not happen

                }
            });
        }


        AlertDialog dialog = builder.create();

        dialog.show();//show dialog
    }
    private boolean CheckGameOver()
    {

            // these are steps winners mode
            String [] winCases = new String[8];
            winCases[0] = "" + arrGame[0] + arrGame[3]+ arrGame[6] ;

            winCases[1] = "" + arrGame[1] + arrGame[4]+ arrGame[7] ;
            winCases[2]  = "" + arrGame[2] + arrGame[5]+ arrGame[8] ;
            winCases[3] = "" + arrGame[0] + arrGame[1]+ arrGame[2] ;
            winCases[4] = "" + arrGame[3] + arrGame[4]+ arrGame[5] ;
            winCases[5] = "" + arrGame[6] + arrGame[7]+ arrGame[8] ;
            winCases[6] = "" + arrGame[0] + arrGame[4]+ arrGame[8] ;
            winCases[7] = "" + arrGame[6] + arrGame[4]+ arrGame[2] ;

            for (int i = 0; i<winCases.length;i++)
            {//check who win or if we have a draw
                switch (winCases[i]){

                    case "XXX" ://for x win situation
                        tvnameTurn.setText("the win is :");
                        tvTurn.setText(Xname);
                        tvnameTurn.setBackground(getDrawable(R.drawable.selector));//spacial bold way with spacial designer
                        tvTurn.setBackground(getDrawable(R.drawable.selector));
                        markWin(i);//mark the buttons are win
                        new Handler().postDelayed(new Runnable() {//daley for show the win spin affect
                            public void run() {
                                newGame("user "+Xname +" WON \n" +
                                        "do you want to play again?");
                            }
                        }, 2400);   //2 second
                        //reference to function, show the result and suggest continue options

                        return  true;
                    case "OOO":
                        tvnameTurn.setText("the win is :");
                        tvTurn.setText(Oname);
                        tvnameTurn.setBackground(getDrawable(R.drawable.selector));
                        tvTurn.setBackground(getDrawable(R.drawable.selector));
                        markWin(i);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                newGame("user "+Oname +" WIN \n" +
                                        "do you want to play again?");
                            }
                        }, 2400);   //2 second

                        return  true;
                }

                }
        if (counter == 9){//for draw situation
            tvnameTurn.setText("Draw");
            tvTurn.setText("");
            tvnameTurn.setBackground(getDrawable(R.drawable.selector));
            tvTurn.setText("");
            grid.startAnimation(rotateAnimation);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    newGame("Draw \n" +
                            "do you want to play again?");
                }
            }, 2400);   //2 second

            return  true;
            }
            return false;//if the game continue
    }

    private  void markWin(int i)////mark the buttons are win
    {
        switch(i){
            case 0:
                commitFrame(0,3,6);
                break;
            case 1:
                commitFrame(1,4,7);
                break;
            case 2:
                commitFrame(2,5,8);
                break;
            case 3:
                commitFrame(0,1,2);
                break;
            case 4:
                commitFrame(3,4,5);
                break;
            case 5:
                commitFrame(6,7,8);
                break;
            case 6:
                commitFrame(0,4,8);
                break;
            case 7:
                commitFrame(2,4,6);
                break;
        }
    }
    private void commitFrame( int i , int j ,int k){//commit mark buttons
        arriB[i].setBackground(getDrawable(R.drawable.border));
        arriB[i].startAnimation(rotateAnimation);
        arriB[j].setBackground(getDrawable(R.drawable.border));
        arriB[j].startAnimation(rotateAnimation);
        arriB[k].setBackground(getDrawable(R.drawable.border));
        arriB[k].startAnimation(rotateAnimation);
    }


    private void play(){//same commons from activity1
        if(ring == null && coun3 % 2 == 0){
            ring = MediaPlayer.create(MainActivity2.this, gaming);
            ring.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stop();
                    play();
                }
            });
        }
        if (coun3 % 2 == 0)
            ring.start();
        else pause();
        coun3++;
    }
    private void pause(){
        if (ring != null)
            ring.pause();
    }
    private void stop(){
        if(ring != null){
            ring.release();
            ring = null;
//            Toast.makeText(this,"media release",Toast.LENGTH_SHORT);
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
        stop();
    }
}

//Toast.makeText(getApplicationContext(),"dscsd",Toast.LENGTH_SHORT).show();