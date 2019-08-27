package uk.ac.ebi.pride.jobschedule.redisscheduledlistener.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import uk.ac.ebi.pride.jobschedule.redisscheduledlistener.redis.RedisListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Suresh Hewapathirana
 */
@Slf4j
@Configuration
@PropertySource("classpath:application.properties")
public class RedisConfiguration {

    @Value("#{'${redis.cluster.nodes}'.split(',')}")
    private List<String> nodes;
    @Value( "${redis.cluster.maxRedirects}" )
    private int maxRedirects ;
    @Value( "${redis.pool.blockWhenExhausted}" )
    private boolean blockWhenExhausted;
    @Value( "${redis.pool.max.total}" )
    private Integer maxTotal;
    @Value( "${redis.pool.max.idle}")
    private Integer maxIdle;
    @Value( "${redis.pool.min.idle}" )
    private Integer minIdle;
    @Value( "${redis.pool.testOnBorrow}" )
    private boolean testOnBorrow;

    @Bean
    public Set<HostAndPort> clusterNodes() {
        Set<HostAndPort> clusterNodes = new HashSet<>();
        for (String node : nodes) {
            String[] hostAndPort = node.split(":");
            clusterNodes.add(new HostAndPort(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
        }
        return clusterNodes;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setTestOnBorrow(testOnBorrow);
        poolConfig.setBlockWhenExhausted(blockWhenExhausted);
        return poolConfig;
    }

    @Bean
    public JedisCluster jedisCluster(Set<HostAndPort> clusterNodes, JedisPoolConfig jedisPoolConfig){
        return new JedisCluster(clusterNodes, jedisPoolConfig);
    }

    @Bean
    public RedisClusterConfiguration redisClusterConfiguration() {
        RedisClusterConfiguration redisClusterConfig = new RedisClusterConfiguration(nodes);
        redisClusterConfig.setMaxRedirects(maxRedirects);
        return redisClusterConfig;
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory(RedisClusterConfiguration redisClusterConfiguration, JedisPoolConfig jedisPoolConfig) {
        return new JedisConnectionFactory(redisClusterConfiguration, jedisPoolConfig);
    }

    @Bean
    public RedisListener redisListener(JedisCluster jedisCluster){
        return new RedisListener(jedisCluster);
    }
}
