package it.unipi.dii.inattentivedrivers.ui.newtrip;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.service.controls.Control;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import it.unipi.dii.inattentivedrivers.databinding.StartTripBinding;
import it.unipi.dii.inattentivedrivers.R;

public class StartTrip extends AppCompatActivity {

    public StartTrip() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        it.unipi.dii.inattentivedrivers.databinding.StartTripBinding binding = StartTripBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ProgressBar pb = (ProgressBar) findViewById(R.id.determinateBar);
        GradientDrawable rainbow = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED});

        AnimationDrawable ad = getProgressBarAnimation();
        pb.setBackgroundDrawable(ad);
    }

    private AnimationDrawable getProgressBarAnimation(){

        GradientDrawable rainbow1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW});

        GradientDrawable rainbow2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] { Color.YELLOW, Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN});

        GradientDrawable rainbow3 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] { Color.GREEN, Color.YELLOW, Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN });

        GradientDrawable rainbow4 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] { Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED, Color.MAGENTA, Color.BLUE });

        GradientDrawable rainbow5 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] { Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED, Color.MAGENTA });

        GradientDrawable rainbow6 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED });


        GradientDrawable[]  gds = new GradientDrawable[] {rainbow1, rainbow2, rainbow3, rainbow4, rainbow5, rainbow6};

        AnimationDrawable animation = new AnimationDrawable();

        for (GradientDrawable gd : gds){
            animation.addFrame(gd, 100);

        }

        animation.setOneShot(false);

        return animation;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
