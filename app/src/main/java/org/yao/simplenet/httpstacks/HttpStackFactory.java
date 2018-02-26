package org.yao.simplenet.httpstacks;

import android.os.Build;

public class HttpStackFactory {

    public static HttpStack createHttpStack() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            return new HttpClientStack();
        }
        return new HttpUrlConnStack();
    }
}
