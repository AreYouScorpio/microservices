package hu.webuni.tokenlib;


import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    private static final String AUTH = "auth";
    private String issuer = "webuni-user-service";

    @Value("${hu.webuni.tokenlib.keypaths.private:#{null}}") //: default ertek // config server-en lasd user-service.yml, itt vannak a pem-ek (privat es publikus kulcsok)
    private String pathToPemWithPrivateKey;
    @Value("${hu.webuni.tokenlib.keypaths.public:#{null}}")
    private String pathToPemWithPublicKey;
    private Algorithm signerAlg; //ezzel alairok
    private Algorithm validatorAlg; //csak validalja alairast
    
    
    
    @PostConstruct // emiatt konstruktor utan ezt hivja meg rajta a Spring
    public void init() throws Exception {
    	if(pathToPemWithPrivateKey != null) { // ez a user-service-nek kell, a tobbinek csak visszafele
    		signerAlg = Algorithm.ECDSA512(null, (ECPrivateKey)PemUtils.getPrivateKey(pathToPemWithPrivateKey));
    	} //EC-s, elliptikus gorbeket hasznalo private key
    	
    	if(pathToPemWithPublicKey != null) {
    		validatorAlg = Algorithm.ECDSA512((ECPublicKey)PemUtils.getPublicKey(pathToPemWithPublicKey), null);
    	}
    }
    
    public String creatJwtToken(UserDetails principal) {
        return JWT.create()
            .withSubject(principal.getUsername())
            .withArrayClaim(AUTH, principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new))
            .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(20)))
            .withIssuer(issuer)
            .sign(signerAlg); // create-nel
        
    }

    public UserDetails parseJwt(String jwtToken) {
        
        DecodedJWT decodedJwt = JWT.require(validatorAlg) //validalasnal
            .withIssuer(issuer)
            .build()
            .verify(jwtToken);
        return new User(decodedJwt.getSubject(), "dummy", 
                decodedJwt.getClaim(AUTH).asList(String.class)
                .stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                );
        
    }

}