package com.kyyee.sps.model.primary;

import com.kyyee.sps.common.component.workflow.WorkFlowState;
import io.mybatis.provider.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity.Table(value = "work_flow", autoResultMap = true)
public class WorkFlow {

    @Entity.Column(id = true)
    private Long id;

    private String chainUuid;

    private String name;

    private WorkFlowState state;

    private String errorCode;

    private String reason;

    private Integer position;

    private byte[] context;

    private LocalDateTime operationDate;
}
