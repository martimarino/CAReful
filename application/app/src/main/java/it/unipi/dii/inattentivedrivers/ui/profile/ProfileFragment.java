package it.unipi.dii.inattentivedrivers.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.unipi.dii.inattentivedrivers.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    EditText email;
    EditText password;
    EditText repeatPassword;
    Button confirm;


    private FragmentProfileBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    boolean isEmail(EditText text){
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEmpty(EditText text){
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    void checkData(){
        if (isEmpty(email)){
            email.setError("Please insert the email");
        }
        if (isEmail(email) == false){
            email.setError("Please insert a valid email");
        }
        if (isEmpty(password)){
            password.setError("Please insert the password");
        }
        if (isEmpty(repeatPassword)){
            repeatPassword.setError("Please insert the password");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSignUp;
        profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        confirm.setOnClickListener(view -> checkData());

        return root;
    }
/*
    Button button = (Button) findViewById(R.id.button_send);
button.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            // Do something in response to button click
        }
    });*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}