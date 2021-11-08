package com.metevs.dcache.loader.cache;


import com.metevs.dcache.facade.domain.BaseDO;
import com.metevs.dcache.loader.runner.AbstractLoader;
import com.metevs.dcache.loader.util.ActionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Data
public class CacheTask<T extends BaseDO> extends BaseDO implements Future, Runnable {
    private LocalDateTime initTime; //任务初始化时间
    ActionType actionType; //任务加载方式
    private T origin;   //原始对象
    private T transfer; //封装对象
    private T history;  //历史缓存
    private Integer executeStep = 1; //成功步数
    private AbstractLoader<T> loader; //加载器
    private Function<Boolean, T> callBack; //回调方法
    private AtomicInteger retry = new AtomicInteger(0); //重试次数
    private Integer source = 0;
    private Boolean isHistory = false;
    private Boolean check = false;


    public CacheTask() {
    }

    public CacheTask(T origin, ActionType actionType, AbstractLoader<T> loader, Function function) {
        this.origin = origin;
        this.actionType = actionType;
        this.loader = loader;
        initTime = LocalDateTime.now();
        callBack = function;
    }

    public CacheTask(T origin, ActionType actionType, AbstractLoader<T> loader, Function function, boolean ifCheck) {
        this.origin = origin;
        this.actionType = actionType;
        this.loader = loader;
        initTime = LocalDateTime.now();
        callBack = function;
        check = ifCheck;
    }

    public CacheTask(T origin, ActionType actionType, AbstractLoader<T> loader) {
        this(origin, actionType, loader, null);
    }

    public CacheTask(T origin, ActionType actionType, AbstractLoader<T> loader, boolean check) {
        this(origin, actionType, loader, null, check);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void run() {
        switch (actionType) {
            case LOAD:
                loader.loadMsg(this);
                break;
            case INCREASE:
                loader.increaseMsg(this);
                break;
            case MODIFY:
                loader.modifyMsg(this);
                break;
            case INVALID:
                loader.invalidMsg(this);
                break;
            default:
                throw new RuntimeException("unknown action type");
        }
    }

    @Override
    public void success() {
        if (this.callBack != null)
            this.callBack.apply(true);
    }

    @Override
    public void failed() {
        if (this.callBack != null)
            this.callBack.apply(false);
    }

    @Override
    public void timeOut() {

    }

    @Override
    public String toString() {
        return "CacheTask{" +
                "initTime=" + initTime +
                ", actionType=" + actionType +
                ", origin=" + origin +
                ", transfer=" + transfer +
                ", history=" + history +
                ", executeStep=" + executeStep +
                ", loader=" + loader +
                ", callBack=" + callBack +
                ", retry=" + retry +
                '}';
    }

    @Override
    public Long getChannel() {
        return origin.getChannel();
    }

    public final static class Builder<T extends BaseDO> {
        private CacheTask<T> cacheTask;

        private Builder() {
            cacheTask = new CacheTask();
        }

        private Builder(CacheTask cacheTask) {
            this.cacheTask = cacheTask;
        }

        public Builder origin(T t) {
            cacheTask.setOrigin(t);
            return this;
        }

        public Builder history(T t) {
            cacheTask.setHistory(t);
            return this;
        }

        public Builder transfer(T t) {
            cacheTask.setTransfer(t);
            return this;
        }

        public Builder source(Integer t) {
            cacheTask.setSource(t);
            return this;
        }

        public Builder actionType(ActionType t) {
            cacheTask.setActionType(t);
            return this;
        }

        public Builder initTime(LocalDateTime t) {
            cacheTask.setInitTime(t);
            return this;
        }

        public Builder executeStep(Integer t) {
            cacheTask.setExecuteStep(t);
            return this;
        }

        public Builder loader(AbstractLoader t) {
            cacheTask.setLoader(t);
            return this;
        }

        public Builder isHistory(Boolean isHistory) {
            cacheTask.setIsHistory(isHistory);
            return this;
        }


        public CacheTask<T> build() {
            return cacheTask;
        }
    }

}


