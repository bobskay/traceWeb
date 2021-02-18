package wang.wangby.tools.http.sync;

import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.IOReactorException;
import wang.wangby.tools.http.HttpConfig;
import wang.wangby.tools.thread.NamedThreadPoolFactory;

import java.util.concurrent.ThreadFactory;

public class HttpAsyncClientProvider {


    //获得异步http请求的客户端
    public HttpAsyncClient newClient(HttpConfig httpConfig) throws IOReactorException {
        return newClient(httpConfig, true);
    }

    public HttpAsyncClient newClient(HttpConfig httpConfig, boolean start) throws IOReactorException {
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().build();
        ThreadFactory threadFactory = new NamedThreadPoolFactory(this.getClass().getSimpleName());
        DefaultConnectingIOReactor connectingIOReactor = new DefaultConnectingIOReactor(ioReactorConfig, threadFactory);

        Registry registry = RegistryBuilder.create().register("http", NoopIOSessionStrategy.INSTANCE)
                .register("https", SSLIOSessionStrategy.getDefaultStrategy()).build();
        //连接池管理
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(connectingIOReactor, registry);
        connManager.setMaxTotal(httpConfig.getMaxTotal());
        connManager.setDefaultMaxPerRoute(httpConfig.getDefaultMaxPerRoute());
        if (httpConfig.getMaxPerRoute() != null) {
            httpConfig.getMaxPerRoute().forEach((route, count) -> {
                connManager.setMaxPerRoute(route, count);
            });
        }
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(httpConfig.getConnectTimeout())         //连接超时时间
                .setSocketTimeout(httpConfig.getSocketTimeout())          //读超时时间（等待数据超时时间）
                .setConnectionRequestTimeout(httpConfig.getConnectionRequestTimeout())    //从池中获取连接超时时间
                .build();

        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        CloseableHttpAsyncClient closeableHttpAsyncClient = HttpAsyncClients.custom()
                .setConnectionManager(connManager)             //连接管理器
                //.setProxy(new HttpHost("myproxy", 8080))       //设置代理
                .setDefaultRequestConfig(defaultRequestConfig) //默认请求配置
                .setRedirectStrategy(redirectStrategy)               //重试策略
                .build();
        if (start) {
            closeableHttpAsyncClient.start();
        }
        HttpAsyncClient client = new HttpAsyncClient(closeableHttpAsyncClient);
        return client;
    }
}
