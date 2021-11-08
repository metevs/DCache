package com.metevs.dcache.loader.hanlde;

import com.metevs.dcache.loader.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 *
 */
//@Component
@Slf4j
public class RedisLockUtil {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * @param lockName
     * @return
     */
    public boolean tryLock(String lockName) {
        RLock lock = redissonClient.getLock(lockName);
        try {
            boolean tryLock = lock.tryLock(Constants.ONE, Constants.THREE, TimeUnit.SECONDS);
            return tryLock;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("tryLock error , msg :{}", e.getMessage());
            return false;
        }
    }

    public boolean tryLockAndWait(String lockName, int waitTime) {
        RLock lock = redissonClient.getLock(lockName);
        try {
            boolean tryLock = lock.tryLock(waitTime, Constants.FIVE, TimeUnit.SECONDS);
            return tryLock;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("tryLockAndWait error , msg :{}", e.getMessage());
            return false;
        }
    }

    /**
     * 解锁
     *
     * @param lockKey
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock != null) {
            lock.unlock();
        }
    }
}
