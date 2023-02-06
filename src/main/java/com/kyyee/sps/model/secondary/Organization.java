/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.model.secondary;

import com.kyyee.sps.model.BaseEntity;
import io.mybatis.provider.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kyyee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity.Table(value = "organization", autoResultMap = true)
public class Organization extends BaseEntity {
    private String name;

    private Short type;

}
