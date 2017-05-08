package cn.appleye.demo;

import android.app.Application;

import cn.appleye.eventtrigger.EventTriggerBus;
import cn.appleye.eventtrigger.triggers.network.NetworkTrigger;

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author feiyu
 * @date 2017/5/8
 */

public class DemoApplication extends Application{
    private static final String TAG = "DemoApplication";

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
        NetworkTrigger networkTrigger = new NetworkTrigger(eventTriggerBus);
        networkTrigger.registerReceiver(this);
        eventTriggerBus.addGlobalTrigger(networkTrigger);
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        EventTriggerBus.getInstance().removeAllGlobalTriggers();
    }
}
