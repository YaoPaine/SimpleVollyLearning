package org.yao.simplenet.cache;

import org.yao.simplenet.base.Request;
import org.yao.simplenet.base.Response;

public class LruMemCache<T, M> implements Cache<T, M> {

    @Override
    public Response get(Request<?> request) {
        return null;
    }

    @Override
    public void put(Request<?> request, Response response) {

    }
}
