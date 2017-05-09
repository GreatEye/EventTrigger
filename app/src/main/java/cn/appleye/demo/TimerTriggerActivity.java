package cn.appleye.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

import cn.appleye.eventtrigger.EventTriggerBus;
import cn.appleye.eventtrigger.annotations.TriggerSubscribe;
import cn.appleye.eventtrigger.triggers.Trigger;
import cn.appleye.eventtrigger.triggers.timer.TimerTrigger;

public class TimerTriggerActivity extends AppCompatActivity {

    /**显示计时信息控件*/
    private TextView mTimerInfoView;
    /**计数器*/
    private AtomicInteger mValue = new AtomicInteger(0);

    private EventTriggerBus mEventTriggerBus;
    /**计时器触发器*/
    private Trigger mTimerTrigger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_trigger);

        mTimerInfoView = (TextView) findViewById(R.id.timer_info_view);

        mEventTriggerBus = EventTriggerBus.getInstance();
        mTimerTrigger = new TimerTrigger(mEventTriggerBus, 1000);
        mEventTriggerBus.register(this);

        mTimerTrigger.setup();
    }

    @TriggerSubscribe(className = TimerTrigger.class)
    public void onTimerInfoChanged(Object result) {
        /*设置值*/
        mTimerInfoView.setText(mValue.getAndIncrement() +"");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mTimerTrigger.stopTrigger();
        mEventTriggerBus.unregister(this);
    }
}
