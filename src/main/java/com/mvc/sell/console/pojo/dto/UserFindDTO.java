package com.mvc.sell.console.pojo.dto;

import com.mvc.sell.console.common.Page;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * UserFindDTO
 *
 * @author qiyichen
 * @create 2018/3/12 20:34
 */
@Data
public class UserFindDTO extends Page implements Serializable {
    private static final long serialVersionUID = 5483965417840093756L;

    private String username;
    private BigInteger id;

}
