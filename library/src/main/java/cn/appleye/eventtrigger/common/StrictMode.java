package cn.appleye.eventtrigger.common;

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
 * date 2017/5/7
 * 定义订阅方法对于触发器的严格模式
 */

public enum StrictMode {
    /**只有实现了{@link cn.appleye.eventtrigger.triggers.Trigger}接口才允许订阅*/
    STRICT,
    /**对触发器接口实现无要求*/
    ALOW_ALL
}
