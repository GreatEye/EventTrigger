package cn.appleye.eventtrigger.triggers.timer;;

import android.os.Handler;
import android.os.Message;

import cn.appleye.eventtrigger.observer.Observer;
import cn.appleye.eventtrigger.triggers.AbstractTrigger;

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
 * @date 2017/5/8
 * 计时器触发器
 */

public class TimerTrigger extends AbstractTrigger{
    private static final String TAG = "TimerTrigger";

    /**时间间隔*/
    private int mInterval;
    /**标记是否结束*/
    private boolean mIsFinished;

    private static final int MSG_DELAY = 1000;

    /**发送延迟消息Handler*/
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DELAY:{
                    if(!mIsFinished){
                        dispatch(null);//派发结果
                        mHandler.sendEmptyMessageDelayed(MSG_DELAY, mInterval);
                    }
                    break;
                }
            }
        }
    };

    /**
     * 构造方法
     * @param interval 时间间隔
     * */
    public TimerTrigger(Observer observer, int interval) {
        super(observer);

        mInterval = interval<0?100:interval;//小于0时，定义为100ms

        mIsFinished = false;
    }

    @Override
    public String getName() {
        return "TimerTrigger";
    }

    @Override
    public void setup() {
        if(mIsFinished) {
            throw new IllegalStateException("the timer has been stopped");
        }

        mHandler.sendEmptyMessage(MSG_DELAY);
    }

    @Override
    public void forceTrigger() {
        //移除延迟消息
        mHandler.removeMessages(MSG_DELAY);
        //立刻执行
        mHandler.sendEmptyMessage(MSG_DELAY);
    }

    @Override
    public void stopTrigger() {
        mIsFinished = true;
    }
}
