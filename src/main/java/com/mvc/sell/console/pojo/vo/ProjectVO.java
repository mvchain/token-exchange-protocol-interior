package com.mvc.sell.console.pojo.vo;

import com.mvc.sell.console.common.Page;
import com.mvc.sell.console.constants.MessageConstants;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * ProjectVO
 *
 * @author qiyichen
 * @create 2018/3/13 11:28
 */
@Data
public class ProjectVO implements Serializable {
    private static final long serialVersionUID = -42092415251737822L;

    private BigInteger id;
    private String title;
    private String tokenName;
    private String contractAddress;
    private BigDecimal ethNumber;
    private Float ratio;
    private Date startTime;
    private Date stopTime;
    private String whitePaperAddress;
    private String whitePaperName;
    private String projectImageAddress;
    private String projectImageName;
    private String leaderImageAddress;
    private String leaderImageName;
    private String leaderName;
    private String position;
    private String description;
    private Date createdAt;
    private Date updatedAt;

}
