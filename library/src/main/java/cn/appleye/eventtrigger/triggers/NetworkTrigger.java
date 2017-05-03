package cn.appleye.eventtrigger.triggers;

import cn.appleye.eventtrigger.observer.Observer;

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

public class NetworkTrigger extends AbstractTrigger{
    private static final String TAG = "NetworkTrigger";

    public NetworkTrigger(Observer observer) {
        super(observer);
    }

    @Override
    public String getName() {
        return "NetworkTrigger";
    }

}