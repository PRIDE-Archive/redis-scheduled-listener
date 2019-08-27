package uk.ac.ebi.pride.jobschedule.redisscheduledlistener.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.JedisCluster;
import uk.ac.ebi.pride.jobschedule.redisscheduledlistener.command.runner.CommandRunner;
import uk.ac.ebi.pride.jobschedule.redisscheduledlistener.lsf.LSFManager;
import uk.ac.ebi.pride.jobschedule.redisscheduledlistener.command.builder.DefaultCommandBuilder;
import uk.ac.ebi.pride.jobschedule.redisscheduledlistener.command.builder.CommandBuilder;
import uk.ac.ebi.pride.jobschedule.redisscheduledlistener.message.model.AssayDataGenerationPayload;

import java.io.IOException;
import java.util.Collection;

/** @author Suresh Hewapathirana */
@Slf4j
public class RedisListener {

  JedisCluster jedisCluster;

  @Autowired private CommandRunner commandRunner;

  @Value("${lsf.max.pool.size}")
  private Integer maxJobPoolSize;

  @Value("${redis.assay.analyse.queue}")
  private String redisQueueName;

  @Value("${command.assay.analyse.command}")
  private String assayAnalyseCommand;

  public RedisListener(JedisCluster jedisCluster) {
    this.jedisCluster = jedisCluster;
  }

  /**
   * Based on the LSF availability, pick messages from redis and
   * launch the job
   */
  public void runNextJobBatch() {

    long numberOfRunningJobsInLsf;
    long numberOfJobsInRedisQueue;
    long batchSize;
    long lsfAvailableJobCapacity;

    numberOfJobsInRedisQueue = jedisCluster.llen(redisQueueName);
    log.info("Number of projects in the queue: " + numberOfJobsInRedisQueue);

    if (numberOfJobsInRedisQueue > 0) { // if there are jobs to run and queued in Redis

      numberOfRunningJobsInLsf = LSFManager.getNumberOfJobs("assay_analise-");
      log.info("Number of running jobs in LSF: " + numberOfRunningJobsInLsf);
      log.info("Number of max jobs run in LSF: " + maxJobPoolSize);

      if (numberOfRunningJobsInLsf < maxJobPoolSize) { // this means we can submit more jobs up-to the max limit
        lsfAvailableJobCapacity = maxJobPoolSize - numberOfRunningJobsInLsf;
        batchSize = (numberOfJobsInRedisQueue >= lsfAvailableJobCapacity) ? lsfAvailableJobCapacity : numberOfJobsInRedisQueue;
        log.info("{} of jobs are launching", batchSize);
        for (int i = 0; i < batchSize; i++) {
          final String message = jedisCluster.lpop(redisQueueName);
          runCommand(deserialiseMessage(message));
        }
      }
    }
  }

  /**
   * Run command to launch job to lsf
   *
   * @param dataGenerationPayload
   */
  private void runCommand(AssayDataGenerationPayload dataGenerationPayload) {

    CommandBuilder commandBuilder = new DefaultCommandBuilder();

    commandBuilder.argument(assayAnalyseCommand);
    // append project accession
    commandBuilder.argument("-p", dataGenerationPayload.getAccession());
    // append assay accession
    commandBuilder.argument("-a", dataGenerationPayload.getAssayAccession());

    Collection<String> command = commandBuilder.getCommand();
    log.info(command.toString());

    commandRunner.run(command);
  }

  /**
   * Convert the message into object
   *
   * @param value redis serialized message
   * @return deserialized message
   */
  private AssayDataGenerationPayload deserialiseMessage(String value) {
    ObjectMapper mapper = new ObjectMapper();
    AssayDataGenerationPayload assayDataGenerationPayload = null;
    try {
      assayDataGenerationPayload = mapper.readValue(value, AssayDataGenerationPayload.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return assayDataGenerationPayload;
  }
}
