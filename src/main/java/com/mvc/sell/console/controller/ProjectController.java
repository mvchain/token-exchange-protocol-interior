package com.mvc.sell.console.controller;

import com.github.pagehelper.PageInfo;
import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.common.Page;
import com.mvc.sell.console.pojo.bean.ProjectSold;
import com.mvc.sell.console.pojo.dto.ProjectDTO;
import com.mvc.sell.console.pojo.vo.ProjectVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;

/**
 * project controller
 *
 * @author qiyichen
 * @create 2018/3/10 17:21
 */
@RestController
@RequestMapping("project")
public class ProjectController extends BaseController {

    @GetMapping
    Result<PageInfo<ProjectVO>> list(@ModelAttribute Page page) {
        return ResultGenerator.genSuccessResult(projectService.list());
    }

    @PostMapping
    Result add(@RequestBody @Valid ProjectDTO project) {
        project.setId(null);
        projectService.insert(project);
        return ResultGenerator.genSuccessResult();
    }

    @PutMapping
    Result update(@RequestBody @Valid ProjectDTO project) {
        projectService.update(project);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("{id}")
    Result<ProjectVO> get(@PathVariable BigInteger id) {
        return ResultGenerator.genSuccessResult(projectService.get(id));
    }

    @GetMapping("{id}/sold")
    Result<ProjectSold> sold(@PathVariable BigInteger id) {
        return ResultGenerator.genSuccessResult(projectService.getSold(id));
    }

    @GetMapping("{id}/status/{status}")
    Result<ProjectVO> get(@PathVariable BigInteger id, @PathVariable Integer status) {
        projectService.updateStatus(id, status);
        return ResultGenerator.genSuccessResult();
    }

}
