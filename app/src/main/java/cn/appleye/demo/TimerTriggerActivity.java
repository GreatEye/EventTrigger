package cn.appleye.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

import cn.appleye.eventtrigger.EventTriggerBus;
import cn.appleye.eventtrigger.annotations.TriggerSubscribe;
import cn.appleye.eventtrigger.triggers.timer.TimerTrigger;

public class TimerTriggerActivity extends AppCompatActivity {

    /**显示计时信息控件*/
    private TextView mTimerInfoView;
    /**计数器*/
    private AtomicInteger mValue = new AtomicInteger(0);

    private EventTriggerBus mEventTriggerBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_trigger);

        mTimerInfoView = (TextView) findViewById(R.id.timer_info_view);

        //获取EventTriggerBus并且注册当前类
        mEventTriggerBus = EventTriggerBus.getInstance();
        mEventTriggerBus.register(this);

        mEventTriggerBus.installLocalTrigger(this,
                TimerTrigger.class, new Object[]{mEventTriggerBus, 1000})
                        .forceCallLocalTrigger(this, TimerTrigger.class);

        //初始化触发器
//        mTimerTrigger = new TimerTrigger(mEventTriggerBus, 1000);//1s间隔
//        mTimerTrigger.setOwner(this);//设置触发器所有者为当前owner
//        mTimerTrigger.setup();
    }

    /**
     * 添加注解，用于过滤和得到要订阅的方法
     * */
    @TriggerSubscribe(className = TimerTrigger.class)
    public void onTimerInfoChanged(Object result) {
        /*设置值*/
        mTimerInfoView.setText(mValue.getAndIncrement() +"");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventTriggerBus.unregister(this);
    }
}
