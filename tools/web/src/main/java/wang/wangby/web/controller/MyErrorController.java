package wang.wangby.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import wang.wangby.annotation.Remark;
import wang.wangby.exception.Message;
import wang.wangby.log.LogUtil;
import wang.wangby.utils.DateTime;
import wang.wangby.web.dto.ErrorInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//全局异常处理类
@RestController
@ControllerAdvice
@Slf4j
@Remark("异常处理controller")
public class MyErrorController extends BaseController implements ErrorController {

    @ResponseBody
    @RequestMapping("/error")
    public Object error(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ErrorInfo errorInfo=createError(request);
        if(isJsonRequest(errorInfo)){
            return errorInfo;
        }
        return $("error/404",errorInfo);
    }

    @ExceptionHandler()
    @ResponseBody
    public Object handleException(HttpServletRequest request, Exception e){
        ErrorInfo errorInfo=createError(request);
        Throwable th= LogUtil.getCause(e);
        if(th instanceof Message){
            errorInfo.setError(e.getMessage());
        }else{
            log.error("未知异常",e);
            errorInfo.setError(LogUtil.getExceptionText(e));
        }

        if(isJsonRequest(errorInfo)){
            errorInfo.setError(th.getMessage());
            return errorInfo;
        }
        return $("error/500",errorInfo);
    }

    private boolean isJsonRequest(ErrorInfo errorInfo){
        if(errorInfo.getContentType()==null){
            return false;
        }
        return errorInfo.getContentType().toLowerCase().indexOf("application/json")!=-1;
    }

    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception var4) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }

    private ErrorInfo createError(HttpServletRequest request){
        HttpStatus status = this.getStatus(request);
        ErrorInfo errorInfo=new ErrorInfo();
        errorInfo.setStatus(status.value());
        errorInfo.setStatusMessage(status.getReasonPhrase());
        errorInfo.setPath(request.getRequestURI());
        errorInfo.setContentType(request.getContentType());
        errorInfo.setTimestamp(DateTime.current()+"");
        return errorInfo;
    }

    /**
     * 实现错误路径,暂时无用
     */
    @Override
    public String getErrorPath() {
        return "";
    }

}