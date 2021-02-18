package wang.wangby.tools.http.interceptor;

import org.apache.http.client.methods.HttpUriRequest;

public interface HttpRequestInterceptor {
    void intercept(HttpUriRequest httpUriRequest);

    //请求头加上json标识
    static HttpRequestInterceptor jsonRequestInterceptor(){
        return request->{
          request.addHeader("Content-Type","application/json; charset=utf-8");
        };
    }

    //请求头加上权限验证的token
    static HttpRequestInterceptor bearer(String token){
        return request->{
            request.addHeader("Authorization","Bearer "+token);
        };
    }
}
