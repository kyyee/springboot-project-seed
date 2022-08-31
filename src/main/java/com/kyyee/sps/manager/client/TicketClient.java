package com.kyyee.sps.manager.client;

import com.kyyee.framework.common.base.Res;
import com.kyyee.framework.common.interceptor.FeignClientRestHeaderInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "bssUuvClient",
    url = "${kyyee.service.bss-uuv:http://bss-uuv:80}",
    path = "/api/bss/v1/uuv",
    configuration = FeignClientRestHeaderInterceptor.class)
public interface TicketClient {

    @GetMapping(path = "/ticket/{user_code}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Res<String> list();

}
