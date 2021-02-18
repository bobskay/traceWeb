package wang.wangby.tools.http.sync;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

@Slf4j
public class HttpResponseCallback implements FutureCallback<HttpResponse> {
    private volatile Future<HttpResponse> responseFuture;
    private Function<HttpResponse,Object> responseToConsumer;
    private volatile Object result;

    public HttpResponseCallback(Function<HttpResponse,Object> responseToConsumer) {
        this.responseToConsumer = responseToConsumer;
    }

    @SneakyThrows
    public void completed(HttpResponse httpResponse) {
        result=responseToConsumer.apply(httpResponse);
    }

    public void failed(Exception ex) {
        result = ex;
    }

    public void failedResult(Object ex) {
        result = ex;
    }

    public void cancelled() {

    }

    public Object getResult() {
        return result;
    }

    public Object waitResult(long timeout,TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
        if(result!=null){
            return result;
        }
        if(responseFuture==null){
           return "responseFuture=null:";
        }
        responseFuture.get(timeout, timeUnit);
        return result;
    }

    public void  setResponseFuture(Future<HttpResponse> responseFuture){
        this.responseFuture=responseFuture;
    }

    public boolean cancel() {
        if(responseFuture!=null){
            return responseFuture.cancel(true);
        }
        return true;
    }
}
