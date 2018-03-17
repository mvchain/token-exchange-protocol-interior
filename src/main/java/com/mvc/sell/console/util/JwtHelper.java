package com.mvc.sell.console.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.login.LoginException;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.security.Key;
import java.util.Date;

@Component
public class JwtHelper {

    private static Logger logger = Logger.getLogger(JwtHelper.class);

    public static String serviceName;
    public static Long expire;
    public static Long refresh;
    public static String base64Secret;

    public static Claims parseJWT(String jsonWebToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(base64Secret))
                    .parseClaimsJws(jsonWebToken).getBody();
        } catch (Exception ex) {
            return null;
        }
    }

    public static String createToken(String username, BigInteger userId) {
        return createJWT(username, userId, expire, "token");
    }

    public static String createRefresh(String username, BigInteger userId) {
        return createJWT(username, userId, refresh, "refresh");
    }

    private static String createJWT(String username, BigInteger userId, Long expire, String type) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64Secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //添加构成JWT的参数
        JwtBuilder builder = Jwts.builder()
                .claim("username", username)
                .claim("userId", userId)
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
        BigInteger userId = oldToken.get("userId", BigInteger.class);
        String type = oldToken.get("type", String.class);
        Assert.isTrue(serviceName.equalsIgnoreCase(service) && "refresh".equalsIgnoreCase(type), "token is wrong");
        return createRefresh(username, userId);
    }


    public static void check(Claims claim, String uri, Boolean isFeign) throws LoginException {
        String type = claim.get("type", String.class);
        String service = claim.get("service", String.class);
        if (!serviceName.equalsIgnoreCase(service) && !isFeign) {
            throw new LoginException("service is wrong");
        }
        if (uri.indexOf("/refresh") > 0 && !"refresh".equalsIgnoreCase(type)) {
            throw new LoginException("token type is wrong");
        } else if (uri.indexOf("/refresh") < 0 && !"token".equalsIgnoreCase(type)) {
            throw new LoginException("token type is wrong");
        }
    }
}