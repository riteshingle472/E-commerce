package org.riteshingle.ecommerce.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.riteshingle.ecommerce.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private String secretKey = JWT_CONSTANT.secretKey;
    private final UserRepository userRepository;

    public SecretKey getKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .signWith(getKey())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 48))
                .compact();
    }


    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaims(String token, Function<Claims,T> claimsResolve){
        final Claims claims = extractAllClaims(token);
        return claimsResolve.apply(claims);
    }

    public String extractEmail(String token){
        return extractClaims(token,Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }

    public Boolean isExpire(String token){
        System.out.println("Expiration Time : "+extractExpiration(token));
        System.out.println("Current Time : "+new Date(System.currentTimeMillis()));

        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token , UserDetails userDetails){
        String email = extractEmail(token);
        Boolean matchEmail = email.equals(userDetails.getUsername());
        Boolean expired = isExpire(token);

        System.out.println("Email : "+email);
        System.out.println("Match Email : "+matchEmail);
        System.out.println("Is token expired : "+expired);

        return (!expired && matchEmail);
    }
}
