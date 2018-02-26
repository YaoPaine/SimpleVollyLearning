package org.yao.simplenet.httpstacks;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.yao.simplenet.base.Request;
import org.yao.simplenet.base.Response;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpUrlConnStack implements HttpStack {

    /**
     *
     */
    private HttpConnConfig mHttpConfig = new HttpConnConfig();

    @Override
    public Response performRequest(Request<?> request) {
        HttpURLConnection urlConnection = null;

        try {
            //构建HttpUrlConnection
            urlConnection = createUrlConnection(request.getUrl());
            //设置headers
            setRequestHeaders(urlConnection, request);
            //设置body参数
            setRequestBody(urlConnection, request);
            //config https
            configHttps(request);
            return fetchResponse(urlConnection);
        } catch (IOException e) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    private Response fetchResponse(HttpURLConnection connection) throws IOException {
        ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);
        int responseCode = connection.getResponseCode();
        if (responseCode == -1) {
            throw new IOException("不能获取正常的responseCode");
        }
        StatusLine statusLine = new BasicStatusLine(protocolVersion, responseCode, connection.getResponseMessage());
        //构建response
        Response response = new Response(statusLine);
        //设置response数据
        response.setEntity(entityFromURLConnection(connection));
        addHeadersToResponse(response, connection);
        return null;
    }

    private void addHeadersToResponse(Response response, HttpURLConnection connection) {
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        if (headerFields != null) {
            for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                Header header = new BasicHeader(entry.getKey(), entry.getValue().get(0));
                response.addHeader(header);
            }
        }
    }

    private HttpEntity entityFromURLConnection(HttpURLConnection connection) {
        BasicHttpEntity httpEntity = new BasicHttpEntity();
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            //e.printStackTrace();
            inputStream = connection.getErrorStream();
        }
        httpEntity.setContent(inputStream);
        httpEntity.setContentLength(connection.getContentLength());
        httpEntity.setContentEncoding(connection.getContentEncoding());
        httpEntity.setContentType(connection.getContentType());
        return httpEntity;
    }

    private void configHttps(Request<?> request) {
        if (request.isHttps()) {
            SSLSocketFactory sslSocketFactory = mHttpConfig.getSslSocketFactory();
            if (sslSocketFactory != null) {
                HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
                HttpsURLConnection.setDefaultHostnameVerifier(mHttpConfig.getHostnameVerifier());
            }
        }
    }

    private void setRequestBody(HttpURLConnection urlConnection, Request<?> request) throws IOException {
        Request.HttpMethod httpMethod = request.getHttpMethod();
        urlConnection.setRequestMethod(httpMethod.toString());

        byte[] body = request.getBody();
        if (body != null) {
            urlConnection.setDoOutput(true);
            urlConnection.addRequestProperty(Request.HEADER_CONTENT_TYPE, request.getBodyContentType());
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.write(body);
            outputStream.close();
        }
    }

    private void setRequestHeaders(HttpURLConnection urlConnection, Request<?> request) {
        Set<Map.Entry<String, String>> entrySet = request.getHeaders().entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            urlConnection.addRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private HttpURLConnection createUrlConnection(String url) throws IOException {
        URL u = new URL(url);
        URLConnection urlConnection = u.openConnection();
        urlConnection.setConnectTimeout(mHttpConfig.getConnectTimeout());
        urlConnection.setReadTimeout(mHttpConfig.getReadTimeout());
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);
        return (HttpURLConnection) urlConnection;
    }
}
