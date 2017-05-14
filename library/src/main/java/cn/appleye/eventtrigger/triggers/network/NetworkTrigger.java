package cn.appleye.eventtrigger.triggers.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

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
 * date 2017/4/25
 * 网络状态变化触发器
 * 使用该触发器类，需要在AndroidManifest中添加一下权限
 * &lt;uses-permission android:name="android.permission.access_network_state" /&gt;
 * &lt;uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /&gt;
 */

public class NetworkTrigger extends AbstractTrigger {
    private static final String TAG = "NetworkTrigger";

    private Context mContext;

    /**
     * 网络连接状态变化广播接收器
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                onNetworkChanged();
            }
        }
    };

    /**
     * 构造方法
     * @param observer 网络变化观察者
     * @param context 上下文
     * */
    public NetworkTrigger(@NonNull Observer observer, Context context) {
        super(observer);

        mContext = context;
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

    private void onNetworkChanged() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        NetworkState networkState;
        if(networkInfo != null) {
            int networkType = networkInfo.getType();
            if(ConnectivityManager.TYPE_WIFI == networkType) {
                networkState = new NetworkState(NetworkState.NetworkType.WIFI);
                WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                networkState.SSID = wifiInfo.getSSID();
                networkState.mac = wifiInfo.getMacAddress();
                networkState.rssi = wifiInfo.getRssi();

            } else if(ConnectivityManager.TYPE_MOBILE == networkType){
                networkState = new NetworkState(NetworkState.NetworkType.MOBILE);

                int subNetworkType = networkInfo.getSubtype();
                setSubNetworkType(subNetworkType, networkInfo.getSubtypeName(), networkState);
            } else {
                networkState = new NetworkState(NetworkState.NetworkType.NONE);
            }
        } else {
            networkState = new NetworkState(NetworkState.NetworkType.NONE);
        }

        dispatch(networkState);
    }

    /**
     * 设置数据网络情况下，网络类型
     * @param subNetworkType 网络类型
     * @param subTypeName 网络名称
     * @param networkState 网络状态信息
     * */
    private void setSubNetworkType(int subNetworkType, String subTypeName, NetworkState networkState) {
        switch (subNetworkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                networkState.subNetworkType = NetworkState.SubNetworkType.G2;
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
            case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
            case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                networkState.subNetworkType = NetworkState.SubNetworkType.G3;
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                networkState.subNetworkType = NetworkState.SubNetworkType.G4;
                break;
            default: {
                // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                if (subTypeName.equalsIgnoreCase("TD-SCDMA") || subTypeName.equalsIgnoreCase("WCDMA")
                        || subTypeName.equalsIgnoreCase("CDMA2000")) {
                    networkState.subNetworkType = NetworkState.SubNetworkType.G3;
                } else {
                    networkState.subNetworkType = NetworkState.SubNetworkType.UNKNOWN;
                }

                break;
            }
        }
    }

    @Override
    public void setup(){
        registerReceiver(mContext);
    }

    @Override
    public String getName() {
        return "NetworkTrigger";
    }

    @Override
    public void forceTrigger() {
        onNetworkChanged();
    }

    @Override
    public void stopTrigger(){
        unregisterReceiver();
    }
}
