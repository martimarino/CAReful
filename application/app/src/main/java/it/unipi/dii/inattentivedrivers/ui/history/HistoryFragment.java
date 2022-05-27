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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import it.unipi.dii.inattentivedrivers.databinding.FragmentHistoryBinding;
import it.unipi.dii.inattentivedrivers.ui.newtrip.NewTripFragment;
import it.unipi.dii.inattentivedrivers.R;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private List<Trip> tripList;
    ArrayList<Trip> arrayList;
    TextView history;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (NewTripFragment.session.arrayList == null){
            Log.d("ArrayList", "ArrayList empty");
        } else {
            Log.d("ArrayList", String.valueOf(NewTripFragment.session.getArrayList()));
            fillHistory();
        }

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fillHistory() {

        arrayList = NewTripFragment.session.arrayList;
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
//            tr.setPadding(20, 20, 20, 20);
//            tr.setBackgroundColor(Color.parseColor("#81D6FF"));
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