package cn.woconnect.pingtest.scheduler;


import cn.woconnect.pingtest.utils.PingUtils;
import cn.woconnect.pingtest.utils.SshUtils;
import com.jcraft.jsch.JSchException;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @author wjzhang
 * @date 2019/11/4  15:56
 */
@Component
public class SchedulerJob {

    /**
     * ping 所需参数
     */
    @Value("${woconnect.ip}")
    private String ip;
    @Value("${woconnect.timeout}")
    private int timeout;
    @Value("${woconnect.pingtimes}")
    private int pingtimes;

    /**
     * ssh 所需参数
     */
    @Value("${woconnect.host}")
    private String host;
    @Value("${woconnect.port}")
    private int port;
    @Value("${woconnect.username}")
    private String username;
    @Value("${woconnect.password}")
    private String password;

    private static Logger logger = LoggerFactory.getLogger(SchedulerJob.class);
    private static boolean isOpenStart = false;

    /**
     * name参数必须是唯一的
     * lockAtLeastFor  单位ms
     * lockAtLeastForString，以便我们可以在方法调用之间产生时间间隔。使用“PT5M”意味着此方法至少要锁定5分钟。
     * lockAtMostFor  单位ms
     * lockAtMostForString来指定在执行节点完成时应该保留多长时间。使用“PT14M”意味着它将被锁定不超过14分钟。
     */
    @Scheduled(cron = "0/1 * * * * ? ")
    @SchedulerLock(name = "scheduledTaskPing", lockAtMostFor = 30 * 1000)
    public void pingTest() {
        if (PingUtils.ping(ip, pingtimes, timeout)) {
            return;
        }

        logger.info("网络不通，切换");
        try {
            SshUtils.execCommandByShell(username, host, port, password);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }


}
