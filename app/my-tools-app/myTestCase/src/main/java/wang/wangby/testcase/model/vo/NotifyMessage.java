package wang.wangby.testcase.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class NotifyMessage {
    private String title;
    private List<String> target;
    private String message;
}
