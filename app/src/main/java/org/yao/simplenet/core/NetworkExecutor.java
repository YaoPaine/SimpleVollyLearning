package org.yao.simplenet.core;

import android.util.Log;
import org.yao.simplenet.base.Request;
import org.yao.simplenet.base.Response;
import org.yao.simplenet.cache.Cache;
import org.yao.simplenet.cache.LruMemCache;
import org.yao.simplenet.httpstacks.HttpStack;

import java.util.concurrent.BlockingQueue;

public class NetworkExecutor extends Thread {
    /**
     * 网络请求队列
     */
    private BlockingQueue<Request<?>> mRequestQueue;

    /**
     * 网络请求栈
     */
    private HttpStack mHttpStack;
    /**
     * 请求缓存
     */
    private Cache<String, Response> mReqCache = new LruMemCache<>();

    /**
     * 结果分发器,将结果投递到主线程
     */
    private ResponseDelivery mResponseDelivery;

    /**
     * 是否停止
     */
    private boolean isStop = false;

    public NetworkExecutor(BlockingQueue<Request<?>> queue, HttpStack httpStack) {
        this.mRequestQueue = queue;
        this.mHttpStack = httpStack;
    }

    @Override
    public void run() {
        super.run();
        try {
            while (!isStop) {
                Request<?> request = mRequestQueue.take();
                if (request.isCanceled()) {
                    Log.d("###", "###: 请求已经被取消了");
                    continue;
                }
                Response response = null;
                if (isUseCache(request)) {

                } else {
                    response = mHttpStack.performRequest(request);
                    if (isSuccess(response)) {
                        mReqCache.put(request, response);
                    }
                }
                mResponseDelivery.deliveryResponse(request, response);
            }
        } catch (InterruptedException e) {
            Log.i("", "### 请求分发器退出");
        }
    }

    private boolean isSuccess(Response response) {
        return response != null && response.getStatusLine().getStatusCode() == 200;
    }

    private boolean isUseCache(Request<?> request) {
        return request.isShouldCache() && mReqCache.get(request) != null;
    }

    public void quit() {
        isStop = true;
        interrupt();
    }
}
