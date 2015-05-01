package com.project.csc480.osubustracker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

/**
 * Created by Scott on 4/30/2015.
 */
public class TutorialActivity extends FragmentActivity{
    private int temp = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.tutorial_1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void next(View view){
        if(temp ==1) {
            setContentView(R.layout.tutorial_2);
            temp++;
        }else if(temp ==2){
            setContentView(R.layout.tutorial_3);
            temp++;
        }else if(temp ==3){
            setContentView(R.layout.tutorial_4);
            temp++;
        }else{

        }
    }

    public void previous(View view){
        if(temp ==4) {
            setContentView(R.layout.tutorial_3);
            temp--;
        }else if(temp ==3){
            setContentView(R.layout.tutorial_2);
            temp--;
        }else if(temp ==2){
            setContentView(R.layout.tutorial_1);
            temp--;
        }else{

        }
    }

    public void done(View view){
        finish();
    }
}
