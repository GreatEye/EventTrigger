package cn.appleye.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        View networkTriggerView = findViewById(R.id.network_trigger_view);
        networkTriggerView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DemoActivity.this, NetworkTriggerActivity.class));
            }
        });

        View timerTriggerView = findViewById(R.id.timer_trigger_view);
        timerTriggerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DemoActivity.this, TimerTriggerActivity.class));
            }
        });
    }

}
