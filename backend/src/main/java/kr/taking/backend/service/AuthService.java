package kr.taking.backend.service;

import kr.taking.backend.util.Security.AccessToken;
import kr.taking.backend.model.UserEntity;
import org.springframework.stereotype.Component;


/**
 * <pre>
 * ClassName : AuthService
 * Type : interface
 * Description : Auth와 관련된 함수를 정리한 인터페이스입니다.
 * Related : AuthController, AuthServiceImpl
 * </pre>
 */
@Component
public interface AuthService {
    AccessToken register(UserEntity.RegisterDto userRegisterDto);
    AccessToken.Get login(UserEntity.LoginDto userLoginDto);
    void checkExistsWithUserId(String userid);
}
