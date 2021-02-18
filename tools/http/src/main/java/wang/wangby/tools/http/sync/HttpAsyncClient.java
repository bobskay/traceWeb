package wang.wangby.tools.http.sync;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import wang.wangby.tools.http.HttpRequestMethod;
import wang.wangby.tools.http.HttpUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class HttpAsyncClient {

    private CloseableHttpAsyncClient closeableHttpAsyncClient;

    public HttpAsyncClient(CloseableHttpAsyncClient closeableHttpAsyncClient) {
        this.closeableHttpAsyncClient=closeableHttpAsyncClient;
    }

    public void execute(HttpUriRequest request, HttpResponseCallback callback){
        try{
            Future<HttpResponse> future=closeableHttpAsyncClient.execute(request,callback);
            callback.setResponseFuture(future);
        }catch (RuntimeException ex){
            callback.failed(ex);
            log.error(ex.getMessage());
            throw ex;
        }
    }


    public HttpResponse doGet(String url) throws ExecutionException, InterruptedException {
        HttpUriRequest request= HttpUtil.createRequest(url, HttpRequestMethod.GET,null,null);
        return closeableHttpAsyncClient.execute(request,null).get();
    }
}
