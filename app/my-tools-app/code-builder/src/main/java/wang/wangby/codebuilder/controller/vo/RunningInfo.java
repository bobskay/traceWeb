package wang.wangby.codebuilder.controller.vo;

import lombok.Data;

import java.util.List;
import java.util.Queue;

@Data
public class RunningInfo {
   private Queue<String> datas;
   private Data lastRead;
   private String appName;

}
