package com.mvc.sell.console.pojo.dto;

import com.mvc.sell.console.common.Page;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author qiyichen
 * @create 2018/3/14 19:06
 */
@Data
public class MyProjectDTO extends Page implements Serializable {
    private static final long serialVersionUID = -4568956783195995014L;

    private BigInteger id;
    private BigInteger userId;
    private Integer status;

}
