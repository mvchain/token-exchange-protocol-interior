package com.mvc.sell.console.job;

import com.mvc.sell.console.service.ProjectService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

import java.io.IOException;

/**
 * eth job
 *
 * @author qiyichen
 * @create 2018/3/16 14:20
 */
@Component
@Log4j
public class ProjectJob {

    @Autowired
    ProjectService projectService;
    @Autowired
    Web3j web3j;

    @Scheduled(cron = "*/1 * * * * ?")
    public void updateStatus() throws IOException {
        Integer num = projectService.updateStatus();
        if (num > 0) {
            log.info(String.format("%s project update status", num));
        }
    }


}
