package com.kyyee.sps.common.component.workflow;


import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.sps.common.exception.ServiceException;
import com.kyyee.sps.common.utils.SerializableHelper;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WorkFlowContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 4275200596455798644L;

    private final Map<String, Object> context = new HashMap<>();

    public void put(String key, Object value) {
        if (!(value instanceof Serializable)) {
            throw new IllegalArgumentException("value[%s] must be Serializable".formatted(value.getClass().getName()));
        }
        context.put(key, value);
    }

    public Object get(String key) {
        return context.get(key);
    }

    public void remove(String key) {
        context.remove(key);
    }

    static WorkFlowContext fromBytes(byte[] bytes) {
        try {
            return SerializableHelper.readObject(bytes);
        } catch (Exception e) {
            throw ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR, "Unable to create WorkFlowContext from input bytes");
        }
    }

    byte[] toBytes() {
        try {
            return SerializableHelper.writeObject(this);
        } catch (IOException e) {
            throw ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR, "Cannot write WorkFlowContext to bytes");
        }
    }

    public void release() {
        context.clear();
    }
}
