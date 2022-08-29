package com.kyyee.sps.model;

import com.kyyee.framework.common.enums.DeletedStatus;
import com.kyyee.framework.common.interceptor.user.UserHandler;
import lombok.Data;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
public class BaseEntity {

    //    @GeneratedValue(generator = "uuidGenerator")
//    @GenericGenerator(name = "uuidGenerator", strategy = "uuid")
    @Id
    private Long id;

    /**
     * 时间字符串：yyyy-MM-dd HH:mm:ss
     */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

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

    //    @PrePersist
    public void prePersist() {
        this.createBy = UserHandler.userCode();
        this.createTime = LocalDateTime.now();
        this.version = 0;
        this.deleted = DeletedStatus.EXIST.value();
    }

    //    @PreUpdate
    public void preUpdate(BaseEntity entity) {
        this.id = entity.getId();
        this.updateBy = UserHandler.userCode();
        this.updateTime = LocalDateTime.now();
        this.createBy = entity.getCreateBy();
        this.createTime = entity.getCreateTime();
        this.version = entity.getVersion() + 1;
    }

}
