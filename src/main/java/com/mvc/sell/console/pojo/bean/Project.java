package com.mvc.sell.console.pojo.bean;

import com.mvc.sell.console.constants.MessageConstants;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Project
 *
 * @author qiyichen
 * @create 2018/3/13 11:17
 */
@Data
public class Project {
    @Id
    private BigInteger id;
    @NotNull(message = "{TITLE_EMPTY}")
    private String title;
    private String tokenName;
    private BigDecimal ethNumber;
    private Float ratio;
    private Date startTime;
    private Date stopTime;
    private String whitePaperAddress;
    private String whitePaperName;
    private String homepage;
    private String projectImageAddress;
    private String projectImageName;
    private String projectCoverAddress;
    private String projectCoverName;
    private String leaderImageAddress;
    private String leaderImageName;
    private String leaderName;
    private String position;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private Integer status;
    private Integer needShow;
    private Integer sendToken;
    private Integer retire;
}
