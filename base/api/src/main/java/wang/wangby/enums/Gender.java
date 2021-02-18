package wang.wangby.enums;

import lombok.Getter;
import wang.wangby.utils.Dictionary;

public enum  Gender implements Dictionary {
    male("男"),female("女");

    @Getter
    String text;
    Gender(String text){
        this.text=text;
    }

}