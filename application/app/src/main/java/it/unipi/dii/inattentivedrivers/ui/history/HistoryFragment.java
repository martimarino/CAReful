package it.unipi.dii.inattentivedrivers.ui.history;

import android.graphics.Color;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import it.unipi.dii.inattentivedrivers.databinding.FragmentHistoryBinding;
import it.unipi.dii.inattentivedrivers.ui.DatabaseHelper;
import it.unipi.dii.inattentivedrivers.ui.newtrip.NewTripFragment;
import it.unipi.dii.inattentivedrivers.R;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    ArrayList<Trip> arrayList;
    DatabaseHelper databaseHelper;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        databaseHelper = new DatabaseHelper(getContext());

        if(NewTripFragment.session.getUsername().equals("anonymous")){
            Toast.makeText(getContext(), "Please register to save your trips!", Toast.LENGTH_LONG).show();
        }

        if (NewTripFragment.session.getTripList() == null){
            Log.d("ArrayList", "ArrayList empty");
            Toast.makeText(getContext(), "Please start some trips to see your history!", Toast.LENGTH_LONG).show();
        } else {
            Log.d("ArrayList", String.valueOf(NewTripFragment.session.getTripList()));
            fillHistory();
        }

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fillHistory() {

        arrayList = NewTripFragment.session.getTripList();
        for (Trip t: arrayList) {

            TextView tv = new TextView(getActivity());
            tv.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            tv.setText("Departure: " + t.getDeparture() + "\n"
                + "Departure time: " + t.getTimeDeparture() + "\n"
                + "Arrival: " + t.getArrival() + "\n"
                + "Arrival time: " + t.getTimeArrival() + "\n"
                + "Score: " + t.getScore());
            tv.setTextColor(Color.WHITE);
            tv.setSingleLine(false);

            TableRow tr = new TableRow(getContext());
            tr.addView(tv);

            tr.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.trip_tr));

            TableLayout.LayoutParams tableRowParams=
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);

            int leftMargin=0;
            int topMargin=0;
            int rightMargin=0;
            int bottomMargin=20;

            tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

            tr.setLayoutParams(tableRowParams);
            binding.tripList.addView(tr);

//            binding.timeDeparture.setText("1");
//            binding.timeArrival.setText("2");
//            binding.score.setText("3");
//            binding.departure.setText("4");
//            binding.arrival.setText("4");

        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}