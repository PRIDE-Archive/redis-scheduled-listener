package uk.ac.ebi.pride.jobschedule.redisscheduledlistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.ebi.pride.jobschedule.redisscheduledlistener.redis.RedisListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Suresh Hewapathirana
 */
@Component
@Slf4j
public class ScheduledTasks {

    @Autowired
    RedisListener redisListener;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Scheduled(fixedRate =120000) // check 2 min
    public void reportCurrentTime() {
        log.info("----------------------- {} -----------------------", dateFormat.format(new Date()));
        redisListener.runNextJobBatch();
    }
}
