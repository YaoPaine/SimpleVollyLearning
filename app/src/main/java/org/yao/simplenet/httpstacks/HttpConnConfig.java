package org.yao.simplenet.httpstacks;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public class HttpConnConfig {

    public int getConnectTimeout() {
        return -1;
    }

    public int getReadTimeout() {
        return -1;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return null;
    }

    public HostnameVerifier getHostnameVerifier() {
        return null;
    }
}
