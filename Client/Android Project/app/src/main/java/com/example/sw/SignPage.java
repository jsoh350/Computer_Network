package com.example.sw;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SignPage extends AppCompatActivity {

    protected String type;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_page);

        listener();

        EventBus.getDefault().register(this);

        type = getIntent().getStringExtra("type");

        if(type.equals("sign in")){
            signin();
        }else if(type.equals("sign up")){
            signup();
        }
    }

    private void signin(){
        TextView title = findViewById(R.id.title);
        Button submit = findViewById(R.id.submit);
        RadioGroup sex = findViewById(R.id.sex);
        sex.setVisibility(View.GONE);
        submit.setText("Sign In");
        title.setText("Sign In");
    }

    private void signup(){
        TextView title = findViewById(R.id.title);
        Button submit = findViewById(R.id.submit);
        submit.setText("Sign Up");
        title.setText("Sign Up");
    }

    private void listener(){
        EditText un = findViewById(R.id.username);
        EditText pw = findViewById(R.id.password);
        Button submit = findViewById(R.id.submit);
        TextView status = findViewById(R.id.connect_state);
        TcpClient.startClient(status);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = new MD5Utils().bin2hex(un.getText().toString());
                password = new MD5Utils().bin2hex(pw.getText().toString());

                String inner = type+username+" "+password;
                if(type.equals("sign up")){
                    RadioButton male = findViewById(R.id.male);
                    RadioButton female = findViewById(R.id.female);
                    if(male.isChecked()==false&&female.isChecked()==false){
                        return;
                    }else if(male.isChecked()){
                        inner += "1";
                    }else{
                        inner += "0";
                    }
                }

                TcpClient.SendMessage(inner);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ShowResponse(ShowResponse messageEvent){
        System.out.println(messageEvent.message);
        if(messageEvent.message.equals("True")){
            SharedPreferences.Editor editor = getSharedPreferences("userInfo",MODE_PRIVATE).edit();
            editor.putString("username",username);
            editor.putString("password",password);
            editor.commit();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }

}