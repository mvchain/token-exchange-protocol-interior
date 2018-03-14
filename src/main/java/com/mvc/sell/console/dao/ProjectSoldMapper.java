package com.mvc.sell.console.dao;

import com.mvc.sell.console.pojo.bean.ProjectSold;
import com.mvc.sell.console.pojo.vo.ProjectSoldVO;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

/**
 * token sold mapper
 *
 * @author qiyichen
 * @create 2018/3/13 19:14
 */
public interface ProjectSoldMapper extends Mapper<ProjectSold>{
    @Select("select t2.*, t1.token_name, t1.eth_number, t1.ratio * t1.eth_number token_number from project t1, project_sold t2 where t1.id = t2.id and t1.id = #{id}")
    ProjectSoldVO selectSold(ProjectSold projectSold);
}
