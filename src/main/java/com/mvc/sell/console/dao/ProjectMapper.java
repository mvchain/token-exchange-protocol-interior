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
            "select * from project t1, project_sold t2 where t1.id = t2.id and t1.need_show = 1",
            "<when test=\"status!=null\">",
            "and t1.status = #{status}",
            "</when>",
            "</script>"})
    List<MyProjectVO> listDetail(MyProjectDTO myProjectDTO);

    @Select("select * from project t1, project_sold t2 where t1.id = t2.id and t1.id = #{id} and t1.need_show = 1")
    MyProjectVO detail(MyProjectDTO myProjectDTO);

    @Select("SELECT (SELECT IFNULL(balance,0) FROM capital WHERE user_id = #{userId} and token_id = 0) eth_balance, ratio, id project_id, token_name FROM project WHERE id = #{id}")
    ProjectInfoVO getInfoByUser(@Param("id") BigInteger id, @Param("userId") BigInteger userId);

    @Update("update project set status = 1 where start_time < now() and status = 0")
    Integer updateStart();

    @Update("update project t1, project_sold t2 set t1.status = 2 where t1.id = t2.id  and t1.status = 1 and (t1.stop_time < now() or t1.eth_number <= t2.sold_eth)")
    Integer updateFinish();

    @Update("update project_sold set buyer_num = buyer_num+#{buyerNum}, sold_eth = sold_eth + #{soldEth} where id = #{id}")
    void updateSoldBalance(ProjectSold projectSold);

    @Update("UPDATE capital SET balance = balance + (SELECT IFNULL(sum(eth_number), 0) FROM orders where user_id = capital.user_id and project_id = #{projectId} AND order_status not in (4,9)) where token_id = #{tokenId}")
    void retireBalance(@Param("projectId") BigInteger projectId, @Param("tokenId") BigInteger tokenId);

    @Update("UPDATE capital SET balance = balance - (SELECT IFNULL(sum(token_number), 0) FROM orders where user_id = capital.user_id and project_id = #{projectId} AND order_status = 2) where token_id = #{tokenId}")
    void retireToken(@Param("projectId") BigInteger projectId, @Param("tokenId") BigInteger tokenId);

    @Select("insert IGNORE INTO capital SELECT null, user_id, #{tokenId}, IFNULL(sum(token_number),0) number FROM orders WHERE order_status = 0 AND project_id = #{projectId} GROUP BY user_id ON DUPLICATE KEY UPDATE balance = balance + IFNULL((SELECT sum(token_number) number FROM orders WHERE order_status = 0 AND project_id = #{projectId}),0)")
    void sendToken(@Param("projectId") BigInteger projectId, @Param("tokenId") BigInteger tokenId);
}
