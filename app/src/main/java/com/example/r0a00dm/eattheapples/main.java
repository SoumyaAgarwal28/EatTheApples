package com.example.r0a00dm.eattheapples;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class main extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView startLabel;
    private ImageView box;
    private ImageView orange;
    private ImageView pink;
    private ImageView black;


    //Size
    private int frameHeight;
    private int boxSize;
    private int screenWidth;
    private  int screenHeight;


    //score
    private int score=0;

    //position of box
    private int boxY;
    private int orangeX;
    private int orangeY;
    private int pinkX;
    private int pinkY;
    private int blackX;
    private int blackY;

    //Initialize class
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer soundPlayer;

    //check Status
    private boolean action_flag = false;
    private boolean start_flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get objects
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        startLabel = (TextView) findViewById(R.id.startLabel);
        box = (ImageView) findViewById(R.id.box);
        orange = (ImageView) findViewById(R.id.orange);
        pink = (ImageView) findViewById(R.id.pink);
        black = (ImageView) findViewById(R.id.black);
        soundPlayer = new SoundPlayer(this);

        //Get Screen Sizes
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        screenHeight = point.y;
        screenWidth = point.x;

        //Move out of screen
        orange.setX(-80);
        orange.setY(-80);
        pink.setX(-80);
        pink.setY(-80);
        black.setX(-80);
        black.setY(-80);

        scoreLabel.setText("Score: "+ score);

    }

    public void changePos() {

        hitCheck();

        //orange
        orangeX -= 12;
        if(orangeX<0) {
            orangeX = screenWidth + 20;
            orangeY = (int) Math.floor(Math.random()*(frameHeight-orange.getHeight()));
        }
        orange.setY(orangeY);
        orange.setX(orangeX);

        //black
        blackX -= 16;
        if(blackX<0) {
            blackX = screenWidth + 10;
            blackY = (int) Math.floor(Math.random()*(frameHeight-black.getHeight()));
        }
        black.setY(blackY);
        black.setX(blackX);

        //pink
        pinkX -= 20;
        if(pinkX<0) {
            pinkX = screenWidth + 10;
            pinkY = (int) Math.floor(Math.random()*(frameHeight-pink.getHeight()));
        }
        pink.setY(pinkY);
        pink.setX(pinkX);

        if(action_flag) {
            //Touching
            boxY -= 20;
        } else {
            //Release
            boxY += 20;
        }
        if(boxY<0)
            boxY=0;
        if(boxY> (frameHeight - boxSize))
            boxY = frameHeight - boxSize;
        box.setY(boxY);

        scoreLabel.setText("Score: "+ score);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if(start_flag) {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                action_flag = true;
            } else {
                action_flag = false;
            }
        } else {
            start_flag =true;

            //Because UI has not been set on Screen in onCreate that why take box nd frame height here
            FrameLayout frameLayout= (FrameLayout) findViewById(R.id.frame);
            frameHeight = frameLayout.getHeight();

            boxY = (int) box.getY();
            boxSize = box.getHeight();

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            },0,20);
        }
        return true;
    }

    public void hitCheck() {
        // if centre of ball is in box then it counts as hit
        int orangeCentreX = orangeX + orange.getWidth()/2;
        int orangeCentreY = orangeY + orange.getHeight()/2;

        if(0 <= orangeCentreX && orangeCentreX <= boxSize &&
                boxY <= orangeCentreY && orangeCentreY <= boxY+boxSize) {
            orangeX = screenWidth + 20;
            orangeY = (int) Math.floor(Math.random()*(frameHeight-orange.getHeight()));
            score+=10;
            soundPlayer.playHitSound();
        }

        int pinkCentreX = pinkX + pink.getWidth()/2;
        int pinkCentreY = pinkY + pink.getHeight()/2;

        if(0 <= pinkCentreX && pinkCentreX <= boxSize &&
                boxY <= pinkCentreY && pinkCentreY <= boxY+boxSize) {
            pinkX = screenWidth + 20;
            pinkY = (int) Math.floor(Math.random()*(frameHeight-pink.getHeight()));
            score+=30;
            soundPlayer.playHitSound();
        }

        int blackCentreX = blackX + black.getWidth()/2;
        int blackCentreY = blackY + black.getHeight()/2;

        if(0 <= blackCentreX && blackCentreX <= boxSize &&
                boxY <= blackCentreY && blackCentreY <= boxY+boxSize) {
           timer.cancel();
           timer = null;
           soundPlayer.playOverSound();

           // Show Result on new Screen
            Intent intent = new Intent(getApplicationContext(), result.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK)
            return true;
        else
            return super.dispatchKeyEvent(keyEvent);
    }
}
