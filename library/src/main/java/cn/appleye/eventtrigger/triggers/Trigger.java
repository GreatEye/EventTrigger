package cn.appleye.eventtrigger.triggers;

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
 * @date 2017/4/25
 * 触发器接口
 */

public interface Trigger{
    /**
     * 获取触发器名称
     * */
    String getName();

    /**
     * 初始化操作
     * */
    void setup();

    /**
     * 触发回调，每次变化的时候需要调用，不然不会将结果派发
     * @param result 结果,用对象封装起来
     * */
    void trigger(Object result);

    /**
     * 强制出发
     * */
    void forceTrigger();

    /**
     * 停止触发器
     * */
    void stopTrigger();
}
