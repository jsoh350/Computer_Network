package com.example.sw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        Button signin_btn = findViewById(R.id.toSignIn);
        Button signup_btn = findViewById(R.id.toSignUp);
        TextView status = findViewById(R.id.connect_state);

        SharedPreferences userInfo = getSharedPreferences("userInfo",MODE_PRIVATE);
        String username = userInfo.getString("username","");
        String password = userInfo.getString("password","");

        if(username.equals("")){
            signin_btn.setVisibility(View.VISIBLE);
            signup_btn.setVisibility(View.VISIBLE);
        }else {
            signin_btn.setVisibility(View.GONE);
            signup_btn.setVisibility(View.GONE);
            TcpClient.startClient(status);
            TcpClient.SendMessage("login"+username+" "+password);
        }
        startListen();
    }

    private void startListen(){
        Button signin_btn = findViewById(R.id.toSignIn);
        Button signup_btn = findViewById(R.id.toSignUp);

        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignPage.class);
                intent.putExtra("type","login");
                startActivity(intent);
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignPage.class);
                intent.putExtra("type","sign up");
                startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ShowResponse(ShowResponse messageEvent){
        System.out.println(messageEvent.message);
    }

}