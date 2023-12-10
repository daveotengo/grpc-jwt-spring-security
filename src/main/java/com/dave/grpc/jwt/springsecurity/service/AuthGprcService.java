package com.dave.grpc.jwt.springsecurity.service;

import com.dave.grpc.jwt.springsecurity.jwt.JwtAuthProvider;
import com.grpc.solution.AuthServiceGrpc;
import com.grpc.solution.JwtRequest;
import com.grpc.solution.JwtToken;
import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@GrpcService
public class AuthGprcService extends AuthServiceGrpc.AuthServiceImplBase {

    @Value("$jwt.signing.key")
    String jwtSecretKey;

    final private JwtAuthProvider jwtAuthProvider;
    @Override
    public void authenticate(JwtRequest request, StreamObserver<JwtToken> responseObserver) {
        //super.authenticate(request, responseObserver);

        Authentication authenticate = jwtAuthProvider.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword())
        );

        Instant now = Instant.now();
        Instant expiration = now.plus(1, ChronoUnit.HOURS);

        String authorities = authenticate.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        responseObserver.onNext(JwtToken.newBuilder().setJwtToken(Jwts.builder()
                .setSubject((String)authenticate.getPrincipal())
                .claim("auth",authorities)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(SignatureAlgorithm.ES512, jwtSecretKey)
                .compact()).build());

        responseObserver.onCompleted();
    }


}
