package de.mbranig.emiexercise3;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    Button btnTask1, btnTask2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.tuHausfarbeBlauDunkel)));

        InitializeApp();
    }

    /**
     * Construct the Interactive Structure
     */
    private void InitializeApp() {
        btnTask1 = (Button) findViewById(R.id.buttonTask1);
        btnTask2 = (Button) findViewById(R.id.buttonTask2);


        //
        // EVENT LISTENERS
        //

        btnTask1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSoundSynthesisActivity();
            }
        });

        btnTask2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startContactmanagementActivity();
            }
        });


    }

    /**
     * Start the First Activity (Task 1) - Sound Synthesis
     */
    private void startSoundSynthesisActivity() {
        startActivity(
                new Intent(this, SoundSynthesisActivity.class));
    }

    /**
     * Start the Second Activity (Task 2 and 3) - Contactmanagement
     */
    private void startContactmanagementActivity() {
        startActivity(
                new Intent(this, ContactManagementActivity.class));
    }


}
