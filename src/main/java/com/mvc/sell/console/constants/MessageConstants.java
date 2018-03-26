package com.mvc.sell.console.constants;

/**
 * @author qiyichen
 * @create 2018/3/12 14:45
 */
public interface MessageConstants {

    String PWD_ERR = "密码输入错误!";
    String PWD_EMPTY = "密码不能为空";
    String USERNAME_EMPTY = "用户名不能为空";
    String TOKEN_WRONG = "token is wrong";
    String TOKEN_EMPTY = "币种名称不能为空";
    String TITLE_EMPTY = "请填写项目标题！";
    String ETH_MIN = "购买数量过小";
    String ADDERSS_ERROR = "地址错误或不存在";
    String CANNOT_DELETE = "不能删除";
    String CANNOT_SEND_TOKEN = "不能发币";
    String PROJECT_NOT_EXIST = "项目不存在";
    String CANNOT_RETIRE = "不能清退";
    String CANNOT_CANCEL = "不能取消";
    String TOKEN_EXPIRE = "令牌已过期,请刷新";
    Integer TOKEN_EXPIRE_CODE = 50014;
    Integer TOKEN_ERROR_CODE = 50015;
    String TOKEN_NAME_EXIST = "币种名称已存在";
}
