package com.foodly.radar.infrastructure.persistence;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class RedisRadarRepositoryImpl implements RedisRadarRepository {

    private JedisPool jedisPool;

    @PostConstruct
    public void init() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(64);
        poolConfig.setMaxIdle(16);
        poolConfig.setMinIdle(4);

        String redisHost = System.getenv().getOrDefault("REDIS_HOST", "localhost");
        int redisPort = Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", "6379"));

        this.jedisPool = new JedisPool(poolConfig, redisHost, redisPort);
    }

    @Override
    public void saveToCell(String h3Index, String restaurantId, String restaurantJson) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "radar:cell:" + h3Index;
            jedis.sadd(key, restaurantJson);

            jedis.expire(key, 43200);
        }
    }

    @Override
    public List<String> getRestaurantsFromCells(List<String> h3Indexes) {
        List<String> allRestaurants = new ArrayList<>();

        try (Jedis jedis = jedisPool.getResource()) {
            for (String h3Index : h3Indexes) {
                String key = "radar:cell:" + h3Index;
                Set<String> members = jedis.smembers(key);
                if (members != null && !members.isEmpty()) {
                    allRestaurants.addAll(members);
                }
            }
        }
        return allRestaurants;
    }

    @Override
    public void removeFromCell(String h3Index, String restaurantId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "radar:cell:" + h3Index;
            Set<String> members = jedis.smembers(key);
            if (members != null) {
                for (String json : members) {
                    if (json.contains("\"id\":\"" + restaurantId + "\"")) {
                        jedis.srem(key, json);
                        break;
                    }
                }
            }
        }
    }

    @PreDestroy
    public void close() {
        if (this.jedisPool != null && !this.jedisPool.isClosed()) {
            this.jedisPool.close();
        }
    }
}