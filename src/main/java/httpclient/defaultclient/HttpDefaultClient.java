package httpclient.defaultclient;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengyong on 17/1/3.
 */
public class HttpDefaultClient {

    // 字符编码
    private static final String        CHART_SET = "UTF-8";

    // http client instance
    private static volatile CloseableHttpClient httpclient;

    public static void create() {
        if (httpclient == null) {
            synchronized (HttpDefaultClient.class) {
                if(httpclient == null) {
                    httpclient = HttpClients.createDefault();
                }
            }
        }
    }

    public static void close() throws IOException {
        if (httpclient != null) {
            httpclient.close();
        }
    }

    /**
     * post 请求
     * 
     * @param postUrl 请求地址
     * @param listParams post请求参数
     * @return 返回结果
     * @throws IOException
     */
    public static String post(String postUrl, List<NameValuePair> listParams) throws IOException {
        HttpPost httpPost = new HttpPost(postUrl);
        httpPost.setEntity(new UrlEncodedFormEntity(listParams));
        CloseableHttpResponse response = httpclient.execute(httpPost);

        return handleResponse(response);
    }

    /**
     * get 请求
     * 
     * @param getUrl 请求地址
     * @param parametersMap get请求参数
     * @return 返回结果
     * @throws IOException
     */
    public static String get(String getUrl, Map<String, Object> parametersMap) throws IOException {
        String url = buildUri(getUrl, parametersMap).toString();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);

        return handleResponse(response);
    }

    /***
     * 拼接get请求调用的URL
     * 
     * @param url 调用请求
     * @param parametersMap 业务数据，作为参数
     * @return 完成url
     */
    private static URI buildUri(String url, Map<String, Object> parametersMap) {
        if (MapUtils.isEmpty(parametersMap)) {
            return URI.create(url);
        }
        ArrayList list = new ArrayList(parametersMap.size());
        Iterator iterator = parametersMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            list.add(entry.getKey().toString().trim() + "=" + entry.getValue().toString().trim());
        }
        return list.isEmpty() ? URI.create(url) : URI.create(url + "?" + StringUtils.join(list, "&"));
    }

    /**
     * 获取结果
     * 
     * @param response 响应
     * @return 返回结果
     * @throws IOException
     */
    private static String handleResponse(CloseableHttpResponse response) throws IOException {
        ResponseHandler<String> handler = new BasicResponseHandler();
        return handler.handleResponse(response);
    }

    /**
     * 获取结果(similar like handleResponse method)
     *
     * @param httpRequest 请求
     * @param response 响应
     * @return 返回结果
     * @throws IOException
     */
    @Deprecated
    private static String getResult(HttpRequestBase httpRequest, CloseableHttpResponse response) throws IOException {

        if (httpRequest == null || response == null) {
            throw new NullPointerException(String.format("请求发生空指针异常"));
        }

        String result = null;
        try {
            // 非200, 不做处理
            int code = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != code) {
                httpRequest.abort();
                throw new RuntimeException(String.format("请求发生%s异常", code));
            }

            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, Charset.forName(CHART_SET));
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        return result;
    }

}
