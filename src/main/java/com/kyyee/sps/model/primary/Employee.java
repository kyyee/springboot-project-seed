/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.model.primary;

import com.kyyee.sps.model.BaseEntity;
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
public class Employee extends BaseEntity {
    private String name;

    private Integer age;

    private String department;

}
