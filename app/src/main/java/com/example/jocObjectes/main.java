package com.example.jocObjectes;

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
    private ImageView obj1;
    private ImageView obj2;
    private ImageView obj3;

    private int boxY;
    private int obj1X;
    private int obj1Y;
    private int obj2X;
    private int obj2Y;
    private int obj3X;
    private int obj3Y;

    private int screenWidth;
    private int screenHeight;

    private int score = 0;

    private int frameHeight;
    private int boxSize;

    private Handler handler = new Handler();
    private Timer timer = new Timer();

    private boolean action_flg = false;
    private boolean start_flg = false;

    private SoundPlayer sound;

    private int boxSpeed;
    private int obj1Speed;
    private int obj2Speed;
    private int obj3Speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);

        box = findViewById(R.id.box);
        obj1 = findViewById(R.id.obj1);
        obj2 = findViewById(R.id.obj2);
        obj3 = findViewById(R.id.obj3);

        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        //Move to out of screen
        obj2.setX(-80);
        obj2.setY(-80);
        obj1.setX(-80);
        obj1.setY(-80);
        obj3.setX(-80);
        obj3.setY(-80);


        // (X) Si vols modificar la velocitat de moviment dels elements
        boxSpeed = Math.round(screenHeight / 60);
        obj1Speed = Math.round(screenWidth / 60);
        obj2Speed = Math.round(screenWidth / 36);
        obj3Speed = Math.round(screenWidth / 45);

        scoreLabel.setText("Score: " + score);

        sound = new SoundPlayer(this);

    }

    //Acció que realitza en fer click a la pantalla.
    public boolean onTouchEvent(MotionEvent me){
        if(start_flg == false){
            start_flg = true;

            FrameLayout frame = findViewById(R.id.frame);
            frameHeight = frame.getHeight();
            boxY = (int) box.getY();
            boxSize = box.getHeight();

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask(){
                @Override
                public void run(){
                    handler.post(new Runnable(){
                        @Override
                        public void run(){
                            changePos();
                        }
                    });
                }
            }, 0,20);
        }else{
            if(me.getAction()== MotionEvent.ACTION_DOWN){
                action_flg = false;
            }else if(me.getAction() == MotionEvent.ACTION_UP){
                action_flg = true;
            }
        }

        return true;
    }

    //hitCheck: comprova si has xocat amb una bala i afegeix la puntuació necessària
    public void hitCheck(){

        int obj1CenterX = obj1X + obj1.getWidth()/2;
        int obj1CenterY = obj1Y + obj1.getHeight()/2;
        if(0<=obj1CenterX && obj1CenterX<=boxSize && boxY<=obj1CenterY && obj1CenterY<= boxY + boxSize){
            obj1X = -10;
            score += 10; // (X) si vols canviar la puntuació
            sound.playHitSound();
        }

        int obj2CenterX = obj2X + obj2.getWidth();
        int obj2CenterY = obj2Y + obj2.getHeight();
        if (0 <= obj2CenterX && obj2CenterX <= boxSize && boxY <= obj2CenterY && obj2CenterY <= boxY + boxSize) {
            obj2X = -10;
            score += 30; // (X) si vols canviar la puntuació
            sound.playHitSound();
        }

        int obj3CenterX = obj3X + obj3.getWidth();
        int obj3CenterY = obj3Y + obj3.getHeight();
        if(0<=obj3CenterX && obj3CenterX<=boxSize && boxY<=obj3CenterY && obj3CenterY<= boxY + boxSize){
            timer.cancel();
            timer=null;

            sound.playOverSound();

            Intent intent = new Intent(getApplicationContext(), result.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }

        scoreLabel.setText("Score: " + score);
    }



    // Modifica la posició dels objectes
    public void changePos(){
        hitCheck();

        //Moviment cercle obj1
        obj1X -= obj1Speed;
        if(obj1X < 0){
            obj1X = screenWidth + 20;
            obj1Y = (int) Math.floor(Math.random() * (frameHeight - obj1.getHeight()));
        }
        obj1.setX(obj1X);
        obj1.setY(obj1Y);

        //Moviment cercle obj2
        obj2X -= obj2Speed;
        if(obj2X < 0){
            obj2X = screenWidth + 5000;
            obj2Y = (int) Math.floor(Math.random() * (frameHeight - obj2.getHeight()));
        }
        obj2.setX(obj2X);
        obj2.setY(obj2Y);

        //Moviment cercle obj3
        obj3X -= obj3Speed;
        if(obj3X < 0){
            obj3X = screenWidth + 10;
            obj3Y = (int) Math.floor(Math.random() * (frameHeight - obj3.getHeight()));
        }
        obj3.setX(obj3X);
        obj3.setY(obj3Y);


        //Modifica posició box
        if(action_flg == true){
            boxY += 20;
        }else{
            boxY -= 20;
        }

        if(boxY < 0) boxY = 0;

        if(boxY > frameHeight - boxSize) boxY = frameHeight - boxSize;
        box.setY(boxY);
    }

    // Disable Return Button
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

}
