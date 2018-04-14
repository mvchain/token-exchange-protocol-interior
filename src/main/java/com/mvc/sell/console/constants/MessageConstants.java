package com.mvc.sell.console.constants;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author qiyichen
 * @create 2018/3/12 14:45
 */
@Component
public class MessageConstants {

    public static Integer TOKEN_EXPIRE_CODE = 50014;
    public static Integer TOKEN_ERROR_CODE = 50015;

    public static String getMsg(String key) {
        Locale locale = LocaleContextHolder.getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        return bundle.getString(key);
    }

}
