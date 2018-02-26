package org.yao.simplenet.base;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @param <T> 指定数据请求结果response的数据类型，string 或者json 或者xml
 */
public abstract class Request<T> implements Comparable<Request<T>> {

    /**
     * http请求方法枚举,这里我们只有GET, POST, PUT, DELETE四种
     */
    public static enum HttpMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        /**
         * http request type
         */
        private String mHttpMethod;

        HttpMethod(String method) {
            mHttpMethod = method;
        }

        @Override
        public String toString() {
            return mHttpMethod;
        }
    }

    /**
     * 优先级枚举
     */
    public static enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    /**
     * Default encoding for POST or PUT parameters. See
     * {@link #getParamsEncoding()}.
     */
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    /**
     * 请求优先级,默认设置NORMAL
     */
    protected Priority mPriority = Priority.NORMAL;
    /**
     * 请求序号，请求序号和请求优先级决定了请求的先后顺序
     */
    protected int mSerialNum = 0;
    /**
     * 请求是否取消
     */
    protected boolean isCancel = false;
    /**
     * 请求是否应该缓存
     */
    private boolean isShouldCache = true;

    private RequestListener<T> mListener;
    /**
     * 请求的url
     */
    private String mUrl = "";
    /**
     * 请求方法
     */
    private HttpMethod mHttpMethod = HttpMethod.POST;
    /**
     * 请求头
     */
    private HashMap<String, String> mHeaders = new HashMap<>();
    /**
     * 请求参数
     */
    private HashMap<String, String> mBodyParams = new HashMap<>();

    public Request(String url, HttpMethod method, RequestListener<T> listener) {
        this.mUrl = url;
        this.mHttpMethod = method;
        this.mListener = listener;
    }

    /**
     * 从原生的网络请求中解析请求响应
     */
    public abstract T parseResponse(Response response);

    /**
     * 运行在UI线程
     *
     * @param response
     */
    public final void deliveryResponse(Response response) {
        T t = parseResponse(response);
        if (mListener == null) return;
        int statusCode = response != null ? response.getStatusCode() : -1;
        String message = response != null ? response.getMessage() : "unkown error";
        mListener.onComplete(statusCode, t, message);
    }

    public String getUrl() {
        return mUrl;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority priority) {
        this.mPriority = priority;
    }

    public int getSerialNum() {
        return mSerialNum;
    }

    public void setSerialNum(int serialNum) {
        this.mSerialNum = serialNum;
    }

    public boolean isShouldCache() {
        return isShouldCache;
    }

    public void setShouldCache(boolean shouldCache) {
        isShouldCache = shouldCache;
    }

    protected static String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public HttpMethod getHttpMethod() {
        return mHttpMethod;
    }

    public HashMap<String, String> getHeaders() {
        return mHeaders;
    }

    public HashMap<String, String> getParams() {
        return mBodyParams;
    }

    public void cancel() {
        isCancel = true;
    }

    public boolean isCanceled() {
        return isCancel;
    }

    /**
     * @return post 或 put请求时的body参数字节数组
     */
    public byte[] getBody() {
        HashMap<String, String> bodyParams = getParams();
        if (bodyParams != null && bodyParams.size() > 0) {
            return encodeParameters(bodyParams, getParamsEncoding());
        }
        return null;
    }

    /**
     * @param params
     * @param encoding
     * @return 将参数转换为Url编码的参数串
     */
    private byte[] encodeParameters(HashMap<String, String> params, String encoding) {
        StringBuilder encodeParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                encodeParams.append(URLEncoder.encode(key, encoding));
                encodeParams.append("=");
                encodeParams.append(URLEncoder.encode(value, encoding));
                encodeParams.append("$");
            }
            return encodeParams.toString().getBytes();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + encoding, e);
        }
    }

    @Override
    public int compareTo(Request<T> o) {
        if (o == null) return -1;
        Priority priority = this.getPriority();
        Priority otherPriority = o.getPriority();
        return priority.equals(otherPriority) ? this.getSerialNum() - o.getSerialNum() : priority.ordinal() - otherPriority.ordinal();
    }

    /**
     * 请求完成的回调
     *
     * @param <T>
     */
    public interface RequestListener<T> {
        void onComplete(int code, T data, String message);
    }
}
