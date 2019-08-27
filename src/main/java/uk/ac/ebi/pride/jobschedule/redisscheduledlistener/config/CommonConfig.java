package uk.ac.ebi.pride.jobschedule.redisscheduledlistener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.pride.jobschedule.redisscheduledlistener.command.handler.DefaultCommandResultHandler;
import uk.ac.ebi.pride.jobschedule.redisscheduledlistener.command.runner.DefaultCommandRunner;

/**
 * @author Suresh Hewapathirana
 */
@Configuration
public class CommonConfig {

    @Bean
    DefaultCommandResultHandler commandResultHandler(){
        return new DefaultCommandResultHandler();
    }

    @Bean
    public DefaultCommandRunner commandRunner(DefaultCommandResultHandler commandResultHandler){
        return new DefaultCommandRunner(commandResultHandler);
    }
}

