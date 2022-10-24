package priv.hb.sample.controller;

// import com.yangt.hirac.core.component.AdminOperations;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调度系统metric的restful接口
 */
@RestController
@RequestMapping("/")
public class MetricController {

    // @Autowired
    // AdminOperations adminOperations;

    @GetMapping(value="/metrics", produces="text/plain; version=0.0.4; charset=utf-8")
    public String getMetric() {
        String waitSize =
                        "# HELP waiting_job_inQueue help\n" +
                        "# TYPE waiting_job_inQueue gauge\n" +
                        "waiting_job_inQueue{queue=\"准实时\",} 0\n" +
                        "waiting_job_inQueue{queue=\"离线\",} 0\n" +
                        "waiting_job_inQueue{queue=\"手动\",} 0\n";
        String runningSize =
                "# HELP running_job help\n" +
                "# TYPE running_job gauge\n" +
                "running_job{queue=\"准实时\",} 0\n" +
                "running_job{queue=\"离线\",} 1\n" +
                "running_job{queue=\"手动\",} 0\n";
        return waitSize + runningSize;
    }

    @GetMapping(value="/metrics2", produces="text/plain; version=0.0.4; charset=utf-8")
    public String getMetric2() {
        // String waitSize = adminOperations.getWaitQueueSize();
        // String runningSize = adminOperations.getRunningJobSize();
        return "waitSize + runningSize";
    }

    @GetMapping("/metrics3")
    public String getMetric3() {
        // String waitSize = adminOperations.getWaitQueueSize();
        // String runningSize = adminOperations.getRunningJobSize();
        return "waitSize + runningSize";
    }

    @GetMapping("/metrics4")
    public String getMetric4() {
        // String waitSize = adminOperations.getWaitQueueSize();
        // String runningSize = adminOperations.getRunningJobSize();
        return "waitSize + runningSize";
    }
}
