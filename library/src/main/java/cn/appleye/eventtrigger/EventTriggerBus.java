package cn.appleye.eventtrigger;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

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
import cn.appleye.eventtrigger.utils.ReflectUtil;

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
    private Set<Trigger> mGlobalTriggers = new HashSet<>();

    /**本地触发器*/
    private Map<Activity, Set<Trigger>> mLocalTriggers = new HashMap<>();

    private Application.ActivityLifecycleCallbacks mLifecycleCallback;

    private EventTriggerBus(){
        mSubscriberMethodFinder = new SubscriberMethodFinder();
        mTotalSubscriberMethodMap.clear();
        mGlobalTriggers.clear();
    }

    /**
     * 添加全局监听之前初始化操作
     * @param application
     * */
    public void init(Application application) {
        if(application == null) {
            throw new IllegalArgumentException("application can not be null");
        }

        if(mLifecycleCallback == null){
            mLifecycleCallback = new Application.ActivityLifecycleCallbacks(){

                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    //activity销毁的时候，移除本地触发器
                    uninstallAllLocalTriggers(activity);
                }
            };

            application.registerActivityLifecycleCallbacks(mLifecycleCallback);
        }
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
     * @return 返回单例
     * */
    public EventTriggerBus installGlobalTrigger(Trigger trigger) {
        if(trigger == null){
            throw new IllegalArgumentException("trigger is null");
        }
        trigger.setup();//全局触发器先进行初始化
        mGlobalTriggers.add(trigger);
        return this;
    }

    /**
     * 添加全局Trigger，需要实现{@link Trigger}接口, 注意内存泄露问题
     * @param triggerClass 触发器类
     * @param args 参数
     * @return 返回单例
     * */
    public EventTriggerBus installGlobalTrigger(Class triggerClass, Object[] args) {
        Trigger trigger = (Trigger) ReflectUtil.newInstance(triggerClass, args);
        installGlobalTrigger(trigger);

        return this;
    }

    /**
     * 强制触发触发器
     * @param triggerClass 触发器对应的类
     * @return 返回单例
     * */
    public EventTriggerBus forceCallGlobalTrigger(Class<?> triggerClass) {
        for(Trigger trigger : mGlobalTriggers) {
            if(trigger.getClass() == triggerClass){
                trigger.forceTrigger();
            }
        }

        return this;
    }

    /**
     * 移除全局触发器
     * @param trigger 触发器
     * @return 返回单例
     * */
    public EventTriggerBus uninstallGlobalTrigger(Trigger trigger) {
        trigger.stopTrigger();
        mGlobalTriggers.remove(trigger);
        return this;
    }

    /**
     * 移除所有属于该类实例的全局触发器
     * @param triggerClass 触发器类名
     * @return 返回单例
     * */
    public EventTriggerBus uninstallGlobalTrigger(Class<?> triggerClass) {
        Iterator<Trigger> iterator = mGlobalTriggers.iterator();
        while(iterator.hasNext()) {
            Trigger trigger = iterator.next();
            if(trigger.getClass() == triggerClass) {
                trigger.stopTrigger();//先停止，再移除
                iterator.remove();
            }
        }

        return this;
    }

    /**
     * 移除所有的全局触发器
     * @return 返回单例
     * */
    public EventTriggerBus uninstallAllGlobalTriggers() {
        for(Trigger trigger : mGlobalTriggers) {
            trigger.forceTrigger();
        }

        mGlobalTriggers.clear();

        return this;
    }

    /**
     * 添加本地触发器
     * @param activity 目标activity
     * @param trigger 触发器
     * @return 返回单例
     * */
    public EventTriggerBus installLocalTrigger(Activity activity, Trigger trigger) {
        Set<Trigger> triggerSet = mLocalTriggers.get(activity);
        if(triggerSet == null){
            triggerSet = new HashSet<>();
            mLocalTriggers.put(activity, triggerSet);
        }

        trigger.setOwner(activity);
        triggerSet.add(trigger);

        return this;
    }

    /**
     * 添加本地触发器
     * @param activity 目标activity
     * @param triggerCls 触发器类
     * @param args 参数值
     * @return 返回单例
     * */
    public EventTriggerBus installLocalTrigger(Activity activity, Class<?> triggerCls, Object[] args) {
        Trigger trigger = (Trigger)ReflectUtil.newInstance(triggerCls, args);
        if(trigger != null){
            installLocalTrigger(activity, trigger);
        }

        return this;
    }

    /**
     * 添加本地无参构造方法触发器
     * @param activity 目标activity
     * @param triggerCls 触发器类
     * @return 返回单例
     * */
    public EventTriggerBus installLocalTrigger(Activity activity, Class<?> triggerCls) {
        return installLocalTrigger(activity, triggerCls);
    }

    /**
     * 调用本地触发器
     * @param activity 目标activity
     * @param trigger 触发器
     * @return 返回单例
     * */
    public EventTriggerBus forceCallLocalTrigger(Activity activity, Trigger trigger){
        Set<Trigger> triggerSet = mLocalTriggers.get(activity);
        if(triggerSet != null && triggerSet.contains(trigger)){
            trigger.forceTrigger();
        }

        return this;
    }

    /**
     * 调用本地触发器
     * @param activity 目标activity
     * @param triggerCls 触发器类
     * @return 返回单例
     * */
    public EventTriggerBus forceCallLocalTrigger(Activity activity, Class<?> triggerCls){
        Set<Trigger> triggerSet = mLocalTriggers.get(activity);
        if(triggerSet != null ){
            Iterator<Trigger> it = triggerSet.iterator();
            while(it.hasNext()){
                Trigger trigger = it.next();
                if(triggerCls == trigger.getClass()){
                    trigger.setup();
                }
            }
        }

        return this;
    }

    /**
     * 调用所有本地触发器
     * @param activity 目标activity
     * @return 返回单例
     * */
    public EventTriggerBus forceCallLocalTrigger(Activity activity){
        Set<Trigger> triggerSet = mLocalTriggers.get(activity);
        if(triggerSet != null ){
            Iterator<Trigger> it = triggerSet.iterator();
            while(it.hasNext()){
                it.next().setup();
            }
        }

        return this;
    }

    /**
     * 移除本地触发器
     * @param activity 目标activity
     * @param trigger 触发器
     * @return 返回单例
     * */
    public EventTriggerBus uninstallLocalTrigger(Activity activity, Trigger trigger){
        Set<Trigger> triggerSet = mLocalTriggers.get(activity);
        if(triggerSet != null){
            trigger.stopTrigger();
            triggerSet.remove(trigger);

            if(triggerSet.size() == 0){
                mLocalTriggers.remove(activity);
            }
        }

        return this;
    }

    /**
     * 移除本地触发器
     * @param activity 目标activity
     * @param triggerCls 触发器类
     * @return 返回单例
     * */
    public EventTriggerBus uninstallLocalTrigger(Activity activity, Class<?> triggerCls){
        Set<Trigger> triggerSet = mLocalTriggers.get(activity);
        if(triggerSet != null){
            Iterator<Trigger> it = triggerSet.iterator();
            while(it.hasNext()){
                Trigger trigger = it.next();
                if(triggerCls == trigger.getClass()){
                    trigger.stopTrigger();
                    triggerSet.remove(trigger);
                }

            }

            if(triggerSet.size() == 0){
                mLocalTriggers.remove(activity);
            }
        }

        return this;
    }

    /**
     * 移除所有本地触发器
     * @param activity 目标activity
     * @return 返回单例
     * */
    public EventTriggerBus uninstallAllLocalTriggers(Activity activity){
        Set<Trigger> triggerSet = mLocalTriggers.get(activity);
        if(triggerSet != null){
            Iterator<Trigger> it = triggerSet.iterator();
            while(it.hasNext()){
                Trigger trigger = it.next();
                trigger.stopTrigger();
                triggerSet.remove(trigger);
            }

            mLocalTriggers.remove(activity);
        }

        return this;
    }

    /**
     * 为当前实例注册触发器
     * @param object 定义了注册方法的对象
     * @return 返回单例
     * */
    public EventTriggerBus register(Object object){
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

        return this;
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
                if(!isGlobalTrigger && object != trigger.getOwner()) {
                    //如果方法所在的对象和触发器所属的对象不是同一个
                    LogUtil.d(TAG, "the trigger " + trigger.getName() + " object is not belongs to "
                            + object.getClass().getSimpleName());
                    continue;
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
        return mGlobalTriggers.contains(trigger);
    }

    /**
     * 为当前实例注销触发器
     * @param object 定义了注册方法的对象
     * @return 返回单例
     * */
    public EventTriggerBus unregister(Object object){
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

        return this;
    }
}
