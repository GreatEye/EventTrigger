# EventTrigger
基于android的事件触发器，通过自定义触发器，同时在类中添加注册方法来实现触发器发生改变时，进行结果分发。
### Demo
1.单个类中定义触发器 <br/>
以TimerTrigger为例
```java
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

        //获取EventTriggerBus并且注册当前类
        mEventTriggerBus = EventTriggerBus.getInstance();
        mEventTriggerBus.register(this);
        //初始化触发器
        mTimerTrigger = new TimerTrigger(mEventTriggerBus, 1000);//1s间隔
        mTimerTrigger.setup();
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
        //停止触发器，并且注销当前类
        mTimerTrigger.stopTrigger();
        mEventTriggerBus.unregister(this);
    }
}
```

###2.全局触发器用法
以NetworkTrigger为例<br/>
Step 1 : 首先在Application中定义全局触发器
```java
public class DemoApplication extends Application{
    private static final String TAG = "DemoApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        setupGlobalTrigger();
    }

    /**
     * 初始化全局触发器
     * */
    private void setupGlobalTrigger() {
        EventTriggerBus eventTriggerBus = EventTriggerBus.getInstance();

        /**添加网络状态变化的触发器*/
        Trigger networkTrigger = new NetworkTrigger(eventTriggerBus, this);
        networkTrigger.setup();
        eventTriggerBus.addGlobalTrigger(networkTrigger);
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        //移除所有触发器
        EventTriggerBus.getInstance().removeAllGlobalTriggers();
    }
}
```

Step 2 : 注册当前对象
```java
public class NetworkTriggerActivity extends AppCompatActivity {
    private TextView mNetworkView;
    private EventTriggerBus mEventTriggerBus;

    private static final int NETWORK_PERMISSION_REQUEST = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_trigger_demo);

        mNetworkView = (TextView) findViewById(R.id.network_info_view);

        //注册当前类
        mEventTriggerBus = EventTriggerBus.getInstance();
        mEventTriggerBus.register(this);

        forceNetworkTrigger();//立刻生效
    }

    @TriggerSubscribe(className = NetworkTrigger.class, loopMode = LoopMode.ALWAYS,
            strictMode = StrictMode.STRICT)
    public void onNetworkChanged(NetworkState networkState) {
        mNetworkView.setText(networkState.toString());
    }

    /**
     * 强制调用网络状态变化触发器
     * */
    private void forceNetworkTrigger(){
        //强制执行当前触发器
        mEventTriggerBus.forceCallGlobalTrigger(NetworkTrigger.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEventTriggerBus.unregister(this);
    }
}
```