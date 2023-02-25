package it.unipi.dii.careful.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import it.unipi.dii.careful.databinding.FragmentSigninSignupBinding;
import it.unipi.dii.careful.ui.DatabaseHelper;
import it.unipi.dii.careful.ui.history.Trip;
import it.unipi.dii.careful.ui.newtrip.NewTripFragment;

public class SignInSignUp extends Fragment {

    DatabaseHelper databaseHelper;
    EditText username, name, surname, email, password, repeat_password, username_login, password_login;
    Button signup_reg, signin_reg, signin_login, signup_login, logout;

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
        logout = binding.logout;
        username_login = binding.username;
        password_login = binding.password;

        if(!NewTripFragment.session.isLogged()) {
            binding.containerProfile.setVisibility(View.INVISIBLE);
            binding.containerSignin.setVisibility(View.VISIBLE);
        } else {
            showProfile();
        }

        signin_reg.setOnClickListener(view -> {
            binding.containerSignup.setVisibility(View.INVISIBLE);
            binding.containerSignin.setVisibility(View.VISIBLE);
        });

        logout.setOnClickListener(view -> {
            binding.containerProfile.setVisibility(View.INVISIBLE);
            binding.containerSignin.setVisibility(View.VISIBLE);
            NewTripFragment.session.logout();
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
                    boolean checkusername = databaseHelper.checkUsername(username_);
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
            String username_ = username_login.getText().toString();
            String password_ = password_login.getText().toString();
            Boolean checklogin = databaseHelper.CheckLogin(username_, password_);
            if(checklogin == true){
                ArrayList<Trip> arrayList = databaseHelper.retrieveHistory(username_);
                if (arrayList.size() > 0){
                    NewTripFragment.session.setTripList(arrayList);
                }
                Toast.makeText(getContext(), "Login Successful\nSee your history in the HistoryFragment", Toast.LENGTH_SHORT).show();
                showProfile();
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

    public void showProfile() {
        binding.us.setText("Username: " + NewTripFragment.session.getUsername());
        binding.nm.setText("Name: " + NewTripFragment.session.getName());
        binding.sn.setText("Surname: " + NewTripFragment.session.getSurname());
        binding.em.setText("E-mail: " + NewTripFragment.session.getEmail());

        binding.containerProfile.setVisibility(View.VISIBLE);
        binding.containerSignin.setVisibility(View.INVISIBLE);
    }
}