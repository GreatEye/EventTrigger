package cn.appleye.eventtrigger.utils;

import android.util.Log;

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
 * 日志工具类
 */

public class LogUtil {
    private static boolean LOGGABLE = true;

    /**tag最大长度23*/
    private static final int TAG_MAX_LEN = 23;

    public static void enble(boolean loggable) {
        LOGGABLE = loggable;
    }

    public static void i(String tag, String message) {
        if(!LOGGABLE) {
            return;
        }

        if(tag.length() > TAG_MAX_LEN) {
            tag = tag.substring(0, TAG_MAX_LEN);
        }

        Log.i(tag, message);
    }

    public static void d(String tag, String message) {
        if(!LOGGABLE) {
            return;
        }

        if(tag.length() > TAG_MAX_LEN) {
            tag = tag.substring(0, TAG_MAX_LEN);
        }

        Log.d(tag, message);
    }

    public static void e(String tag, String message) {
        if(!LOGGABLE) {
            return;
        }

        if(tag.length() > TAG_MAX_LEN) {
            tag = tag.substring(0, TAG_MAX_LEN);
        }

        Log.e(tag, message);
    }
}
