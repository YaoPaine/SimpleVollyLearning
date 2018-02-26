package org.yao.simplenet.core;

import android.util.Log;
import org.yao.simplenet.httpstacks.HttpStack;
import org.yao.simplenet.httpstacks.HttpStackFactory;
import org.yao.simplenet.base.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求队列，采用优先队列，使得请求可以按照优先级处理
 */
public final class RequestQueue {
    /**
     * 优先队列
     */
    private BlockingQueue<Request<?>> mQueue = new PriorityBlockingQueue<>();
    /**
     * 请求的序列化生成器
     */
    private AtomicInteger mSerialNumGenerator = new AtomicInteger(0);

    /**
     * 默认的核心数
     */
    private static final int DEFAULT_CORE_NUM = Runtime.getRuntime().availableProcessors() + 1;
    /**
     * cpu核心数数+1个分发线程数
     */
    private int mDispatchNum = DEFAULT_CORE_NUM;
    /**
     * 执行网络请求的线程
     */
    private NetworkExecutor[] mDispatchers = null;
    /**
     * http请求的真正执行者
     */
    private HttpStack mHttpStack;

    protected RequestQueue(int coreNum, HttpStack httpStack) {
        this.mDispatchNum = coreNum;
        this.mHttpStack = httpStack != null ? httpStack : HttpStackFactory.createHttpStack();
    }

    private final void startNetworkExecutors() {
        mDispatchers = new NetworkExecutor[mDispatchNum];
        for (int i = 0; i < mDispatchNum; i++) {
            mDispatchers[i] = new NetworkExecutor(mQueue, mHttpStack);
            mDispatchers[i].start();
        }
    }

    private final void stopNetworkExecutors() {
        for (int i = 0; i < mDispatchNum; i++) {
            mDispatchers[i].quit();
        }
    }

    public void start() {
        stopNetworkExecutors();
        startNetworkExecutors();
    }

    public void addRequest(Request<?> request) {
        if (!mQueue.contains(request)) {
            request.setSerialNum(this.generateSerialNumber());
            mQueue.add(request);
        } else {
            Log.d("", "### 请求队列中已经含有" + request);
        }
    }

    public void clear() {
        mQueue.clear();
    }

    public BlockingQueue<Request<?>> getRequestQueue() {
        return mQueue;
    }

    private int generateSerialNumber() {
        return mSerialNumGenerator.incrementAndGet();
    }
}
