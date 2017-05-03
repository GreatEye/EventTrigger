package cn.appleye.eventtrigger.subscriber;

import java.util.ArrayList;
import java.util.List;

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
 */

public class SubscriberMethodFinder {
    /**
     * 获取当前类的注册过触发器的方法信息
     * @param subscriber 订阅者
     * @return 方法信息
     * */
    public List<SubscriberMethod> findSubscriberMethod(Class<?> subscriber) {
        List<SubscriberMethod> subscriberMethodList = new ArrayList<>();

        return subscriberMethodList;
    }
}
