package wang.wangby.web.webfilter.permission;


import wang.wangby.template.TemplateUtil;
import wang.wangby.web.MenuProvider;
import wang.wangby.web.dto.MenuInfo;
import wang.wangby.web.webfilter.WebFilter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PermissionFilter implements WebFilter<String> {

    PermissionUserService userService;
    private String[] excludes;
    TemplateUtil templateUtil;
    Set<String> jsonUrl;

    public PermissionFilter(TemplateUtil templateUtil, MenuProvider menuProvider, PermissionUserService userService, String[] excludes){
        this.userService=userService;
        this.excludes=excludes;
        this.templateUtil=templateUtil;
        jsonUrl=new HashSet<>();
        for(MenuInfo m:menuProvider.getMappings()){
            if(m.isJson()){
                jsonUrl.add(m.getUrl());
            }
        }
    }


    @Override
    public String begin(HttpServletRequest request, HttpServletResponse httpResponse) throws Exception {
        String uri=request.getRequestURI();
        for(String exclude:excludes){
            if(exclude.equalsIgnoreCase(uri)){
                return "";
            }
        }

        Token token=getToken(request);
        if(token==null||token.isExpired()){
            toLogin(request,httpResponse);
            return null;
        }
        PermissionUser user=userService.getUser(token.getUserName());
        if(!user.hasPermission(uri)){
            toPermissionDeny(user,request,httpResponse);
            return null;
        }
        CurrentUserHolder.setUser(user);
        return "";
    }

    private void toPermissionDeny(PermissionUser user,HttpServletRequest request, HttpServletResponse httpResponse) throws IOException {
        Map map=new HashMap<>();
        map.put("user",user);
        map.put("uri",request.getRequestURI());
        if(isJson(request)){
            httpResponse.setContentType("application/json; charset=utf-8");
            String text=templateUtil.parseTemplate("/error/403.json",map);
            httpResponse.getOutputStream().write(text.getBytes(StandardCharsets.UTF_8));
        }else{
            httpResponse.setContentType("text/html; charset=utf-8");
            String text=templateUtil.parseTemplate("/error/403.html",map);
            httpResponse.getOutputStream().write(text.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void toLogin(HttpServletRequest request, HttpServletResponse httpResponse) throws IOException, ServletException {
        if(isJson(request)){
            httpResponse.setContentType("application/json; charset=utf-8");
           String text= templateUtil.parseTemplate("/error/login.json",new HashMap());
            httpResponse.getOutputStream().write(text.getBytes(StandardCharsets.UTF_8));
        }else{
            request.getRequestDispatcher("/login/prepareLogin").forward(request,httpResponse);
        }

    }

    private boolean isJson(HttpServletRequest request){
        return jsonUrl.contains(request.getRequestURI());
    }

    @Override
    public void end(String s, HttpServletRequest request) throws Exception {
        CurrentUserHolder.clear();
    }

    private Token getToken(HttpServletRequest request){
        String tokenId=request.getHeader("Authorization");
        if(tokenId!=null&&tokenId.startsWith("Bearer")){
            Token token=Token.getToken(tokenId.substring("Bearer".length()+1).trim());
            if(token!=null){
                return token;
            }
        }
        Cookie[] cookies= request.getCookies();
        if(cookies==null){
            return null;
        }
        for(Cookie ck:cookies){
            if(Token.COOKIE_NAME.equalsIgnoreCase(ck.getName())){
                Token token=Token.getToken(ck.getValue());
                if(token!=null){
                    return token;
                }
                break;
            }
        }
       return null;
    }
}
