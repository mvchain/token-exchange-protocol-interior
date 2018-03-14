package com.mvc.sell.console.dao;

import com.mvc.sell.console.pojo.bean.Config;
import com.mvc.sell.console.pojo.vo.ConfigVO;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * config mapper
 *
 * @author qiyichen
 * @create 2018/3/12 14:47
 */
public interface ConfigMapper extends Mapper<Config> {
}
