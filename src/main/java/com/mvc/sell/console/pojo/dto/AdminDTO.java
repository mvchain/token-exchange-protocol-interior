package com.mvc.sell.console.pojo.dto;

import com.mvc.sell.console.constants.MessageConstants;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * admin dto
 *
 * @author qiyichen
 * @create 2018/3/12 14:43
 */
@Data
public class AdminDTO implements Serializable {
    private static final long serialVersionUID = -1840405735682750834L;

    @NotNull(message = MessageConstants.USERNAME_EMPTY)
    private String username;

    @NotNull(message = MessageConstants.PWD_EMPTY)
    private String password;

    private String imageCode;

}
