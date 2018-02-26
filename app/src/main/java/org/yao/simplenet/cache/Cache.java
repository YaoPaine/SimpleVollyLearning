package org.yao.simplenet.cache;

import org.yao.simplenet.base.Request;
import org.yao.simplenet.base.Response;

public interface Cache<T, M> {

    Response get(Request<?> request);

    void put(Request<?> request, Response response);
}
