package com.example.campuscrave;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends Activity {

    View mConentView;
    Handler handler;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView imageView=findViewById(R.id.splashLogo);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        mConentView = findViewById(R.id.splashLogo);
        mConentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        handler = new Handler();
        handler.postDelayed(() -> {
            if (user !=null){
                if (user.isEmailVerified()){
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }else{
                    startActivity(new Intent(SplashActivity.this,SignupActivity.class));
                }
            }else{
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        },2000);

    }
}
