package com.mvc.sell.console.service;

import com.github.pagehelper.PageInfo;
import com.mvc.common.context.BaseContextHandler;
import com.mvc.sell.console.constants.CommonConstants;
import com.mvc.sell.console.constants.RedisConstants;
import com.mvc.sell.console.pojo.bean.*;
import com.mvc.sell.console.pojo.dto.BuyDTO;
import com.mvc.sell.console.pojo.dto.MyProjectDTO;
import com.mvc.sell.console.pojo.dto.ProjectDTO;
import com.mvc.sell.console.pojo.dto.WithdrawDTO;
import com.mvc.sell.console.pojo.vo.*;
import com.mvc.sell.console.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
    @Autowired
    AccountService accountService;

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

    public MyProjectVO getByUser(MyProjectDTO myProjectDTO) {
        BigInteger userId = (BigInteger) BaseContextHandler.get("userId");
        List<BigInteger> userProjects = getUserProject(userId);
        MyProjectVO result = projectMapper.detail(myProjectDTO);
        if (null != userId) {
            result.setPartake(userProjects.contains(result.getId()));
        }
        return result;
    }

    public PageInfo<MyProjectVO> getListByUser(MyProjectDTO myProjectDTO) {
        BigInteger userId = (BigInteger) BaseContextHandler.get("userId");
        List<MyProjectVO> list = projectMapper.listDetail(myProjectDTO);
        if (null != userId) {
            List<BigInteger> userProjects = getUserProject(userId);
            list.stream().forEach(obj -> obj.setPartake(userProjects.contains(obj.getId())));
        }
        return new PageInfo<>(list);
    }

    private List<BigInteger> getUserProject(BigInteger userId) {
        String key = RedisConstants.USER_PROJECTS + "#" + userId;
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, orderMapper.getUserProject(userId));
        }
        return (List<BigInteger>) redisTemplate.opsForValue().get(key);
    }


    public ProjectInfoVO info(BigInteger id) {
        return projectMapper.getInfoByUser(id, getUserId());
    }

    public void buy(BuyDTO buyDTO) {
        Config config = new Config();
        config.setProjectId(buyDTO.getProjectId());
        config = configMapper.selectOne(config);
        Assert.notNull(config, CommonConstants.PROJECT_NOT_EXIST);
        ProjectVO project = get(buyDTO.getProjectId());
        Assert.notNull(project, CommonConstants.PROJECT_NOT_EXIST);
        // update token balance
        Capital capital = new Capital();
        capital.setUserId(getUserId());
        capital.setTokenId(config.getId());
        Capital capitalTemp = capitalMapper.selectOne(capital);
        BigDecimal balance = buyDTO.getEthNumber().multiply(new BigDecimal(project.getRatio()));
        if (null == capitalTemp) {
            capitalTemp = capital;
            capitalTemp.setBalance(balance);
            capitalMapper.insert(capital);
        } else {
            capitalMapper.updateBalance(getUserId(), config.getId(), balance);
        }
        // update ethBalance
        Capital ethCapital = new Capital();
        ethCapital.setUserId(getUserId());
        ethCapital.setTokenId(BigInteger.ZERO);
        Integer result = capitalMapper.updateEth(getUserId(), buyDTO.getEthNumber());
        Assert.isTrue(result > 0, CommonConstants.ETH_NOT_ENOUGH);
        // add order
        Orders orders = new Orders();
        orders.setUserId(getUserId());
        orders.setEthNumber(buyDTO.getEthNumber());
        orders.setOrderId(getOrderId(CommonConstants.ORDER_BUY));
        orders.setOrderStatus(0);
        orders.setProjectId(buyDTO.getProjectId());
        orders.setTokenNumber(balance);
        orderMapper.insert(orders);
    }

    public WithdrawInfoVO getWithdrawConfig(String tokenName) {
        Config config = new Config();
        config.setTokenName(tokenName);
        config.setNeedShow(1);
        config = configMapper.selectOne(config);
        Assert.notNull(config, CommonConstants.TOKEN_ERR);
        WithdrawInfoVO withdrawInfoVO = (WithdrawInfoVO) BeanUtil.copyProperties(config, new WithdrawInfoVO());
        Capital capital = new Capital();
        capital.setUserId(getUserId());
        capital.setTokenId(config.getId());
        capital = capitalMapper.selectOne(capital);
        withdrawInfoVO.setBalance(null == capital ? BigDecimal.ZERO : capital.getBalance());
        String key = RedisConstants.TODAY_USER + "#" + tokenName + "#" + getUserId();
        BigDecimal use = (BigDecimal) redisTemplate.opsForValue().get(key);
        withdrawInfoVO.setTodayUse(null == use?BigDecimal.ZERO: use);
        return withdrawInfoVO;
    }

    public void withdraw(WithdrawDTO withdrawDTO) {
        // check
        AccountVO account = accountService.get(getUserId());
        Assert.isTrue(null != account && account.getTransactionPassword().equalsIgnoreCase(withdrawDTO.getTransactionPassword()), CommonConstants.USER_PWD_ERR);
        Config config = new Config();
        config.setTokenName(withdrawDTO.getTokenName());
        config.setNeedShow(1);
        config = configMapper.selectOne(config);
        Assert.notNull(config, CommonConstants.TOKEN_ERR);
        String key = RedisConstants.TODAY_USER + "#" + withdrawDTO.getTokenName() + "#" + getUserId();
        BigDecimal use = (BigDecimal) redisTemplate.opsForValue().get(key);
        use = null == use?BigDecimal.ZERO: use;
        Boolean canWithdraw = BigDecimal.valueOf(config.getMax()).subtract(use).compareTo(withdrawDTO.getNumber()) > 0;
        Assert.isTrue(canWithdraw, CommonConstants.NOT_ENOUGH);
        Capital capital = new Capital();
        capital.setTokenId(config.getId());
        capital.setUserId(getUserId());
        capital = capitalMapper.selectOne(capital);
        Assert.isTrue(null != capital && capital.getBalance().compareTo(withdrawDTO.getNumber()) > 0, CommonConstants.ETH_NOT_ENOUGH);
        capitalMapper.updateBalance(getUserId(), config.getId(), withdrawDTO.getNumber());
        // add trans
        Transaction transaction = new Transaction();
        transaction.setStatus(0);
        transaction.setNumber(withdrawDTO.getNumber());
        transaction.setOrderId(getOrderId(CommonConstants.ORDER_WITHDRAW));
        transaction.setPoundage(config.getPoundage());
        transaction.setRealNumber(withdrawDTO.getNumber().subtract(BigDecimal.valueOf(config.getPoundage())));
        transaction.setToAddress(withdrawDTO.getAddress());
        transaction.setType(CommonConstants.WITHDRAW);
        transaction.setUserId(getUserId());
        transactionMapper.insert(transaction);
    }
}
