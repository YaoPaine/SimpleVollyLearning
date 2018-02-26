package org.yao.simplenet.core;

import android.os.Handler;
import android.os.Looper;
import org.yao.simplenet.base.Request;
import org.yao.simplenet.base.Response;

import java.util.concurrent.Executor;

public class ResponseDelivery implements Executor {
    /**
     * 主线程handler
     */
    private Handler mResponseHandler = new Handler(Looper.getMainLooper());

    public void deliveryResponse(final Request<?> request, final Response response) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                request.deliveryResponse(response);
            }
        };
        execute(runnable);
    }

    @Override
    public void execute(Runnable command) {
        mResponseHandler.post(command);
    }
}
