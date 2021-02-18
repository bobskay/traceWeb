package wang.wangby.gupiao;

import lombok.Data;

@Data
public class GamblingResult {
    int success;
    int failue;
    int beginMoney;
    int endMoney;
    int empty;
    int maxPer;
}
