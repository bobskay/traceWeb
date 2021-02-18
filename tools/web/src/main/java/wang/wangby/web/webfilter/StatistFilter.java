package wang.wangby.web.webfilter;

import wang.wangby.tools.monitor.TimeMonitor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//统计请求耗时
public class StatistFilter implements WebFilter<Long> {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WebFilter.class);
    private List<String> ignoreUrl;//忽略打印日志的请求前缀,因为有些定时任务会一直调用
    private TimeMonitor timeMontor;

    public StatistFilter(List<String> ignoreUrl, TimeMonitor timeMontor) {
        log.debug("开始打印访问日志,忽略页面:" + ignoreUrl);
        this.ignoreUrl = ignoreUrl;
        this.timeMontor=timeMontor;
    }

    @Override
    public Long begin(HttpServletRequest request, HttpServletResponse httpResponse) throws IOException {
        String uri = request.getRequestURI();

        if (!log.isDebugEnabled()) {
            return System.currentTimeMillis();
        }
        for (String ig : ignoreUrl) {
            if (request.getRequestURI().startsWith(ig)) {
                return System.currentTimeMillis();
            }
        }
        uri += getParameters(request);
        log.debug("[" + request.getRemoteHost() + "]开始请求:" + uri);
        return System.currentTimeMillis();
    }

    //获取请求的参数
    private String getParameters(HttpServletRequest request) {
        StringBuilder param = new StringBuilder();
        param.append("?");
        request.getParameterMap().forEach((k, v) -> {
            if (v.length == 0) {
                param.append(k + "=");
            } else if (v.length == 1) {
                param.append(k + "=" + v[0]);
            } else {
                param.append(k + "=" + Arrays.asList(v));
            }
            param.append("&");
        });

        return param.substring(0, param.length() - 1);
    }


    @Override
    public void end(Long begin, HttpServletRequest request) {
        long time = System.currentTimeMillis() - begin;
        timeMontor.addRequest(request.getRequestURI(), time);
        if (!log.isDebugEnabled()) {
            return;
        }
        for (String ig : ignoreUrl) {
            if (request.getRequestURI().matches(ig)) {
                return;
            }
        }
        log.debug("访问结束"+request.getRequestURI()+time+"ma,remoteHost=" + request.getRemoteHost()+"\n");
    }
}
