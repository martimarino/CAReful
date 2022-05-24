package it.unipi.dii.inattentivedrivers.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.unipi.dii.inattentivedrivers.databinding.FragmentSigninSignupBinding;
import it.unipi.dii.inattentivedrivers.ui.DatabaseHelper;

public class SignInSignUp extends Fragment {

    DatabaseHelper databaseHelper;
    EditText username, name, surname, email, password, repeat_password;
    Button signup_reg, signin_reg, signin_login, signup_login;

    FragmentSigninSignupBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentSigninSignupBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        databaseHelper = new DatabaseHelper(getContext());
        username = binding.editTextUsername;
        name = binding.editTextName;
        surname = binding.editTextSurname;
        email = binding.editTextEmailAddress;
        password = binding.editTextPassword;
        repeat_password = binding.editTextRepeatPassword;
        signup_reg = binding.signupReg;
        signin_reg = binding.signinReg;
        signin_login = binding.signinLogin;
        signup_login = binding.signupLogin;

        signin_reg.setOnClickListener(view -> {
            binding.containerSignup.setVisibility(View.INVISIBLE);
            binding.containerSignin.setVisibility(View.VISIBLE);
        });

        signup_reg.setOnClickListener(view -> {
            String username_ = username.getText().toString();
            String name_ = name.getText().toString();
            String surname_ = surname.getText().toString();
            String email_ = email.getText().toString();
            String password_ = password.getText().toString();
            String repeat_password_ = repeat_password.getText().toString();

            if(username_.equals("") || name_.equals("") || surname_.equals("") || email_.equals("") || password_.equals("") || repeat_password_.equals("")){
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (password_.equals(repeat_password_)) {
                    boolean checkusername = databaseHelper.CheckUsername(username_);
                    if (checkusername) {
                        boolean insert = databaseHelper.Insert(username_, name_, surname_, email_, password_, repeat_password_ );
                        if (insert) {
                            Toast.makeText(getContext(), "Registered", Toast.LENGTH_SHORT).show();
                            username.setText(username_);
                            name.setText(name_);
                            surname.setText(surname_);
                            email.setText(email_);
                            password.setText(password_);
                            repeat_password.setText(repeat_password_);
                        }
                    } else {
                        Toast.makeText(getContext(), "Username already taken", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signin_login.setOnClickListener(view -> {
            String username_ = username.getText().toString();
            String password_ = password.getText().toString();
            Boolean checklogin = databaseHelper.CheckLogin(username_, password_);
            if(checklogin){
                Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        signup_login.setOnClickListener(view -> {
            binding.containerSignup.setVisibility(View.VISIBLE);
            binding.containerSignin.setVisibility(View.INVISIBLE);
        });

        return root;
    }
}
