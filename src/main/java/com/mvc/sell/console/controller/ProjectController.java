package com.mvc.sell.console.controller;

import com.github.pagehelper.Page;
import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.pojo.bean.Project;
import com.mvc.sell.console.pojo.vo.ProjectVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;

/**
 * project controller
 *
 * @author qiyichen
 * @create 2018/3/10 17:21
 */
@Controller
@RequestMapping("project")
public class ProjectController extends BaseController {

    @GetMapping
    Result<List<ProjectVO>> list() {
        return ResultGenerator.genSuccessResult(projectService.list());
    }

    @PostMapping
    Result add(@RequestBody @Valid Project project) {
        projectService.insert(project);
        return ResultGenerator.genSuccessResult();
    }

    @PutMapping
    Result update(@RequestBody @Valid Project project) {
        projectService.update(project);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("{id}")
    Result<ProjectVO> get(@PathVariable BigInteger id) {
        return ResultGenerator.genSuccessResult(projectService.get(id));
    }
}
