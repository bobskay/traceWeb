package wang.wangby.web.dto;

import lombok.Data;
import wang.wangby.annotation.Remark;

@Data
@Remark("请求的错误信息信息")
public class ErrorInfo {
    private String path;
    private String timestamp;
    private Integer status;
    private String error;
    private String statusMessage;
    private String contentType;
}
