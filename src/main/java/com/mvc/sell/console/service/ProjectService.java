package com.mvc.sell.console.service;

import com.github.pagehelper.PageInfo;
import com.mvc.sell.console.pojo.bean.Config;
import com.mvc.sell.console.pojo.bean.Project;
import com.mvc.sell.console.pojo.bean.ProjectSold;
import com.mvc.sell.console.pojo.dto.ProjectDTO;
import com.mvc.sell.console.pojo.vo.ProjectSoldVO;
import com.mvc.sell.console.pojo.vo.ProjectVO;
import com.mvc.sell.console.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * project service
 *
 * @author qiyichen
 * @create 2018/3/13 11:25
 */
@Service
public class ProjectService extends BaseService {

    @Autowired
    ConfigService configService;

    public PageInfo<ProjectVO> list() {
        List<Project> list = projectMapper.selectAll();
        return (PageInfo<ProjectVO>) BeanUtil.beanList2VOList(list, ProjectVO.class);
    }

    public void insert(ProjectDTO projectDTO) {
        Project project = (Project) BeanUtil.copyProperties(projectDTO, new Project());
        projectMapper.insertSelective(project);
        Config config = new Config();
        config.setProjectId(project.getId());
        config.setTokenName(project.getTokenName());
        configService.insert(config);
        ProjectSold projectSold = new ProjectSold();
        projectSold.setId(project.getId());
        projectSold.setBuyerNum(0);
        projectSold.setSendToken(BigDecimal.ZERO);
        projectSold.setSoldEth(BigDecimal.ZERO);
        tokenSoldMapper.insert(projectSold);
    }

    public void update(ProjectDTO projectDTO) {
        Project project = (Project) BeanUtil.copyProperties(projectDTO, new Project());
        projectMapper.updateByPrimaryKeySelective(project);
        Config config = new Config();
        config.setProjectId(project.getId());
        config.setTokenName(project.getTokenName());
        configService.update(config);
    }

    public ProjectVO get(BigInteger id) {
        Project project = projectMapper.selectByPrimaryKey(id);
        return (ProjectVO) BeanUtil.copyProperties(project, new ProjectVO());
    }

    public void updateStatus(BigInteger id, Integer status) {
        Project project = new Project();
        project.setId(id);
        project.setStatus(status);
        projectMapper.updateByPrimaryKeySelective(project);
    }

    public ProjectSoldVO getSold(BigInteger id) {
        ProjectSold projectSold = new ProjectSold();
        projectSold.setId(id);
        return tokenSoldMapper.selectSold(projectSold);
    }
}
