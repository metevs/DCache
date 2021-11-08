package com.metevs.dcache.loader.hanlde;

import com.metevs.dcache.facade.domain.BaseDO;
import com.metevs.dcache.loader.runner.AbstractLoader;
import com.metevs.dcache.loader.util.ActionType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NodeHandleChain<T extends BaseDO> {

    public NodeHandleChain(AbstractLoader<T> loader, ActionType actionType, ActionType actionType1) {
        this.loader = loader;
        this.actionTypes = new ActionType[2];
        actionTypes[0] = actionType;
        actionTypes[1] = actionType1;
    }

    public NodeHandleChain(AbstractLoader<T> loader, ActionType actionType) {
        this.loader = loader;
        this.actionTypes = new ActionType[1];
        actionTypes[0] = actionType;
    }

    public NodeHandleChain(AbstractLoader<T> loader) {
        this(loader, null);
    }

    public NodeHandleChain() {
    }

    private AbstractLoader<T> loader;
    private NodeHandleCacheProxy<T> head;
    private List<NodeHandleCacheProxy<T>> nodeHandles = null;
    private ActionType[] actionTypes;

    public void addLastNodeHandle(NodeHandleCacheProxy<T> nodeHandle) {
        if (nodeHandles == null) {
            nodeHandles = new ArrayList<>();
        }
        nodeHandles.add(nodeHandle);
        if (nodeHandles.size() == 1) {
            head = nodeHandle;
        } else {
            nodeHandles.get(nodeHandles.size() - 1 - 1).setNext(nodeHandle);
        }
        nodeHandle.setPosition(1 << (nodeHandles.size() - 1));
    }
}
