package my.study.spring_security_jwt.service;

import lombok.RequiredArgsConstructor;
import my.study.spring_security_jwt.dto.JoinDTO;
import my.study.spring_security_jwt.entity.UserEntity;
import my.study.spring_security_jwt.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public void joinProcess(JoinDTO joinDTO) {
        if (userRepository.existsByUsername(joinDTO.getUsername()))
            return;

        UserEntity user = modelMapper.map(joinDTO, UserEntity.class);
        user.encodePassword(passwordEncoder); //패스워드 인코딩 필수
        user.setRole("ROLE_ADMIN");

        userRepository.save(user);
    }
}
