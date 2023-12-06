package kr.taking.backend.service.Impl;

import kr.taking.backend.repository.RoleRepository;
import kr.taking.backend.repository.UserRepository;
import kr.taking.backend.util.Security.AccessToken;
import kr.taking.backend.error.enums.ErrorCode;
import kr.taking.backend.error.exception.CustomException;
import kr.taking.backend.model.RoleEntity;
import kr.taking.backend.model.UserEntity;
import kr.taking.backend.service.AuthService;
import kr.taking.backend.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * <pre>
 * ClassName : AuthServiceImpl
 * Type : class
 * Description : 인증과 관련된 서비스 구현과 관련된 함수를 포함하고 있는 클래스입니다.
 * Related : spring-boot-starter-data-jpa
 * </pre>
 */
@Slf4j
@Service("authServiceImpl")
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    /**
     * [AuthServiceImpl] 회원가입 함수
     *
     * @param userRegisterDto 회원가입에 필요한 사용자 등록 정보를 담은 개체입니다.
     * @return 생성된 JWT 토큰을 리턴합니다.
     * @throws CustomException 중복된 아이디에 대한 예외 처리 발생
     * <pre>
     * 사용자를 등록하고, 등록된 사용자의 정보를 기반으로 JWT 토큰을 생성하여 반환합니다.
     * </pre>
     *
     * Author : taking(taking@duck.com)
     */
    public AccessToken register(UserEntity.RegisterDto userRegisterDto) {

        Instant instant = Instant.now();
        checkExistsWithUserId(userRegisterDto.getUserid()); // 동일한 이름 중복체크

        RoleEntity roleEntity = roleRepository.findByName("ROLE_USER").get();

        UserEntity userEntity = UserEntity.builder()
            .userid(userRegisterDto.getUserid())
            .username(userRegisterDto.getUsername())
            .email(userRegisterDto.getEmail())
            .password(passwordEncoder.encode(userRegisterDto.getPassword()))
            .role(roleEntity)
            .enabled(true)
            .created_at(instant)
            .build();

        userRepository.save(userEntity);

        String userid = userEntity.getUserid();
        RoleEntity role = userEntity.getRole();

        return tokenService.generateJwtToken(userid,role);
    }

    /**
     * [AuthServiceImpl] 로그인 함수
     *
     * @param userLoginDto 로그인에 필요한 사용자 등록 정보를 담은 개체입니다.
     * @return 생성된 JWT 토큰을 리턴합니다.
     * @throws CustomException 입력 값이 유효하지 않거나 사용자가 등록되지 않았을 경우 예외 처리 발생
     * <pre>
     * 사용자의 로그인 정보를 검증하고, 검증된 사용자의 정보를 기반으로 JWT 토큰을 생성하여 반환합니다.
     * </pre>
     *
     * Author : taking(taking@duck.com)
     */
    @Transactional(readOnly = true)
    public AccessToken.Get login(UserEntity.LoginDto userLoginDto) {

        authenticateByIdAndPassword(userLoginDto);

        Optional<UserEntity> userEntity = userRepository.findByuserid(userLoginDto.getUserid());
        AccessToken accessToken = tokenService.generateJwtToken(userLoginDto.getUserid(), userEntity.get().getRole());

        return AccessToken.Get.builder()
            .accessToken(accessToken.getAccessToken())
            .userId(userEntity.get().getUserid())
            .userName(userEntity.get().getUsername())
            .userRole(userEntity.get().getRole())
            .build();

    }

    /**
     * [AuthServiceImpl] 아이디 중복 체크 함수
     *
     * @param userid 중복 체크에 필요한 사용자 아이디 객체입니다.
     * @throws CustomException 사용자의 아이디가 중복된 경우 예외 처리 발생
     * <pre>
     * 입력된 사용자 아아디로 이미 등록된 사용자가 있는지 확인합니다.
     * </pre>
     *
     * Author : taking(taking@duck.com)
     */
    @Transactional(readOnly = true)
    public void checkExistsWithUserId(String userid) {
        if (userRepository.findByuserid(userid).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE); // 사용자 아이디가 중복됨
        }
    }

    /**
     * [AuthServiceImpl] 로그인 정보 검증 함수
     *
     * @param userLoginDto userLoginDto 로그인 정보 검증에 필요한 사용자 등록 정보를 담은 개체입니다.
     * @throws CustomException 사용자가 등록되지 않았거나, 비밀번호가 일치하지 않을 경우 예외 처리 발생
     * <pre>
     * 입력된 사용자 로그인 정보를 검증합니다.
     * </pre>
     *
     * Author : taking(taking@duck.com)
     */
    private void authenticateByIdAndPassword(UserEntity.LoginDto userLoginDto) {

        if(userLoginDto == null) {  // Body 값이 비어 있을 경우, 예외처리
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);   // 입력 값이 유효하지 않음
        }

        UserEntity user = userRepository.findByuserid(userLoginDto.getUserid())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));   // 로그인 정보가 유효하지 않음

        if(!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            log.error("{} Account Password is Corrent!", userLoginDto.getUserid());
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);   // 로그인 정보가 정확하지 않음
        }
    }

    /**
     * [AuthServiceImpl] 역할 정보 조회 함수
     *
     * @param roles Roles 정보를 포함하고 있습니다.
     * @return Roles 정보를 담은 Set<Entity>
     * <pre>
     * 입력된 사용자 정보를 조회하여, Set<RoleEntity> 형태로 역할을 반환합니다.
     * </pre>
     *
     * Author : taking(taking@duck.com)
     */
    // private Set<RoleEntity> getRoles(String [] roles){
    // Set<RoleEntity> userRoles = new HashSet<>();
    // for (String role : roles) {
    //     Optional<RoleEntity> optionalRole = roleRepository.findByName(role);
    //     if (optionalRole.isPresent()) {
    //         userRoles.add(optionalRole.get()); // Extract the RoleEntity from Optional
    //     } else {
    //         // Handle the case when role is not found
    //         // You can throw an exception or perform other error handling
    //     }
    // }
    // return userRoles;
    // }
}
