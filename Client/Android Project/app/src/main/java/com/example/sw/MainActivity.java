package com.example.sw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        Button signout_btn = findViewById(R.id.logout);
        Button add = findViewById(R.id.add);
        TextView status = findViewById(R.id.connect_state);

        SharedPreferences userInfo = getSharedPreferences("userInfo",MODE_PRIVATE);
        String username = userInfo.getString("username","");
        String password = userInfo.getString("password","");

        if(username.equals("")){
            signin_btn.setVisibility(View.VISIBLE);
            signup_btn.setVisibility(View.VISIBLE);
            signout_btn.setVisibility(View.GONE);
            add.setVisibility(View.GONE);
        }else {
            signin_btn.setVisibility(View.GONE);
            signup_btn.setVisibility(View.GONE);
            signout_btn.setVisibility(View.VISIBLE);
            add.setVisibility(View.VISIBLE);
            TcpClient.startClient(status);
            TcpClient.SendMessage("login"+username+" "+password);
        }
        startListen();
    }

    private void startListen(){
        Button signin_btn = findViewById(R.id.toSignIn);
        Button signup_btn = findViewById(R.id.toSignUp);
        Button signout_btn = findViewById(R.id.logout);
        Button add = findViewById(R.id.add);
        EditText height = findViewById(R.id.height);
        EditText weight = findViewById(R.id.weight);
        EditText muscle = findViewById(R.id.muscle);

        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignPage.class);
                intent.putExtra("type","sign in");
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

        signout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("userInfo",MODE_PRIVATE).edit();
                editor.putString("username","");
                editor.putString("password","");
                editor.commit();
                TcpClient.SendMessage("logout");
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String data = "add{'height':" + height.getText().toString() + ",'weight':" + weight.getText().toString() + ",'muscle':" + muscle.getText().toString() + "}";
                 TcpClient.SendMessage(data);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ShowResponse(ShowResponse messageEvent){
        System.out.println(messageEvent.message);
    }

}