package cat.iesmanacor.gateway.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationManagerJwt implements ReactiveAuthenticationManager {

    @Value("${jwt.secret}")
    private String secretJwt;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication.getCredentials().toString())
                .mapNotNull(token -> {
                    try {
                        String tokenParsed = token.replace("Bearer ","");
                        //SecretKey key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(secretJwt.getBytes()));
                        SecretKey key = Keys.hmacShaKeyFor(secretJwt.getBytes());
                        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(tokenParsed).getBody();
                    } catch (Exception e){
                        return null;
                    }
                })
                .map(claims -> {
                    if(claims!=null) {
                        String email = claims.get("email", String.class);
                        List<String> rols = claims.get("rols", List.class);
                        Collection<GrantedAuthority> authorities = rols.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
                        return new UsernamePasswordAuthenticationToken(email, null, authorities);
                    }
                    Authentication a = new UsernamePasswordAuthenticationToken(null,null,null);
                    a.setAuthenticated(false);

                    return a;
                });
    }
}
