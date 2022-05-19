package it.unipi.dii.inattentivedrivers.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import it.unipi.dii.inattentivedrivers.R;
import it.unipi.dii.inattentivedrivers.ui.DatabaseHelper;

public class Login extends AppCompatActivity {
    Button signup, signin;
    EditText username, password;

    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        databaseHelper = new DatabaseHelper(this);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        signup = (Button) findViewById(R.id.signup);
        signin = (Button) findViewById(R.id.signin);

        signup.setOnClickListener(view -> {
           Intent intent = new Intent(Login.this, ProfileActivity.class);
           startActivity(intent);
        });

        signin.setOnClickListener(view -> {
            String username_ = username.getText().toString();
            String password_ = password.getText().toString();
            Boolean checklogin = databaseHelper.CheckLogin(username_, password_);
            if(checklogin == true){
                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
