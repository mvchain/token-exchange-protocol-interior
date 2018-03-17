package com.mvc.sell.console.pojo.dto;

import com.mvc.sell.console.constants.MessageConstants;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * BuyDTO
 *
 * @author qiyichen
 * @create 2018/3/15 14:21
 */
@Data
public class BuyDTO implements Serializable {

    private static final long serialVersionUID = 3760227948938685745L;

    private BigInteger projectId;
    @DecimalMin(value = "0.1", message = MessageConstants.ETH_MIN)
    private BigDecimal ethNumber;

}
