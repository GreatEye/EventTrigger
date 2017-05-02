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
 * @date 2017/5/2
 * 触发器抽象类，保证每次调用触发器的{@link #trigger(Object)}时，能够派发结果
 */

public abstract class AbstractTrigger implements Trigger{
    private Observer mObserver;

    public AbstractTrigger(Observer observer){
        if(observer == null) {
            throw new IllegalArgumentException("observer can not be null");
        }

        mObserver = observer;
    }

    @Override
    public void trigger(Object result){
        //派发结果
        mObserver.apply(this, result);
    }
}
