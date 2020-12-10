package com.example.sw;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
        Button autologin = findViewById(R.id.auto_login);
        TextView status = findViewById(R.id.connect_state);
        TextView bmi = findViewById(R.id.bmi);
        EditText height = findViewById(R.id.height);
        EditText weight = findViewById(R.id.weight);
        EditText muscle = findViewById(R.id.muscle);


        SharedPreferences userInfo = getSharedPreferences("userInfo",MODE_PRIVATE);
        String username = userInfo.getString("username","");
        String password = userInfo.getString("password","");

        if(username.equals("")){
            signin_btn.setVisibility(View.VISIBLE);
            signup_btn.setVisibility(View.VISIBLE);
            signout_btn.setVisibility(View.GONE);
            add.setVisibility(View.GONE);
            height.setVisibility(View.GONE);
            weight.setVisibility(View.GONE);
            muscle.setVisibility(View.GONE);
            autologin.setVisibility(View.GONE);
            bmi.setVisibility(View.GONE);
        }else {
            signin_btn.setVisibility(View.GONE);
            signup_btn.setVisibility(View.GONE);
            signout_btn.setVisibility(View.VISIBLE);
            add.setVisibility(View.VISIBLE);
            height.setVisibility(View.VISIBLE);
            weight.setVisibility(View.VISIBLE);
            muscle.setVisibility(View.VISIBLE);
            autologin.setVisibility(View.VISIBLE);
            bmi.setVisibility(View.VISIBLE);
            signout_btn.setEnabled(false);
            add.setEnabled(false);
            height.setEnabled(false);
            weight.setEnabled(false);
            muscle.setEnabled(false);
            bmi.setEnabled(false);

            TcpClient.startClient(status);
        }

        autologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TcpClient.SendMessage("login"+username+" "+password);
            }
        });

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
        if(messageEvent.message.equals("logined")){
            Button autologin = findViewById(R.id.auto_login);
            Button signout_btn = findViewById(R.id.logout);
            Button add = findViewById(R.id.add);
            autologin.setVisibility(View.GONE);
            EditText height = findViewById(R.id.height);
            EditText weight = findViewById(R.id.weight);
            EditText muscle = findViewById(R.id.muscle);
            TextView bmi = findViewById(R.id.bmi);
            signout_btn.setEnabled(true);
            add.setEnabled(true);
            height.setEnabled(true);
            weight.setEnabled(true);
            muscle.setEnabled(true);
            bmi.setEnabled(true);

        }else if(messageEvent.message.equals("addTrue")){
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Succeed!")
                    .setMessage("Data add Succeed!")
                    .setPositiveButton("Ok~", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            alertDialog.show();
        }else {
            TextView bmi = findViewById(R.id.bmi);
            bmi.setText(messageEvent.message);
        }


    }

}