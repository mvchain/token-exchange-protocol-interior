package com.mvc.sell.console.service;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * oss service
 *
 * @author qiyichen
 * @create 2018/3/16 10:24
 */
@Service
public class OssService {

    @Autowired
    OSSClient ossClient;
    @Value("${oss.endpoint}")
    private String endpoint;
    @Value("${oss.accessKeyId}")
    private String accessKeyId;
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${oss.bucketName}")
    private String bucketName;

    public Map<String, String> doGetSignature(String dir) throws UnsupportedEncodingException {
        String host = "http://" + bucketName + "." + endpoint;
        long expireTime = 300;
        long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        Date expiration = new Date(expireEndTime);
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
        String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes("utf-8");
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = ossClient.calculatePostSignature(postPolicy);
        Map<String, String> responseMap = new LinkedHashMap<String, String>();
        responseMap.put("accessid", accessKeyId);
        responseMap.put("policy", encodedPolicy);
        responseMap.put("signature", postSignature);
        responseMap.put("dir", dir);
        responseMap.put("host", host);
        responseMap.put("expire", String.valueOf(expireEndTime / 1000));
        return responseMap;
    }

}
