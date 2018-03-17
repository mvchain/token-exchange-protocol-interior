package com.mvc.sell.console.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * token vo
 *
 * @author qiyichen
 * @create 2018/3/12 14:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO implements Serializable {


    private static final long serialVersionUID = -2962447657047492746L;

    private String token;
    private String refreshToken;
}
