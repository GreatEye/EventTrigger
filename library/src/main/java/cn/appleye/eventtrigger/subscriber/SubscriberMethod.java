package cn.appleye.eventtrigger.subscriber;

import java.lang.reflect.Method;

import cn.appleye.eventtrigger.common.LoopMode;

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
 * date 2017/4/30
 * 方法信息
 */

public class SubscriberMethod {
    /**方法*/
    public final Method mMethod;
    /**循环类型*/
    public final LoopMode mLoopMode;
    /**所属触发器*/
    public final Class<?> mTriggerClass;

    public SubscriberMethod(Method method, LoopMode loopMode, Class<?> triggerClass) {
        mMethod = method;
        mLoopMode = loopMode;
        mTriggerClass = triggerClass;
    }

    @Override
    public int hashCode() {
        return mMethod.hashCode();
    }
}
