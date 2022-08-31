package com.kyyee.sps.manager.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "status")
    private Short status;

    @JsonProperty(value = "receiver")
    private String receiver;
    /**
     * 0：不弹框；1：弹框
     */
    @JsonProperty(value = "popup")
    private Short popup;

    @JsonProperty(value = "type")
    private Short type;

    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "content")
    private String content;

    @JsonProperty(value = "callback_url")
    private String callbackUrl;

    @JsonProperty(value = "happen_time")
    private LocalDateTime happenTime;

    @JsonProperty(value = "business_id")
    private String businessId;

    @JsonProperty(value = "sender")
    private String sender;

    @JsonProperty(value = "callback_ext")
    private JsonNode callbackExt;

    @JsonProperty(value = "popup_ext")
    private JsonNode popupExt;

    @JsonProperty(value = "audio_ext")
    private JsonNode audioExt;

}
