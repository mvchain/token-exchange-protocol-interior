package com.mvc.sell.console.service;

import com.github.pagehelper.Page;
import com.mvc.sell.console.pojo.bean.Config;
import com.mvc.sell.console.pojo.bean.Project;
import com.mvc.sell.console.pojo.vo.ProjectVO;
import com.mvc.sell.console.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

/**
 * project service
 *
 * @author qiyichen
 * @create 2018/3/13 11:25
 */
public class ProjectService extends BaseService {

    @Autowired
    ConfigService configService;

    public List<ProjectVO> list() {
        List<Project> list = projectMapper.selectAll();
        return (List<ProjectVO>) BeanUtil.beanList2VOList(list, ProjectVO.class);
    }

    public void insert(Project project) {
        projectMapper.insertSelective(project);
        Config config = new Config();
        config.setTokenName(project.getTokenName());
        config.setId(project.getId());
        configService.insert(config);
    }

    public void update(Project project) {
        projectMapper.updateByPrimaryKeySelective(project);
        Config config = new Config();
        config.setTokenName(project.getTokenName());
        config.setId(project.getId());
        configService.update(config);
    }

    public ProjectVO get(BigInteger id) {
        Project project = projectMapper.selectByPrimaryKey(id);
        return (ProjectVO) BeanUtil.copyProperties(project, new ProjectVO());
    }
}
