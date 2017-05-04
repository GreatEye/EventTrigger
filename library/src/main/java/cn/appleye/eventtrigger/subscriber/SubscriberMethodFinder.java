package cn.appleye.eventtrigger.subscriber;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import cn.appleye.eventtrigger.annotations.TriggerSubscribe;

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
 * @date 2017/4/30
 * 注册方法查找类
 */

public class SubscriberMethodFinder {
    //一下是编译器编译产生的方法
    private static final int BRIDGE = 0x40;
    private static final int SYNTHETIC = 0x1000;

    /**需要忽略的方法*/
    private static final int MODIFIERS_IGNORE = Modifier.ABSTRACT | Modifier.STATIC | BRIDGE | SYNTHETIC;
    /**
     * 获取当前类的注册过触发器的方法信息
     * @param subscriber 订阅者
     * @return 方法信息
     * */
    public List<SubscriberMethod> findSubscriberMethod(Class<?> subscriber) {
        List<SubscriberMethod> subscriberMethodList = new ArrayList<>();

        if(subscriber != null) {
            Method[] methods = subscriber.getDeclaredMethods();//包含继承类和接口类的方法
            for(Method method : methods) {
                int modifiers = method.getModifiers();
                /*过滤掉不是public和需要忽略的方法*/
                if((modifiers & Modifier.PUBLIC) != 0 && (modifiers & MODIFIERS_IGNORE) == 0) {
                    Class<?>[] parameterTypes = method.getParameterTypes();//方法参数类型
                    if(parameterTypes.length == 1) {//允许的参数只有一个，这样的方法
                        TriggerSubscribe subscribeAnnotation = method.getAnnotation(TriggerSubscribe.class);
                        if(subscribeAnnotation != null) {//方法包含有定义的注解类
                            Class<?> triggerClass = subscribeAnnotation.className();
                            if(triggerClass != Void.class) {//定义的触发器类不是空的触发器类，才有意义
                                SubscriberMethod subscriberMethod = new SubscriberMethod(method,
                                        subscribeAnnotation.loopMode(), triggerClass);
                                subscriberMethodList.add(subscriberMethod);//添加到列表中
                            }
                        }
                    } else if(method.isAnnotationPresent(TriggerSubscribe.class)) {//参数个数错误
                        String methodName = method.getName();
                        throw new IllegalArgumentException("method " + methodName + " must have exactly 1 parameter");
                    }
                } else if(method.isAnnotationPresent(TriggerSubscribe.class)) {//方法属性错误
                    String methodName = method.getName();
                    throw new IllegalArgumentException("method " + methodName + " must be public, non-static, and non-abstract");
                }
            }
        }

        return subscriberMethodList;
    }
}
