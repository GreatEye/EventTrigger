package cn.appleye.eventtrigger;

import java.util.Map;

import cn.appleye.eventtrigger.observer.Observer;
import cn.appleye.eventtrigger.subscriber.SubscriberMethod;
import cn.appleye.eventtrigger.subscriber.SubscriberMethodFinder;
import cn.appleye.eventtrigger.triggers.Trigger;

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

public class EventTrigger implements Observer{
    private static volatile EventTrigger sInstance;

    private SubscriberMethodFinder mSubscriberMethodFinder;

    private EventTrigger(){
        mSubscriberMethodFinder = new SubscriberMethodFinder();
    }

    /**
     * 获取单例
     * */
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
        Class<?> subscriberClass = object.getClass();
        Map<Class, SubscriberMethod> subscriberMethodMap = mSubscriberMethodFinder.findSubscriberMethod(subscriberClass);
    }

    /**
     * 触发器派发接口
     * @param trigger 触发器
     * @param result 结果
     * */
    @Override
    public void apply(Trigger trigger, Object result){

    }

    /**
     * 为当前实例注销触发器
     * */
    public void unregister(Object object){

    }
}
