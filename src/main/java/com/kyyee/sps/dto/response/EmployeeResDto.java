/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.dto.response;

import com.kyyee.sps.model.secondary.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author kyyee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResDto {
    private Long id;

    private String name;

    private Integer age;

    private List<Long> orgIds;

    private List<Organization> orgs;

}
