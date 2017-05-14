package cn.appleye.eventtrigger.triggers.network;

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
 * date 2017/5/5
 * 保存网络状态的类
 */

public class NetworkState {
    /**网络类型*/
    public final NetworkType networkType;
    /**数据网络下面的详细网络类型*/
    public SubNetworkType subNetworkType;
    /**wifi网络名称*/
    public String SSID;
    /**wifi强度*/
    public int rssi;
    /**wifi mac地址*/
    public String mac;

    public NetworkState(NetworkType networkType) {
        this.networkType = networkType;
    }

    public enum NetworkType{
        WIFI, MOBILE, NONE//Wifi、无网络
    }

    public enum SubNetworkType{
        G4, G3, G2, UNKNOWN //4G、3G、2G、未知
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{\n");

        if(NetworkType.NONE != networkType) {
            if(NetworkType.WIFI == networkType) {
                sb.append("\tnetworkType : wifi,\n");
                sb.append("\tssid : " + SSID + ",\n");
                sb.append("\trssi : " + rssi + ",\n");
                sb.append("\tmac : " + mac);
            } else if(NetworkType.MOBILE == networkType) {
                sb.append("\tnetworkType : mobile,\n");
                if(SubNetworkType.G4 == subNetworkType) {
                    sb.append("\tsubNetworkType : 4G");
                }else if(SubNetworkType.G3 == subNetworkType) {
                    sb.append("\tsubNetworkType : 3G");
                }else if(SubNetworkType.G2 == subNetworkType){
                    sb.append("\tsubNetworkType : 2G");
                }else {
                    sb.append("\tsubNetworkType : unknown");
                }
            }
        } else {
            sb.append("\tnetworkType : none");
        }

        sb.append("\n}");

        return sb.toString();
    }
}
