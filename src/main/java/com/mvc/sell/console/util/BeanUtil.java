package com.mvc.sell.console.util;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * bean util
 *
 * @author qiyichen
 * @create 2018/3/13 10:56
 */
public class BeanUtil {

    public static PageInfo beanList2VOList (List<? extends Object> list , Class targetClass) {
        List<Object> retult = list.stream().map(account1 -> {
            Object instance = null;
            try {
                instance = targetClass.newInstance();
                BeanUtils.copyProperties(account1, instance);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return instance;
        }).collect(Collectors.toList());
        return new PageInfo(retult);
    }

    public static Object copyProperties(Object source, Object target) {
        if (null != source && null != target) {
            BeanUtils.copyProperties(source, target);
        }
        return target;
    }
}
