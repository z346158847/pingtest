package cn.woconnect.pingtest.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.ScheduledLockConfiguration;
import net.javacrumbs.shedlock.spring.ScheduledLockConfigurationBuilder;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.time.Duration;

/**
 * ShedLock是一个悲观锁，而无论悲观锁还是乐观锁，他的实现，必须借助于公共存储。
 *
 *
 * @author wjzhang
 * @date 2019/10/29  18:52
 */
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class ScheduledConfig {

    /**
     * 需要初始化Provider支持。操作数据库。
     * @param dataSource
     * @return
     */
    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource);
    }

//    @Bean
//    public ScheduledLockConfiguration scheduledLockConfiguration(LockProvider lockProvider) {
//        return ScheduledLockConfigurationBuilder
//                .withLockProvider(lockProvider)
//                .withPoolSize(10)
//                .withDefaultLockAtMostFor(Duration.ofMinutes(10))
//                .build();
//    }
}