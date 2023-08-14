package likelion.hamahama.user.service;

import likelion.hamahama.user.config.auth.JwtProvider;
import likelion.hamahama.user.dto.SignRequest;
import likelion.hamahama.user.dto.SignResponse;
import likelion.hamahama.user.entity.RefreshToken;
import likelion.hamahama.user.entity.User;
import likelion.hamahama.user.repository.RefreshTokenRepository;
import likelion.hamahama.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JwtProvider jwtProvider;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    @Autowired
    private AuthenticationManager authenticationManager;

    public SignResponse login(SignRequest request, HttpServletResponse response) throws Exception {
        //유저가 입력한 이메일을 이용하여 DB에서 정보 가져오기
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new BadCredentialsException("유저정보를 찾을 수 없습니다."));

        //유저가 입력한 비밀번호와 DB에 저장된 비밀번호가 일치한지 검사
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        if (authentication.isAuthenticated()) {
            //RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getUsername());
            String accessToken = jwtProvider.createAccessToken(user.getEmail(), user.getRoles());
            String refreshToken = jwtProvider.createRefreshToken(user.getEmail(), user.getRoles());
            jwtProvider.setHeaderAccessToken(response, accessToken);
            jwtProvider.setHeaderRefreshToken(response, refreshToken);

            RefreshToken refreshEntity = RefreshToken.builder()
                    .token(refreshToken)
                    .expiryDate(jwtProvider.getRefreshTokenExpTime(refreshToken))
                    .user(user)
                    .build();

            refreshTokenRepository.save(refreshEntity);


            return SignResponse.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .role(user.getRoles())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } else {
            throw new UsernameNotFoundException("invalid user request!");
        }


    }

    public boolean register(SignRequest request) throws Exception {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadCredentialsException("이미 존재하는 이메일입니다");
        }
        try {
            User user = User.builder()
                    .password(passwordEncoder.encode(request.getPassword()))
                    .nickname(request.getNickname())
                    .email(request.getEmail())
                    .build();

            List<String> roles = new ArrayList<>();
            roles.add("ROLE_USER");
            roles.add("ROLE_ADMIN");
            user.setRoles(roles);


            userRepository.save(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("잘못된 요청입니다.");
        }
        return true;
    }

    public String reissueAccessToken(String bearerToken) {


        String refreshToken = jwtProvider.resolveToken(bearerToken);
        if (!StringUtils.hasText(refreshToken)) {
            throw null;
        }

        Claims claims = jwtProvider.parseClaimsFromRefreshToken(refreshToken);
        if (claims == null) {
            throw new NullPointerException("claims가 존재하지 않습니다");
        }

        User user = userRepository.findByEmail(claims.getSubject()).orElseThrow(() ->
                new UsernameNotFoundException("해당 이메일을 가진 유저가 존재하지 않습니다."));

        String new_accessToken = jwtProvider.createAccessToken(user.getEmail(), user.getRoles());

        return new_accessToken;
    }
}