package cn.appleye.demo;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

import cn.appleye.eventtrigger.EventTrigger;
import cn.appleye.eventtrigger.annotations.TriggerSubscribe;
import cn.appleye.eventtrigger.common.LoopMode;
import cn.appleye.eventtrigger.common.StrictMode;
import cn.appleye.eventtrigger.triggers.network.NetworkState;
import cn.appleye.eventtrigger.triggers.network.NetworkTrigger;
import pub.devrel.easypermissions.EasyPermissions;

public class NetworkTriggerDemoActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private TextView mNetworkView;
    private NetworkTrigger mNetworkTrigger;
    private EventTrigger mEventTrigger;

    private static final int NETWORK_PERMISSION_REQUEST = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_trigger_demo);

        mNetworkView = (TextView) findViewById(R.id.network_info_view);

        mEventTrigger = EventTrigger.getInstance();
        mNetworkTrigger = new NetworkTrigger(mEventTrigger);

        mEventTrigger.register(this);

        //检测权限
        String[] networkPermission = new String[]{Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE};
        if(EasyPermissions.hasPermissions(this, networkPermission)){
            registerReceiver();
        } else {//申请权限
            EasyPermissions.requestPermissions(this,"Network Permission", NETWORK_PERMISSION_REQUEST, networkPermission);
        }
    }

    private void registerReceiver() {
        mNetworkTrigger.registerReceiver(this);
    }

    private void unregisterReceiver() {
        mNetworkTrigger.unregisterReceiver();
    }

    @TriggerSubscribe(className = NetworkTrigger.class, loopMode = LoopMode.ALWAYS,
            strictMode = StrictMode.STRICT)
    public void onNetworkChanged(NetworkState networkState) {
        mNetworkView.setText(networkState.toString());
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(NETWORK_PERMISSION_REQUEST == requestCode) {
            registerReceiver();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver();
        mEventTrigger.unregister(this);
    }
}
