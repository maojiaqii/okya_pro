package top.okya.component.utils.common;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: maojiaqi
 * @Date: 2021/1/27 15:16
 * @describe： http请求工具类
 */
@Slf4j
public class HttpUtil {

    private static final int CONNECT_TIMEOUT = 35000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 35000;
    private static final int SOCKET_TIMEOUT = 60000;

    private CloseableHttpClient httpClient = null;
    private CloseableHttpResponse httpResponse = null;
    private String result = null;

    private String url = null;
    private Map<String, String> headers = null;

    public HttpUtil(String url, Map<String, String> headers) {
        this.url = url;
        this.headers = headers;
    }

    public HttpUtil(String url) {
        this.url = url;
    }

    public String doGet() {

        if (url != null) {
            try {
                log.info("http GET 请求开始; [url={}]", url);
                // 通过址默认配置创建一个httpClient实例
                httpClient = HttpClients.createDefault();
                // 创建httpGet远程连接实例
                HttpGet httpGet = new HttpGet(url);
                // 设置请求头信息，鉴权
                if (headers != null) {
                    Set<String> keys = headers.keySet();
                    for (Iterator<String> i = keys.iterator(); i.hasNext(); ) {
                        String key = (String) i.next();
                        httpGet.addHeader(key, headers.get(key));

                    }
                }
                // 设置配置请求参数
                RequestConfig requestConfig = RequestConfig.custom()
                        // 连接主机服务超时时间
                        .setConnectTimeout(CONNECT_TIMEOUT)
                        // 请求超时时间
                        .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                        // 数据读取超时时间
                        .setSocketTimeout(SOCKET_TIMEOUT)
                        .build();
                // 为httpGet实例设置配置
                httpGet.setConfig(requestConfig);
                // 执行get请求得到返回对象
                httpResponse = httpClient.execute(httpGet);
                // 通过返回对象获取返回数据
                HttpEntity entity = httpResponse.getEntity();
                // 通过EntityUtils中的toString方法将结果转换为字符串
                result = EntityUtils.toString(entity);
                log.info("请求结果： " + result);
            } catch (ClientProtocolException e) {
                log.error("Http GET 请求失败; url={}", url, e);
            } catch (IOException e) {
                log.error("Http GET 请求失败; url={}", url, e);
            } finally {
                // 关闭资源
                if (null != httpResponse) {
                    try {
                        httpResponse.close();
                    } catch (IOException e) {
                        log.error("关闭httpResponse资源失败; url={}", url, e);
                    }
                }
                if (null != httpClient) {
                    try {
                        httpClient.close();
                    } catch (IOException e) {
                        log.error("关闭httpClient资源失败; url={}", url, e);
                    }
                }
                log.info("关闭资源，结束本次通讯！");
            }
        }
        return result;
    }

    public String doPostJson(Map<String, Object> paramMap) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        log.info("http POST 请求开始; [url={}]", url);
        // 创建httpClient实例
        SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(
                SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
                NoopHostnameVerifier.INSTANCE);
        httpClient = HttpClients.custom().setSSLSocketFactory(scsf).build();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom()
                // 设置连接主机服务超时时间
                .setConnectTimeout(CONNECT_TIMEOUT)
                // 设置连接请求超时时间
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                // 设置读取数据连接超时时间
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头信息，鉴权
        if (headers != null) {
            Set<String> keys = headers.keySet();
            for (Iterator<String> i = keys.iterator(); i.hasNext(); ) {
                String key = (String) i.next();
                httpPost.addHeader(key, headers.get(key));

            }
        }
        httpPost.setHeader("Content-Type", "application/json");
        // 封装post请求参数
        if (null != paramMap && paramMap.size() > 0) {
            log.info("封装并设置post请求参数");
            // 为httpPost设置封装好的请求参数
            String pd = new JSONObject(paramMap).toJSONString();
            log.info("post请求参数：" + pd);
            httpPost.setEntity(new StringEntity(pd, "UTF-8"));
        }
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
            log.info("请求结果： " + result);
        } catch (ClientProtocolException e) {
            log.error("Http POST 请求失败; url={}", url, e);
        } catch (IOException e) {
            log.error("Http POST 请求失败; url={}", url, e);
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error("关闭httpResponse资源失败; url={}", url, e);
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.error("关闭httpClient资源失败; url={}", url, e);
                }
            }
            log.info("关闭资源，结束本次通讯！");
        }
        return result;
    }

    public String doPostDefualt(Map<String, Object> paramMap) {
        log.info("http POST 请求开始; [url={}]", url);
        // 创建httpClient实例
        httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom()
                // 设置连接主机服务超时时间
                .setConnectTimeout(CONNECT_TIMEOUT)
                // 设置连接请求超时时间
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                // 设置读取数据连接超时时间
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        if (headers != null) {
            Set<String> keys = headers.keySet();
            for (Iterator<String> i = keys.iterator(); i.hasNext(); ) {
                String key = (String) i.next();
                httpPost.addHeader(key, headers.get(key));

            }
        }
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        // 封装post请求参数
        if (null != paramMap && paramMap.size() > 0) {
            log.info("封装并设置post请求参数");
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            // 通过map集成entrySet方法获取entity
            Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
            // 循环遍历，获取迭代器
            Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> mapEntry = iterator.next();
                nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
            }
            // 为httpPost设置封装好的请求参数
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
            log.info("请求结果： " + result);
        } catch (ClientProtocolException e) {
            log.error("Http POST 请求失败; url={}", url, e);
        } catch (IOException e) {
            log.error("Http POST 请求失败; url={}", url, e);
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error("关闭httpResponse资源失败; url={}", url, e);
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.error("关闭httpClient资源失败; url={}", url, e);
                }
            }
            log.info("关闭资源，结束本次通讯！");
        }
        return result;
    }
}
