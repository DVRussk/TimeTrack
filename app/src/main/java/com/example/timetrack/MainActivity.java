package com.example.timetrack;


import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    TimeList[] times;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int[] btn_ids = {R.id.btn, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6};
        times = new TimeList[6];
        for (int i = 0; i < 6; i++) {
            times[i] = new TimeList(new ArrayList<Long>(), 0, (ToggleButton) findViewById(btn_ids[i]));
            times[i].btn.setOnCheckedChangeListener(this);
        }
    }

    public void onBtnClick(View view) {
        //System.out.println(new Date().getTime());

        for (TimeList i : times) {
            if (view.getId() != i.getButton().getId() && i.getButton().isChecked()) {
                i.getButton().setChecked(false);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        for (TimeList i : times) {
            if (buttonView.getId() == i.getButton().getId()) {
                if (isChecked) {
                    i.addEnterPoint(new Date().getTime());
                } else {
                    i.addExitPoint(new Date().getTime());
                    i.getButton().setTextOff(Objects.toString(i.getSpentTime()/1000));
                }
            }
        }

    }
}
