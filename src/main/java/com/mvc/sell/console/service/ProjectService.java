package com.mvc.sell.console.service;

import com.github.pagehelper.PageInfo;
import com.mvc.common.context.BaseContextHandler;
import com.mvc.sell.console.constants.CommonConstants;
import com.mvc.sell.console.constants.MessageConstants;
import com.mvc.sell.console.constants.RedisConstants;
import com.mvc.sell.console.pojo.bean.*;
import com.mvc.sell.console.pojo.dto.BuyDTO;
import com.mvc.sell.console.pojo.dto.MyProjectDTO;
import com.mvc.sell.console.pojo.dto.ProjectDTO;
import com.mvc.sell.console.pojo.dto.WithdrawDTO;
import com.mvc.sell.console.pojo.vo.*;
import com.mvc.sell.console.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * project service
 *
 * @author qiyichen
 * @author dreamingodd
 * @create 2018/3/13 11:25
 * <p>
 * Added withdraw availability(status) of the token of the project symbol in the list.
 * @updated 2018/4/2
 */
@Service
public class ProjectService extends BaseService {

    @Autowired
    ConfigService configService;
    @Autowired
    AccountService accountService;
    @Autowired
    OrderService orderService;
    @Value("${wallet.user}")
    String defaultUser;
    @Value("${wallet.coldUser}")
    String coldUser;
    @Value("${wallet.xlm.user}")
    String xlmUser;

    public PageInfo<ProjectVO> list() {
        List<Project> list = projectMapper.selectAll();
        PageInfo<Project> page = new PageInfo<>(list);
        PageInfo<ProjectVO> voPage = (PageInfo<ProjectVO>) BeanUtil.beanList2VOList(page, ProjectVO.class);
        Map<String, Config> configMap = configService.map();
        for (ProjectVO projectVO : voPage.getList()) {
            projectVO.setTokenWithdrawStatus(0);
            Config config = configMap.get(projectVO.getTokenName());
            if (config != null) {
                projectVO.setTokenWithdrawStatus(config.getWithdrawStatus());
            }
        }
        return voPage;
    }

    public void insert(ProjectDTO projectDTO) {
        Project project = (Project) BeanUtil.copyProperties(projectDTO, new Project());
        projectMapper.insertSelective(project);
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
        // update order number
        Account account = accountService.getAccount(getUserId());
        Assert.isTrue(encoder.matches(buyDTO.getTransactionPassword(), account.getTransactionPassword()), MessageConstants.getMsg("TRANSFER_PWD_ERR"));
        ProjectVO project = get(buyDTO.getProjectId());
        Assert.notNull(project, MessageConstants.getMsg("PROJECT_NOT_EXIST"));
        BigDecimal sold = getSold(buyDTO.getProjectId()).getSoldEth();
        Assert.isTrue(sold.add(buyDTO.getEthNumber()).compareTo(project.getEthNumber()) <= 0, MessageConstants.getMsg("ETH_OVER"));
        BigDecimal balance = buyDTO.getEthNumber().multiply(new BigDecimal(project.getRatio()));
        // update ethBalance
        Capital ethCapital = new Capital();
        ethCapital.setUserId(getUserId());
        ethCapital.setTokenId(BigInteger.ZERO);
        Integer result = capitalMapper.updateEth(getUserId(), buyDTO.getEthNumber(), getTokenIdByNameIgnoreCase(project.getCoin()));
        Assert.isTrue(result > 0, MessageConstants.getMsg("ETH_NOT_ENOUGH"));
        // add order
        addOrder(buyDTO, balance);
        Integer orderNum = account.getOrderNum();
        orderNum = null == orderNum ? 1 : ++orderNum;
        account.setOrderNum(orderNum);
        accountService.update(account);
        sold = getSold(buyDTO.getProjectId()).getSoldEth();
        Assert.isTrue(sold.compareTo(project.getEthNumber()) <= 0, MessageConstants.getMsg("ETH_OVER"));
        String key = RedisConstants.USER_PROJECTS + "#" + getUserId();
        redisTemplate.opsForValue().set(key, orderMapper.getUserProject(getUserId()));
    }

    private void addOrder(BuyDTO buyDTO, BigDecimal balance) {
        Orders orders = new Orders();
        orders.setUserId(getUserId());
        orders.setEthNumber(buyDTO.getEthNumber());
        orders.setOrderId(getOrderId(CommonConstants.ORDER_BUY));
        orders.setOrderStatus(0);
        orders.setProjectId(buyDTO.getProjectId());
        orders.setTokenNumber(balance);
        orderMapper.insert(orders);
        updateSold(buyDTO.getProjectId(), buyDTO.getEthNumber(), balance);
    }

    private void updateSold(BigInteger projectId, BigDecimal ethNumber, BigDecimal balance) {
        Orders orders = new Orders();
        orders.setUserId(getUserId());
        orders.setProjectId(projectId);
        ProjectSold projectSold = new ProjectSold();
        projectSold.setId(projectId);
        projectSold.setSoldEth(ethNumber);
        projectSold.setBuyerNum(1);
        projectMapper.updateSoldBalance(projectSold);
    }

    public WithdrawInfoVO getWithdrawConfig(String tokenName) {
        Config config = getConfig(tokenName);
        Assert.notNull(config, MessageConstants.getMsg("TOKEN_ERR"));
        WithdrawInfoVO withdrawInfoVO = (WithdrawInfoVO) BeanUtil.copyProperties(config, new WithdrawInfoVO());
        Capital capital = new Capital();
        capital.setUserId(getUserId());
        capital.setTokenId(config.getId());
        capital = capitalMapper.selectOne(capital);
        withdrawInfoVO.setBalance(null == capital ? BigDecimal.ZERO : capital.getBalance());
        String key = RedisConstants.TODAY_USER + "#" + tokenName + "#" + getUserId() + "#" + new SimpleDateFormat("YYYYMMdd").format(new Date());
        BigDecimal use = (BigDecimal) redisTemplate.opsForValue().get(key);
        withdrawInfoVO.setTodayUse(null == use ? BigDecimal.ZERO : use);
        return withdrawInfoVO;
    }

    public void withdraw(WithdrawDTO withdrawDTO) {
        // check
        checkAccount(withdrawDTO);
        Config config = getConfig(withdrawDTO.getTokenName());
        Assert.notNull(config, MessageConstants.getMsg("TOKEN_ERR"));
        BigDecimal realNumber = withdrawDTO.getNumber().subtract(BigDecimal.valueOf(config.getPoundage()));
        Assert.isTrue(withdrawDTO.getNumber().compareTo(BigDecimal.ZERO) >= 0, MessageConstants.getMsg("ETH_NOT_ENOUGH"));
        checkCanWithdraw(withdrawDTO, config);
        checkEthBalance(withdrawDTO, config);
        // add trans
        Transaction transaction = new Transaction();
        transaction.setStatus(0);
        transaction.setNumber(withdrawDTO.getNumber());
        transaction.setOrderId(getOrderId(CommonConstants.ORDER_WITHDRAW));
        transaction.setPoundage(config.getPoundage());
        transaction.setRealNumber(realNumber);
        transaction.setToAddress(withdrawDTO.getAddress());
        transaction.setType(CommonConstants.WITHDRAW);
        transaction.setUserId(getUserId());
        transaction.setTokenId(config.getId());
        String fromUser = coldUser;
        if (config.getContractAddress().equalsIgnoreCase("XLM") || config.getContractAddress().startsWith("XLM-")) {
            fromUser = xlmUser;
        }
        transaction.setFromAddress(fromUser);
        transactionMapper.insert(transaction);
        capitalMapper.updateBalance(getUserId(), config.getId(), BigDecimal.ZERO.subtract(withdrawDTO.getNumber()));
        String key = RedisConstants.TODAY_USER + "#" + withdrawDTO.getTokenName() + "#" + getUserId() + "#" + new SimpleDateFormat("YYYYMMdd").format(new Date());
        BigDecimal use = (BigDecimal) redisTemplate.opsForValue().get(key);
        use = use == null ? withdrawDTO.getNumber() : use.add(withdrawDTO.getNumber());
        redisTemplate.opsForValue().set(key, use);
        redisTemplate.expire(key, 24, TimeUnit.HOURS);
    }

    private void checkCanWithdraw(WithdrawDTO withdrawDTO, Config config) {
        String addressKey = RedisConstants.LISTEN_ETH_ADDR + "#" + withdrawDTO.getAddress();
        // 不能提现到临时地址
        Assert.isTrue(!redisTemplate.hasKey(addressKey), MessageConstants.getMsg("ADDRESS_ERROR"));
        String key = RedisConstants.TODAY_USER + "#" + withdrawDTO.getTokenName() + "#" + getUserId() + "#" + new SimpleDateFormat("YYYYMMdd").format(new Date());
        BigDecimal use = (BigDecimal) redisTemplate.opsForValue().get(key);
        use = null == use ? BigDecimal.ZERO : use;
        Boolean canWithdraw = BigDecimal.valueOf(config.getMax()).subtract(use).compareTo(withdrawDTO.getNumber()) >= 0;
        Assert.isTrue(canWithdraw, MessageConstants.getMsg("NOT_ENOUGH"));
    }

    private void checkEthBalance(WithdrawDTO withdrawDTO, Config config) {
        Capital capital = new Capital();
        capital.setTokenId(config.getId());
        capital.setUserId(getUserId());
        capital = capitalMapper.selectOne(capital);
        Assert.isTrue(null != capital && capital.getBalance().compareTo(withdrawDTO.getNumber()) > 0, MessageConstants.getMsg("ETH_NOT_ENOUGH"));
    }

    private Config getConfig(String tokenName) {
        Config config = new Config();
        config.setTokenName(tokenName);
        config.setNeedShow(1);
        config = configMapper.selectOne(config);
        return config;
    }

    private void checkAccount(WithdrawDTO withdrawDTO) {
        AccountVO account = accountService.get(getUserId(), null);
        Assert.isTrue(encoder.matches(withdrawDTO.getTransactionPassword(), account.getTransactionPassword()), MessageConstants.getMsg("TRANSFER_USER_PWD_ERR"));
    }

    @Async
    public Integer updateStatus() {
        Integer number = 0;
        number = number + projectMapper.updateStart();
        number = number + projectMapper.updateFinish();
        return number;
    }

    public void updateShow(BigInteger id, Integer show) {
        Project project = new Project();
        project.setId(id);
        project.setNeedShow(show);
        projectMapper.updateByPrimaryKeySelective(project);
    }

    private Project getNotNullById(BigInteger id) {
        Project project = new Project();
        project.setId(id);
        project = projectMapper.selectByPrimaryKey(project);
        // 项目开始前或项目发币后可用
        Assert.notNull(project, MessageConstants.getMsg("PROJECT_NOT_EXIST"));
        return project;
    }

    public void sendToken(BigInteger id, Integer sendToken) {
        Project project = getNotNullById(id);
        Config config = configService.getConfigByTokenName(project.getTokenName());
        // 当前未发币且项目结束后可用
        Boolean canSend = project.getStatus().equals(2) && project.getSendToken() == 0;
        Assert.isTrue(canSend, MessageConstants.getMsg("CANNOT_SEND_TOKEN"));
        project.setSendToken(1);
        projectMapper.updateByPrimaryKeySelective(project);
        projectMapper.sendToken(id, config.getId());
        orderService.updateStatusByProject(id, CommonConstants.ORDER_STATUS_SEND_TOKEN);
    }

    public void retire(BigInteger id, Integer retire) {
        Project project = getNotNullById(id);
        // 项目结束后可用, 使用一次此功能后或此项目代币开放提币后禁用
        Config config = configService.getConfigByTokenName(project.getTokenName());
        Boolean canRetire = project.getStatus().equals(2) && project.getRetire().equals(0) && config.getWithdrawStatus().equals(0);
        Assert.isTrue(canRetire, MessageConstants.getMsg("CANNOT_RETIRE"));
        project.setRetire(1);
        projectMapper.updateByPrimaryKeySelective(project);
        Orders orders = new Orders();
        orders.setProjectId(id);
        if (orderMapper.selectCount(orders) > 0) {
            BigInteger tokenId = getTokenIdByNameIgnoreCase(project.getCoin());
            projectMapper.retireBalance(id, tokenId);
            projectMapper.retireToken(id, config.getId());
            orderService.retireToken(id, CommonConstants.ORDER_STATUS_RETIRE);
        }
    }

    public BigInteger getTokenIdByNameIgnoreCase(String tokenName) {
        Config config = new Config();
        config.setTokenName(tokenName);
        config = configMapper.selectOne(config);
        if (null != config) {
            return config.getId();
        }
        return BigInteger.ZERO;
    }

    public List<Project> select(Project project) {
        return projectMapper.select(project);
    }

    public void delete(BigInteger id) {
        Project project = getNotNullById(id);
        Boolean canDelete = project.getStatus().equals(0) || project.getSendToken().equals(1);
        Assert.isTrue(canDelete, MessageConstants.getMsg("CANNOT_DELETE"));
        projectMapper.delete(project);
    }
}
