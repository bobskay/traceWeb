package wang.wangby.exchange.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import lombok.extern.slf4j.Slf4j;
import wang.wangby.exchange.Api;
import wang.wangby.exchange.Constants;
import wang.wangby.exchange.response.ApiResponse;
import wang.wangby.exchange.response.ListResponse;
import wang.wangby.serialize.json.FastJsonImpl;
import wang.wangby.serialize.json.JsonUtil;
import wang.wangby.trace.config.MarketConfig;
import wang.wangby.utils.ClassUtil;

import java.util.function.Function;

@Slf4j
public class HttpClient {
    private String base;
    private ApiSignature apiSignature;
    private JsonUtil jsonUtil;

    public HttpClient() {
        this.apiSignature = new ApiSignature(MarketConfig.SECRET_KEY);
        this.base = Constants.API_BASE_URL;
        this.jsonUtil = new FastJsonImpl();
    }

    public <T extends ApiResponse> T get(Api api, UrlParamsBuilder builder) {
        return invoke(api, builder, HttpUtil::createGet);
    }

    public <T extends ApiResponse> T post(Api api, UrlParamsBuilder builder) {
        return invoke(api, builder, HttpUtil::createPost);
    }

    public <T extends ApiResponse> T delete(Api api, UrlParamsBuilder builder) {
        return invoke(api, builder, url -> {
            return HttpUtil.createRequest(Method.DELETE, url);
        });

    }


    public <T extends ApiResponse> T invoke(Api api, UrlParamsBuilder builder,
                                            HttpRequestSupply supply, Function<String, T> responseConsumer) {
        apiSignature.createSignature(builder);
        String requestUrl = api.url;
        if (!requestUrl.startsWith("http")) {
            requestUrl = base + requestUrl;
        }
        requestUrl += builder.buildUrl();
        log.info("开始请求：" + requestUrl);
        String resp = supply.create(requestUrl).header("X-MBX-APIKEY", MarketConfig.API_KEY).execute().body();
        log.info("请求返回：" + resp);

        try {
            T t = responseConsumer.apply(resp);
            t.setRequest(requestUrl);
            t.setResponse(resp);
            return t;
        } catch (Exception ex) {
            log.error("将结果转为java对象失败\nclass={}\nresp={}", api.response.getName(), resp, ex);
            throw new RuntimeException(ex);
        }


    }

    public <T extends ApiResponse> T invoke(Api api, UrlParamsBuilder builder, HttpRequestSupply supply) {
        return this.invoke(api, builder, supply, response -> {
            if (ListResponse.class.isAssignableFrom(api.response)) {
                ListResponse list = (ListResponse) ClassUtil.newInstance(api.response);
                list.addResult(response);
                return (T) list;
            } else {
                return (T) jsonUtil.toBean(response, api.response);
            }
        });
    }

    /**
     * 没有任何参数的请求，获取websocket的key时用到
     */
    public <T extends ApiResponse> T invoke(Api api, HttpRequestSupply supply) {
        String requestUrl = api.url;
        if (!requestUrl.startsWith("http")) {
            requestUrl = base + requestUrl;
        }
        log.info("开始请求：" + requestUrl);
        String resp = supply.create(api.url).header("X-MBX-APIKEY", MarketConfig.API_KEY).execute().body();
        log.info("请求返回：" + resp);
        T t = (T) jsonUtil.toBean(resp, api.response);
        t.setRequest(requestUrl);
        t.setResponse(resp);
        return t;
    }


    public interface HttpRequestSupply {
        HttpRequest create(String url);
    }
}
