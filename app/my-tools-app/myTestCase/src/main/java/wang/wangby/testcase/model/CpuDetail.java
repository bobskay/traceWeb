package wang.wangby.testcase.model;

import lombok.Data;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import wang.wangby.annotation.Remark;

import java.util.List;

@Data
public class CpuDetail {

    @Remark("核数")
    private int size;
    @Remark("基本信息")
    private List<CpuInfo> cpuInfoList;
    @Remark("使用率")
    private List<CpuPerc> percList;
    @Remark("总使用率")
    private CpuPerc cpuTotal;
}
