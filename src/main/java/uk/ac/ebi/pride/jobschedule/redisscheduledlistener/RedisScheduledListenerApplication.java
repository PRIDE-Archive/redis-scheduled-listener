package uk.ac.ebi.pride.jobschedule.redisscheduledlistener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RedisScheduledListenerApplication {

  public static void main(String[] args) {
    SpringApplication.run(RedisScheduledListenerApplication.class, args);
  }
}
