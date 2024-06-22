package com.kyyee.sps.model;

import com.kyyee.framework.common.enums.DeletedStatus;
import com.kyyee.framework.common.interceptor.user.UserHandler;
import io.mybatis.provider.Entity;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseTaskEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 2682499693097064899L;

    protected BaseTaskEntity() {
        prePersist();
    }

    @Entity.Column(id = true)
    private Long id;

    /**
     * 流程号
     */
    private String flowChainId;

    private String grId;

    private String type;

    private String state;

    private String message;

    private String finish;

    /**
     * 超时时间
     */
    private Long timeout;

    /**
     * 副本名称
     */
    private String hostname;

    /**
     * 请求id
     */
    private String reqId;

    /**
     * 请求上下文
     */
    private String context;

    /**
     * 时间字符串：yyyy-MM-dd HH:mm:ss
     */
    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private String createBy;

    private String updateBy;

    /**
     * 删除状态，-1：已删除；0：未删除
     */
    private Short deleted;

    /**
     * 乐观锁
     */
    private Integer version;

    public void prePersist() {
        this.createBy = UserHandler.userCode();
        this.createAt = LocalDateTime.now();
        this.version = 0;
        this.deleted = DeletedStatus.EXIST.value();
    }

    public void preUpdate(BaseTaskEntity entity) {
        this.id = entity.getId();
        this.updateBy = UserHandler.userCode();
        this.updateAt = LocalDateTime.now();
        this.createBy = entity.getCreateBy();
        this.createAt = entity.getCreateAt();
        this.version = entity.getVersion() + 1;
    }

}
