package org.yao.simplenet.cache;

import android.support.v4.util.LruCache;

import org.yao.simplenet.base.Request;
import org.yao.simplenet.base.Response;

public class LruMemCache implements Cache<Request<?>, Response> {

    public LruCache<Request<?>, Response> mRequestCache;

    public LruMemCache() {
        long runtimeMemory = Runtime.getRuntime().maxMemory() / 1024;
        int cacheSize = (int) (runtimeMemory / 8);
        mRequestCache = new LruCache<Request<?>, Response>(cacheSize) {
            @Override
            protected int sizeOf(Request<?> key, Response value) {
                return value.rawData.length / 1024;
            }
        };
    }

    @Override
    public Response get(Request<?> key) {
        return mRequestCache.get(key);
    }

    @Override
    public void put(Request<?> key, Response value) {
        mRequestCache.put(key, value);
    }

    @Override
    public void remove(Request<?> key) {
        mRequestCache.remove(key);
    }
}
