package com.kyyee.sps.model.primary;

import com.kyyee.sps.common.component.workflow.WorkFlowChainState;
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
@Entity.Table(value = "work_flow_chain", autoResultMap = true)
public class WorkFlowChain {
    @Entity.Column(id = true)
    private String uuid;

    private String name;

    private String owner;

    private WorkFlowChainState state;

    private String reason;

    private Integer totalWorkFlows;

    private Integer currentPosition;

    private LocalDateTime operationDate;
}
