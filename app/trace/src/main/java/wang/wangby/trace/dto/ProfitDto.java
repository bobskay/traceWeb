package wang.wangby.trace.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProfitDto {
    private Date stat;
    private Date end;
    private String type;
}