package com.mvc.sell.console.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Project dto
 *
 * @author qiyichen
 * @create 2018/3/13 18:12
 */
@Data
public class ProjectDTO implements Serializable {

    private static final long serialVersionUID = -4469141542284477495L;
    private BigInteger id;
    private String title;
    private String tokenName;
    private String contractAddress;
    private BigDecimal ethNumber;
    private Float ratio;
    private Date startTime;
    private Date stopTime;
    private String homepage;
    private String whitePaperAddress;
    private String whitePaperName;
    private String projectImageAddress;
    private String projectImageName;
    private String projectCoverAddress;
    private String projectCoverName;
    private String leaderImageAddress;
    private String leaderImageName;
    private String leaderName;
    private String position;
    private String description;
    private Integer decimals;

}
