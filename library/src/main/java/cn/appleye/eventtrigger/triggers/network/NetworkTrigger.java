package cn.appleye.eventtrigger.triggers.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import cn.appleye.eventtrigger.observer.Observer;
import cn.appleye.eventtrigger.triggers.AbstractTrigger;

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
 * @date 2017/4/25
 * 网络状态变化触发器
 */

public class NetworkTrigger extends AbstractTrigger {
    private static final String TAG = "NetworkTrigger";

    private Context mContext;

    /**
     * 网络连接状态变化广播接收器
     * 需要在AndroidManifest中添加一下权限
     * <uses-permission android:name="android.permission.access_network_state" />
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    /**
     * 构造方法
     * @param observer 网络变化观察者
     * */
    public NetworkTrigger(@NonNull Observer observer) {
        super(observer);
    }

    /**
     * 注册网络变化广播
     * @param context 上下文，全局使用，建议用Application
     * */
    public void registerReceiver(@NonNull Context context) {
        mContext = context;

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        mContext.registerReceiver(mReceiver, filter);
    }

    /**
     * 注销网络变化广播
     * */
    public void unregisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public String getName() {
        return "NetworkTrigger";
    }
}
