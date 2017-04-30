package cn.appleye.eventtrigger;

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
 * @author liuliaopu
 * @date 2017/4/24
 */

public class EventTrigger {
    private static volatile EventTrigger sInstance;

    private EventTrigger(){}

    public static EventTrigger getInstance(){
        if(sInstance == null) {
            synchronized (EventTrigger.class){
                if(sInstance == null){
                    sInstance = new EventTrigger();
                }
            }
        }

        return sInstance;
    }

    /**
     * 为当前实例注册触发器
     * */
    public void register(Object object){

    }

    /**
     * 为当前实例注销触发器
     * */
    public void unregister(Object object){

    }
}
