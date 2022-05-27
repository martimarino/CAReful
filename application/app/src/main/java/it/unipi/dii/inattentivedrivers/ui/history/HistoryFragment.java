package it.unipi.dii.inattentivedrivers.ui.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import it.unipi.dii.inattentivedrivers.databinding.FragmentHistoryBinding;
import it.unipi.dii.inattentivedrivers.ui.newtrip.NewTripFragment;
import it.unipi.dii.inattentivedrivers.ui.profile.Session;
import it.unipi.dii.inattentivedrivers.ui.profile.SignInSignUp;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private List<Trip> tripList;
    ArrayList<Trip> arrayList;
    TextView history;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (NewTripFragment.session.arrayList == null){
            Log.d("ArrayList", "ArrayList empty");
        } else {
            Log.d("ArrayList", String.valueOf(NewTripFragment.session.getArrayList()));
        }
        // history = binding.history;

        //final TextView textView = binding.textHistory;
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}