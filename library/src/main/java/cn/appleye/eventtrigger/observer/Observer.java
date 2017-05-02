package cn.appleye.eventtrigger.observer;

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
 * @author feiyu
 * @date 2017/5/2
 * 监听触发器数据观察者
 */

public interface Observer {
    /**
     * 回调触发器结果
     * @param trigger 触发器
     * @param result 结果
     * */
    void apply(Trigger trigger, Object result);
}
