package br.com.oliveirawillian.services;

import br.com.oliveirawillian.data.dto.v1.PersonDTO;
import br.com.oliveirawillian.data.dto.v1.security.AccountCredentialsDTO;
import br.com.oliveirawillian.data.dto.v1.security.TokenDTO;
import br.com.oliveirawillian.exception.RequiredObjectIsNullException;
import br.com.oliveirawillian.mapper.AccountCredentialsMapper;
import br.com.oliveirawillian.model.User;
import br.com.oliveirawillian.repository.UserRepository;
import br.com.oliveirawillian.security.jwt.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountCredentialsMapper accountCredentialsMapper;


    Logger logger = LoggerFactory.getLogger(AuthService.class);

    public ResponseEntity<TokenDTO> signIn(AccountCredentialsDTO accountCredentialsDTO) throws Exception{
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(accountCredentialsDTO.getUserName(),accountCredentialsDTO.getPassword()));
        var user = userRepository.findbyUsername(accountCredentialsDTO.getUserName());
        if(user == null) {throw new UsernameNotFoundException("Username" + accountCredentialsDTO.getUserName() + "not found!");}
        var token = jwtTokenProvider.createAccessToken(accountCredentialsDTO.getUserName(),user.getRoles());
        return ResponseEntity.ok(token);
    }

    public ResponseEntity<TokenDTO> refreshToken(String userName,String refreshToken){
        var user = userRepository.findbyUsername(userName);
        TokenDTO token;
        if (user != null) {
            token = jwtTokenProvider.refreshAccessToken(refreshToken);
        } else {
            throw new UsernameNotFoundException("Username " + userName + " not found!");
        }
        return ResponseEntity.ok(token);
    }

    private String generateHashedPassword(String password) {

        PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder("",
                8,
                185000,
                Pbkdf2PasswordEncoder
                        .SecretKeyFactoryAlgorithm
                        .PBKDF2WithHmacSHA256);
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("pbkdf2", pbkdf2Encoder);
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);
        var pass1 = passwordEncoder.encode("admin123");
        return passwordEncoder.encode(password);
    }

    public AccountCredentialsDTO create(AccountCredentialsDTO accountCredentialsDTO) {
        if (accountCredentialsDTO == null) throw new RequiredObjectIsNullException();
        logger.info("Creating one new User!");
        var entity = new User();
        entity.setFullName(accountCredentialsDTO.getFullName());
        entity.setUserName(accountCredentialsDTO.getUserName());
        entity.setPassword(generateHashedPassword(accountCredentialsDTO.getPassword()));
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);

        return accountCredentialsMapper.toDTO(userRepository.save(entity));

    }

}
