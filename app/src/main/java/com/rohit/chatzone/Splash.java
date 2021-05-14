package com.rohit.chatzone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Thread background =new Thread(){
            public void run(){
                try{
                sleep(2000);
                    Intent intent=new Intent(Splash.this,WelcomeActivity.class);
                    startActivity(intent);
                    finish();

                }catch (Exception e){

                }

            }
        };
        background.start();

    }
}
