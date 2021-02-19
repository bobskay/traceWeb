package wang.wangby.trace.config;

import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wang.wangby.utils.StringUtil;


@Component
@Slf4j
public class PermissionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (!check(request)) {
            response.setStatus(401);
            response.setHeader("Cache-Control", "no-store");
            response.setDateHeader("Expires", 0);
            response.setHeader("WWW-authenticate", "Basic Realm=\"test\"");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }


    private boolean check(HttpServletRequest request) {
        String author = request.getHeader("Authorization");
        if (StringUtil.isEmpty(author)) {
            // log.debug("用户为空");
            return false;
        }
        String[] basic = author.split(" ");
        if (basic.length != 2) {
            return false;
        }
        String nameAndPwd = decode(basic[1]);
        //log.debug("解密后："   nameAndPwd);
        if (!MarketConfig.PASSWORD.equals(nameAndPwd)) {
            return false;
        }
        return true;
    }

    private String decode(String s) {
        Decoder decoder = Base64.getDecoder();
        try {
            byte[] b = decoder.decode(s);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }

}
