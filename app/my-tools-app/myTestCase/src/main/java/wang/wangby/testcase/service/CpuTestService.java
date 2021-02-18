package wang.wangby.testcase.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.wangby.config.Beans;
import wang.wangby.testcase.controller.vo.CpuTestInfo;
import wang.wangby.tools.thread.ThreadPool;
import wang.wangby.tools.thread.ThreadPoolConfig;

@Service
public class CpuTestService implements InitializingBean {

    @Autowired
    Beans beans;

    ThreadPool pool;
    volatile boolean running = false;
    volatile int taskCount = 0;

    public CpuTestInfo start(int taskCount) {
        //如果测试已经在执行,就直接返回
        if (running) {
            return getInfo();
        }
        running = true;
        this.taskCount = taskCount;
        for (int i = 0; i < taskCount; i++) {
            pool.run(() -> {
                int no = 0;
                while (running) {
                    no++;
                }
            });
        }
        return getInfo();
    }

    public CpuTestInfo getInfo() {
        CpuTestInfo info = new CpuTestInfo();
        info.setRunning(running);
        info.setTaskCount(taskCount);
        info.setPoolInfo(pool.getDataMap());
        return info;
    }

    public CpuTestInfo stop() {
        running = false;
        return getInfo();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = beans.newThreadPool(ThreadPoolConfig.newInstance("cpuTest", 10));
    }
}
