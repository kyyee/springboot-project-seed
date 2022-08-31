package com.kyyee.sps.dto.websocket;

import com.kyyee.framework.common.interceptor.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.security.Principal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeedMessage implements Principal, Serializable {

    private static final long serialVersionUID = 9080762398916334124L;

    /**
     * 客户端标识
     */
    private String id;

    /**
     * 用户user
     */
    private User user;

    /**
     * 外网为1，内外为0
     */
    private Integer outer;

    private String simpleId;

    @Override
    public String getName() {
        return simpleId + ":" + (outer == null ? 0 : outer);
    }
}
