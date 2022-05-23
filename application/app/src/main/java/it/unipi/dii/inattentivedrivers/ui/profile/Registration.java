package it.unipi.dii.inattentivedrivers.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import it.unipi.dii.inattentivedrivers.R;
import it.unipi.dii.inattentivedrivers.ui.DatabaseHelper;

public class Registration extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    EditText username, name, surname, email, password, repeat_password;
    Button signup, signin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_registration);

        databaseHelper = new DatabaseHelper(this);
        username = (EditText)findViewById(R.id.editTextUsername);
        name = (EditText)findViewById(R.id.editTextName);
        surname = (EditText)findViewById(R.id.editTextSurname);
        email = (EditText)findViewById(R.id.editTextEmailAddress);
        password = (EditText)findViewById(R.id.editTextPassword);
        repeat_password = (EditText)findViewById(R.id.editTextRepeatPassword);
        signup = (Button)findViewById(R.id.signup);
        signin = (Button)findViewById(R.id.signin);


        signin.setOnClickListener(view -> {
            Intent intent = new Intent(Registration.this, Login.class);
            startActivity(intent);
        });

        signup.setOnClickListener(view -> {
            String username_ = username.getText().toString();
            String name_ = name.getText().toString();
            String surname_ = surname.getText().toString();
            String email_ = email.getText().toString();
            String password_ = password.getText().toString();
            String repeat_password_ = repeat_password.getText().toString();

            if(username_.equals("") || name_.equals("") || surname_.equals("") || email_.equals("") || password_.equals("") || repeat_password_.equals("")){
                Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (password_.equals(repeat_password_)) {
                    boolean checkusername = databaseHelper.CheckUsername(username_);
                    if (checkusername) {
                        boolean insert = databaseHelper.Insert(username_, name_, surname_, email_, password_, repeat_password_ );
                        if (insert) {
                            Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_SHORT).show();
                            username.setText(username_);
                            name.setText(name_);
                            surname.setText(surname_);
                            email.setText(email_);
                            password.setText(password_);
                            repeat_password.setText(repeat_password_);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Username already taken", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
