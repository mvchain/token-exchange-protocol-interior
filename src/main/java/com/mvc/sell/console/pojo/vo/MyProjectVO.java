package com.mvc.sell.console.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * MyProjectVO
 *
 * @author qiyichen
 * @create 2018/3/14 19:08
 */
@Data
public class MyProjectVO extends ProjectVO implements Serializable {
    private static final long serialVersionUID = -3075200194863929308L;

    private Boolean partake;
    private Integer soldEth;
    private Integer buyerNum;

}
