package com.papa.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author eric
 * @date 2019/7/15 14:41
 **/
@Data
public class AccessTokenDto implements Serializable {

    private String appId;
    private String accessToken;
    private long expiresIn;
    private Long createTimestamp;
    private Long expireTimeStamp;
}
