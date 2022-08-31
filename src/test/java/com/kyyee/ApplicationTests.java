/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    private MockMvc mockMvc;

    @Resource
    private
    WebApplicationContext wac;

    String expectedJson;

    @Test
    public void contextLoads() {
    }

}
