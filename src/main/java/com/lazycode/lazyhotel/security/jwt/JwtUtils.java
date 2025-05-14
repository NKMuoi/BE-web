package com.lazycode.lazyhotel.security.jwt;

import com.lazycode.lazyhotel.security.user.HotelUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;


@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.jwtExpirationInMils}")
    private int jwtExpirationMs;

    public String generateJwtTokenForUser(Authentication authentication) {
        HotelUserDetails userPrincipal = (HotelUserDetails) authentication.getPrincipal();
        List<String> roles = userPrincipal.
                getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + jwtExpirationMs);

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey key() {
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

        // Log giá trị của key để kiểm tra
        logger.info("Generated SecretKey: {}", secretKey);
    
        return secretKey;
    }
    public String getUserNameFromToken(String token){
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token).getPayload().getSubject();
    }
   public boolean validateToken(String token) {
    try {
        Jws<Claims> claims = Jwts.parserBuilder()  // Thay đổi từ parser() sang parserBuilder()
                .setSigningKey(key())  // Đặt khóa ký
                .build()
                .parseClaimsJws(token);  // Giải mã token

        // Kiểm tra xem token có hết hạn hay không
        return !claims.getBody().getExpiration().before(new Date());
    } catch (MalformedJwtException e) {
        logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
        logger.error("Expired JWT token: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
        logger.error("Unsupported JWT token: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
        logger.error("JWT token is empty: {}", e.getMessage());
    }
    return false;
}

}
