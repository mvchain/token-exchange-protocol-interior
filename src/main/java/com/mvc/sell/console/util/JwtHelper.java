package com.mvc.sell.console.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

@Component
public class JwtHelper {

    private static Logger logger = Logger.getLogger(JwtHelper.class);

    @Value("${service.name}")
    private static String serviceName;
    @Value("${service.expire}")
    private static Long expire;
    @Value("${service.refresh}")
    private static Long refresh;
    @Value("${service.base64Secret}")
    private static String base64Secret;


    private JwtHelper() {
    }

    public static Claims parseJWT(String jsonWebToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(base64Secret))
                    .parseClaimsJws(jsonWebToken).getBody();
        } catch (Exception ex) {
            return null;
        }
    }

    public static String createToken(String username) {
        return createJWT(username, expire, "token");
    }

    public static String createRefresh(String username) {
        return createJWT(username, refresh, "refresh");
    }

    private static String createJWT(String username, Long expire, String type) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //添加构成JWT的参数
        JwtBuilder builder = Jwts.builder()
                .claim("username", username)
                .claim("service", serviceName)
                .claim("type", type)
                .signWith(signatureAlgorithm, signingKey)
                .setExpiration(new Date(System.currentTimeMillis() + expire));
        return builder.compact();
    }

    public static String refresh(String refreshToken) {
        Claims oldToken = parseJWT(refreshToken);
        String username = oldToken.get("username", String.class);
        String service = oldToken.get("service", String.class);
        String type = oldToken.get("type", String.class);
        Assert.isTrue(serviceName.equalsIgnoreCase(service) && "refresh".equalsIgnoreCase(type), "token is wrong");
        return createRefresh(username);
    }


}