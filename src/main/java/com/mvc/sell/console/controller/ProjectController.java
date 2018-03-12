package com.mvc.sell.console.controller;

import com.github.pagehelper.Page;
import com.mvc.common.msg.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;

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
    Result list(@ModelAttribute Page page) {
        return projectService.list(page);
    }

    @PostMapping
    Result add(@RequestBody @Valid ProjectDTO projectDTO) {
        return  projectService.insert(projectDTO);
    }

    @PutMapping
    Result update(@RequestBody @Valid ProjectDTO projectDTO) {
        return  projectService.udpate(projectDTO);
    }

    @GetMapping("{id}")
    Result get(@PathVariable BigInteger id) {
        return projectService.get(id);
    }
}
