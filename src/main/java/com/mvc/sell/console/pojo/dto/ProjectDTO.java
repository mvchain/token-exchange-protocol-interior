package com.mvc.sell.console.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

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
    private Float ratio;
    private String whitePaperAddress;
    private String whitePaperName;
    private String projectImageAddress;
    private String projectImageName;
    private String leaderImageAddress;
    private String leaderImageName;
    private String leaderName;
    private String position;
    private String description;

}
