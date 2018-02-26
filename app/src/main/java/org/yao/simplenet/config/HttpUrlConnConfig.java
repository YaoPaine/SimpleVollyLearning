package org.yao.simplenet.config;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * @Description
 * @AuthorCreated yaopaine
 * @Version 1.0
 * @Time 2/26/18
 */

public class HttpUrlConnConfig {

    private static HttpUrlConnConfig mConfig = new HttpUrlConnConfig();

    private SSLSocketFactory sslSocketFactory;

    private HostnameVerifier hostnameVerifier;

    private HttpUrlConnConfig() {

    }

    public static HttpUrlConnConfig getInstance() {
        return mConfig;
    }

    public void sethHttpsConfig(SSLSocketFactory sslSocketFactory, HostnameVerifier hostnameVerifier) {
        this.sslSocketFactory = sslSocketFactory;
        this.hostnameVerifier = hostnameVerifier;
    }

    public int getConnectTimeout() {
        return -1;
    }

    public int getReadTimeout() {
        return -1;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }
}
