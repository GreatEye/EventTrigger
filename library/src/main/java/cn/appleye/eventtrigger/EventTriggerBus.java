package cn.appleye.eventtrigger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.appleye.eventtrigger.common.LoopMode;
import cn.appleye.eventtrigger.observer.Observer;
import cn.appleye.eventtrigger.subscriber.SubscriberInfo;
import cn.appleye.eventtrigger.subscriber.SubscriberMethod;
import cn.appleye.eventtrigger.subscriber.SubscriberMethodFinder;
import cn.appleye.eventtrigger.triggers.Trigger;
import cn.appleye.eventtrigger.utils.LogUtil;

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
 * date 2017/4/24
 * EventTrigger是一个事件型触发器，当定义的触发器出发动作时，会自动调用在类中订阅过的对应的方法
 * 在方法中订阅方法时，根据注解{@link cn.appleye.eventtrigger.annotations.TriggerSubscribe}
 * 定义触发器对应的类，和循环类型
 */

public class EventTriggerBus implements Observer{
    private static final String TAG = "EventTriggerBus";

    private static volatile EventTriggerBus sInstance;

    /**订阅方法查找器*/
    private SubscriberMethodFinder mSubscriberMethodFinder;

    /**所有触发器对应方法集合*/
    private Map<Class, Set<SubscriberInfo>> mTotalSubscriberMethodMap = new HashMap<>();

    /**全局触发器列表*/
    private List<Trigger> mGlobalTriggers = new ArrayList<>();

    private EventTriggerBus(){
        mSubscriberMethodFinder = new SubscriberMethodFinder();
        mTotalSubscriberMethodMap.clear();
        mGlobalTriggers.clear();
    }

    /**
     * 获取单例
     * @return 返回单例
     * */
    public static EventTriggerBus getInstance(){
        if(sInstance == null) {
            synchronized (EventTriggerBus.class){
                if(sInstance == null){
                    sInstance = new EventTriggerBus();
                }
            }
        }

        return sInstance;
    }

    /**
     * 添加全局Trigger，需要实现{@link Trigger}接口, 注意内存泄露问题
     * @param trigger 触发器
     * */
    public void addGlobalTrigger(Trigger trigger) {
        mGlobalTriggers.add(trigger);
    }

    /**
     * 强制触发触发器
     * @param triggerClass 触发器对应的类
     * */
    public void forceCallGlobalTrigger(Class<?> triggerClass) {
        for(Trigger trigger : mGlobalTriggers) {
            if(trigger.getClass() == triggerClass){
                trigger.forceTrigger();
            }
        }
    }

    /**
     * 去掉全局触发器
     * @param triggerClass 触发器类名
     * */
    public void removeGlobalTrigger(Class<?> triggerClass) {
        int size = mGlobalTriggers.size();
        for(int i=size-1; i>=0; i--) {
            Trigger trigger = mGlobalTriggers.get(i);
            if(trigger.getClass() == triggerClass) {
                trigger.stopTrigger();
                mGlobalTriggers.remove(i);
            }
        }
    }

    /**
     * 去掉所有的全局触发器
     * */
    public void removeAllGlobalTriggers() {
        for(Trigger trigger : mGlobalTriggers) {
            trigger.forceTrigger();
        }

        mGlobalTriggers.clear();
    }

    /**
     * 为当前实例注册触发器
     * @param object 定义了注册方法的对象
     * */
    public void register(Object object){
        Class<?> subscriberClass = object.getClass();
        List<SubscriberMethod> subscriberMethodList = mSubscriberMethodFinder.findSubscriberMethod(subscriberClass);
        synchronized (this){
            for(SubscriberMethod subscriberMethod : subscriberMethodList) {
                Set<SubscriberInfo> subscriberMethodSet =
                        mTotalSubscriberMethodMap.get(subscriberMethod.mTriggerClass);
                if(subscriberMethodSet == null) {
                    subscriberMethodSet = new HashSet<>();
                    mTotalSubscriberMethodMap.put(subscriberMethod.mTriggerClass, subscriberMethodSet);
                }

                SubscriberInfo subscriberInfo = new SubscriberInfo(object, subscriberMethod);
                subscriberMethodSet.add(subscriberInfo);
            }
        }
    }

    /**
     * 触发器派发接口
     * @param trigger 触发器
     * @param result 结果参数
     * */
    @Override
    public void apply(Trigger trigger, Object result){
        boolean isGlobalTrigger = checkIfGlobalTrigger(trigger);
        Class<?> triggerClass = trigger.getClass();
        Set<SubscriberInfo> subscriberInfoSet = mTotalSubscriberMethodMap.get(triggerClass);
        if(subscriberInfoSet != null) {
            //遍历该触发器对应的所有方法，然后调用方法
            Iterator<SubscriberInfo> it = subscriberInfoSet.iterator();
            while(it.hasNext()) {
                SubscriberInfo subscriberInfo = it.next();
                Object object = subscriberInfo.mObject;

                //如果不是全局触发器，那么我们只能允许同一个触发器和注册方法作为同一个对象的属性和方法才能调用
                if(!isGlobalTrigger) {
                    try{
                        Field targetField = object.getClass().getField(trigger.getClass().getName());
                        boolean access = targetField.isAccessible();
                        try{
                            if(!access) {
                                targetField.setAccessible(true);
                            }
                            Object targetFiledObject = targetField.get(object);

                            if(targetFiledObject != trigger) {//如果当前触发器不是当前对象中定义的那个属性
                                LogUtil.d(TAG, "the trigger " + trigger.getName() + " object is not the field of "
                                        + object.getClass().getSimpleName());
                                continue;
                            }
                        }finally {
                            if(!access) {
                                targetField.setAccessible(false);//还原属性
                            }
                        }
                    }catch (NoSuchFieldException nsfe){
                        LogUtil.d(TAG, "the trigger " + trigger.getName() + " is not the field of "
                                + object.getClass().getSimpleName());
                        continue;//不属于当前对象，则跳过
                    }catch (IllegalAccessException iae){
                        iae.printStackTrace();
                        continue;
                    }

                }
                SubscriberMethod subscriberMethod = subscriberInfo.mSubscriberMethod;
                //把try..catch..放在循环体内，可以避免对后续方法调用造成影响
                try{
                    //调用方法接口
                    subscriberMethod.mMethod.invoke(object, result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //只执行一次的方法，则执行完把它去掉
                if(LoopMode.ONCE == subscriberMethod.mLoopMode){
                    it.remove();
                }
            }
        }
    }

    /**
     * 检查触发器是否是全局触发器
     * @param trigger 目标触发器
     * */
    private boolean checkIfGlobalTrigger(Trigger trigger) {
        for(Trigger globalTrigger : mGlobalTriggers) {
            if(globalTrigger == trigger) {
                return true;
            }
        }

        return false;
    }

    /**
     * 为当前实例注销触发器
     * @param object 定义了注册方法的对象
     * */
    public void unregister(Object object){
        synchronized (this) {
            //遍历所有的方法，注销掉当前对象包含的方法
            Iterator<Set<SubscriberInfo>> setIterator = mTotalSubscriberMethodMap.values().iterator();
            while(setIterator.hasNext()) {
                Set<SubscriberInfo> subscriberInfoSet = setIterator.next();
                Iterator<SubscriberInfo> subscriberInfoIterator = subscriberInfoSet.iterator();
                while(subscriberInfoIterator.hasNext()) {
                    SubscriberInfo subscriberInfo = subscriberInfoIterator.next();
                    if(subscriberInfo.mObject == object) {
                        subscriberInfoIterator.remove();
                    }
                }
            }
        }
    }
}
