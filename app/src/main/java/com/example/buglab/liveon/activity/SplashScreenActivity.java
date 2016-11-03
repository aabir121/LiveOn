package com.example.buglab.liveon.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.buglab.liveon.R;

public class SplashScreenActivity extends AppCompatActivity {
    String TAG="LIVEON TAG";
    Handler handler=new Handler();
    int progress=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<20;i++)
                {

                    progress+=10;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(progress==100)
                            {
                                //Finish the splash activity so it can't be returned to.
                                SplashScreenActivity.this.finish();
                                // Create an Intent that will start the main activity.
                                Intent mainIntent = new Intent(SplashScreenActivity.this, MainMenuActivity.class);
                                SplashScreenActivity.this.startActivity(mainIntent);
                            }
                        }
                    });
                    try{
                        Thread.sleep(500);
                    }catch(InterruptedException ie){

                    }

                }
            }
        }).start();
    }
}
