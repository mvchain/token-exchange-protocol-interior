package com.mvc.sell.console.dao;

import com.mvc.sell.console.pojo.bean.Project;
import com.mvc.sell.console.pojo.dto.MyProjectDTO;
import com.mvc.sell.console.pojo.vo.MyProjectVO;
import com.mvc.sell.console.pojo.vo.ProjectInfoVO;
import com.mvc.sell.console.pojo.vo.WithdrawInfoVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigInteger;
import java.util.List;

/**
 * project mapper
 *
 * @author qiyichen
 * @create 2018/3/12 14:47
 */
public interface ProjectMapper extends Mapper<Project> {

    @Select({"<script>",
            "select * from project t1, project_sold t2 where t1.id = t2.id",
            "<when test=\"status!=null\">",
            "and t1.status = #{status}",
            "</when>",
            "</script>"})
    List<MyProjectVO> listDetail(MyProjectDTO myProjectDTO);

    @Select("select * from project t1, project_sold t2 where t1.id = t2.id and t1.id = #{id}")
    MyProjectVO detail(MyProjectDTO myProjectDTO);

    @Select("SELECT (SELECT IFNULL(balance,0) FROM capital WHERE user_id = #{userId} and token_id = 0) eth_balance, ratio, id project_id, token_name FROM project WHERE id = #{id}")
    ProjectInfoVO getInfoByUser(@Param("id") BigInteger id, @Param("userId") BigInteger userId);

}
