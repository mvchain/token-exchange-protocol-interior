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
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
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
    @Autowired
    OrderService orderService;

    public PageInfo<ProjectVO> list() {
        List<Project> list = projectMapper.selectAll();
        PageInfo<Project> page = new PageInfo<>(list);
        return (PageInfo<ProjectVO>) BeanUtil.beanList2VOList(page, ProjectVO.class);
    }

    public void insert(ProjectDTO projectDTO) {
        Project project = (Project) BeanUtil.copyProperties(projectDTO, new Project());
        projectMapper.insertSelective(project);
        Config config = new Config();
        config.setProjectId(project.getId());
        config.setTokenName(project.getTokenName());
        configService.insert(config);
        setUnit(config.getId(), project.getDecimals());
        ProjectSold projectSold = new ProjectSold();
        projectSold.setId(project.getId());
        projectSold.setBuyerNum(0);
        projectSold.setSendToken(BigDecimal.ZERO);
        projectSold.setSoldEth(BigDecimal.ZERO);
        tokenSoldMapper.insert(projectSold);
    }

    public static void main(String[] args) {
        Arrays.stream(Convert.Unit.values()).forEach(obj -> System.out.println(obj.getWeiFactor().toString().length() - 1));
    }

    private void setUnit(BigInteger id, Integer decimals) {
        Arrays.stream(Convert.Unit.values()).forEach(obj -> {
                    int value = obj.getWeiFactor().toString().length() - 1;
                    if (decimals == value) {
                        redisTemplate.opsForValue().set(RedisConstants.UNIT + "#" + id, obj);

                    }
                }
        );
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
        addOrder(buyDTO, balance);
        // update order number
        Account account = accountService.getAccount(getUserId());
        Integer orderNum = account.getOrderNum();
        orderNum = null == orderNum ? 1 : orderNum++;
        account.setOrderNum(orderNum);
        accountService.update(account);
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
        ProjectSold projectSold = new ProjectSold();
        projectSold.setId(projectId);
        projectSold.setSoldEth(ethNumber);
        projectMapper.updateSoldBalance(projectSold);
    }

    public WithdrawInfoVO getWithdrawConfig(String tokenName) {
        Config config = getConfig(tokenName);
        Assert.notNull(config, CommonConstants.TOKEN_ERR);
        WithdrawInfoVO withdrawInfoVO = (WithdrawInfoVO) BeanUtil.copyProperties(config, new WithdrawInfoVO());
        Capital capital = new Capital();
        capital.setUserId(getUserId());
        capital.setTokenId(config.getId());
        capital = capitalMapper.selectOne(capital);
        withdrawInfoVO.setBalance(null == capital ? BigDecimal.ZERO : capital.getBalance());
        String key = RedisConstants.TODAY_USER + "#" + tokenName + "#" + getUserId();
        BigDecimal use = (BigDecimal) redisTemplate.opsForValue().get(key);
        withdrawInfoVO.setTodayUse(null == use ? BigDecimal.ZERO : use);
        return withdrawInfoVO;
    }

    public void withdraw(WithdrawDTO withdrawDTO) {
        // check
        checkAccount(withdrawDTO);
        Config config = getConfig(withdrawDTO.getTokenName());
        Assert.notNull(config, CommonConstants.TOKEN_ERR);
        checkCanWithdraw(withdrawDTO, config);
        checkEthBalance(withdrawDTO, config);
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
        capitalMapper.updateBalance(getUserId(), config.getId(), BigDecimal.ZERO.multiply(withdrawDTO.getNumber()));
    }

    private void checkEthBalance(WithdrawDTO withdrawDTO, Config config) {
        Capital capital = new Capital();
        capital.setTokenId(config.getId());
        capital.setUserId(getUserId());
        capital = capitalMapper.selectOne(capital);
        Assert.isTrue(null != capital && capital.getBalance().compareTo(withdrawDTO.getNumber()) > 0, CommonConstants.ETH_NOT_ENOUGH);
    }

    private void checkCanWithdraw(WithdrawDTO withdrawDTO, Config config) {
        String addressKey = RedisConstants.LISTEN_ETH_ADDR + "#" + withdrawDTO.getAddress();
        // 不能提现到临时地址
        Assert.isTrue(!redisTemplate.hasKey(addressKey), MessageConstants.ADDERSS_ERROR);
        String key = RedisConstants.TODAY_USER + "#" + withdrawDTO.getTokenName() + "#" + getUserId();
        BigDecimal use = (BigDecimal) redisTemplate.opsForValue().get(key);
        use = null == use ? BigDecimal.ZERO : use;
        Boolean canWithdraw = BigDecimal.valueOf(config.getMax()).subtract(use).compareTo(withdrawDTO.getNumber()) > 0;
        Assert.isTrue(canWithdraw, CommonConstants.NOT_ENOUGH);
    }

    private Config getConfig(String tokenName) {
        Config config = new Config();
        config.setTokenName(tokenName);
        config.setNeedShow(1);
        config = configMapper.selectOne(config);
        return config;
    }

    private void checkAccount(WithdrawDTO withdrawDTO) {
        AccountVO account = accountService.get(getUserId());
        Assert.isTrue(null != account && account.getTransactionPassword().equalsIgnoreCase(withdrawDTO.getTransactionPassword()), CommonConstants.USER_PWD_ERR);
    }

    public Project getByContractAddress(String contractAddress) {
        Project project = new Project();
        project.setContractAddress(contractAddress);
        return projectMapper.selectOne(project);

    }

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

    public void delete(BigInteger id) {
        Project project = getNotNullById(id);
        Boolean canDelete = project.getStatus().equals(0) || project.getSendToken().equals(1);
        Assert.isTrue(canDelete, MessageConstants.CANNOT_DELETE);
        projectMapper.delete(project);
        configService.deleteByProjectId(project.getId());
    }

    private Project getNotNullById(BigInteger id) {
        Project project = new Project();
        project.setId(id);
        project = projectMapper.selectByPrimaryKey(project);
        // 项目开始前或项目发币后可用
        Assert.notNull(project, MessageConstants.PROJECT_NOT_EXIST);
        return project;
    }

    public void sendToken(BigInteger id, Integer sendToken) {
        Project project = getNotNullById(id);
        // 当前未发币且项目结束后可用
        Boolean canSend = project.getStatus().equals(2) && project.getSendToken() == 0;
        Assert.isTrue(canSend, MessageConstants.CANNOT_SEND_TOKEN);
        project.setSendToken(1);
        projectMapper.updateByPrimaryKeySelective(project);
        orderService.updateStatusByProject(id, CommonConstants.ORDER_STATUS_SEND_TOKEN);
    }

    public void retire(BigInteger id, Integer retire) {
        Project project = getNotNullById(id);
        // 项目结束后可用, 使用一次此功能后或此项目代币开放提币后禁用
        Config config = configService.getByPorjectId(project.getId());
        Boolean canRetire = project.getStatus().equals(2) && project.getRetire().equals(0) && config.getRechargeStatus().equals(0);
        Assert.isTrue(canRetire, MessageConstants.CANNOT_RETIRE);
        project.setRetire(1);
        projectMapper.updateByPrimaryKeySelective(project);
        orderService.updateStatusByProject(id, CommonConstants.ORDER_STATUS_RETIRE);
    }

    public Project getByTokenId(BigInteger tokenId) {
        Config config = configService.getByTokenId(tokenId);
        Project project = new Project();
        project.setId(config.getProjectId());
        return projectMapper.selectByPrimaryKey(project);
    }

    public List<Project> select(Project project) {
        return projectMapper.select(project);
    }
}
