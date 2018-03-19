package com.mvc.sell.console.dao;

import com.mvc.sell.console.pojo.bean.Project;
import com.mvc.sell.console.pojo.bean.ProjectSold;
import com.mvc.sell.console.pojo.dto.MyProjectDTO;
import com.mvc.sell.console.pojo.vo.MyProjectVO;
import com.mvc.sell.console.pojo.vo.ProjectInfoVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
            "select * from project t1, project_sold t2 where t1.id = t2.id and show = 1",
            "<when test=\"status!=null\">",
            "and t1.status = #{status}",
            "</when>",
            "</script>"})
    List<MyProjectVO> listDetail(MyProjectDTO myProjectDTO);

    @Select("select * from project t1, project_sold t2 where t1.id = t2.id and t1.id = #{id} and t1.show = 1")
    MyProjectVO detail(MyProjectDTO myProjectDTO);

    @Select("SELECT (SELECT IFNULL(balance,0) FROM capital WHERE user_id = #{userId} and token_id = 0) eth_balance, ratio, id project_id, token_name FROM project WHERE id = #{id}")
    ProjectInfoVO getInfoByUser(@Param("id") BigInteger id, @Param("userId") BigInteger userId);

    @Update("update project set status = 1 where start_time < now() and status = 0")
    Integer updateStart();

    @Update("update project set status = 2 where stop_time < now() and status = 1")
    Integer updateFinish();

    @Update("update project_sold set buyer_num = buyer_num+1, sold_eth = sold_eth + {soldEth} where id = #{id}")
    void updateSolePalance(ProjectSold projectSold);
}
