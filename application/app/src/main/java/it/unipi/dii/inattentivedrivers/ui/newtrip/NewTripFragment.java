package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.unipi.dii.inattentivedrivers.R;
import it.unipi.dii.inattentivedrivers.databinding.FragmentNewtripBinding;

public class NewTripFragment extends Fragment {

    private FragmentNewtripBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewTripViewModel newTripViewModel =
                new ViewModelProvider(this).get(NewTripViewModel.class);

        binding = FragmentNewtripBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        final TextView textView = binding.textNewtrip;
        final Button button = binding.button;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity2();
            }
        });

        newTripViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void openActivity2(){
        Intent intent = new Intent(getActivity(), StartTrip.class);
        startActivity(intent);
    }

}