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
 * @date 2017/5/5
 */

public class NetworkState {
    /**网络类型*/
    public final NetworkType networkType;
    /**网络名称*/
    public final String SSID;
    /**强度*/
    public final int level;
    /**mac地址*/
    public final String mac;

    public NetworkState(NetworkType networkType,
                        String SSID, int level, String mac) {
        this.networkType = networkType;
        this.SSID = SSID;
        this.level = level;
        this.mac = mac;
    }

    public enum NetworkType{
        WIFI, G4, G3, G2, NONE
    }
}
