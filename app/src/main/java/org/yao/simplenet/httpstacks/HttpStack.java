package org.yao.simplenet.httpstacks;

import org.yao.simplenet.base.Request;
import org.yao.simplenet.base.Response;

public interface HttpStack {

    Response performRequest(Request<?> request);

}
