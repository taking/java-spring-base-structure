package kr.taking.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * <pre>
 * ClassName : UserEntity
 * Type : class
 * Description : User와 관련된 Entity를 구성하고 있는 클래스입니다.
 * Related : UserRepository, UserServiceImpl
 * </pre>
 */
@Getter
@Builder
@NoArgsConstructor @AllArgsConstructor
@Document(collection = "users")
@JsonPropertyOrder({ "id", "userId", "userName", "userEmail", "userRole", "userEnabled", "userRole", "userOrgs", "created_at" })
public class UserEntity implements Serializable {

    @Id
    @JsonProperty("userSeq")
    @Schema(title = "사용자 고유번호", example = "64ed89aa9e813b5ab16da6de")
    private String id;

    @JsonProperty("userId")
    @Schema(title = "사용자 아이디", example = "admin")
    @Size(min = 4, max = 10, message = "Minimum userId length: 4 characters")
    private String userid;

    @JsonProperty("userName")
    @Schema(title = "사용자 이름", example = "홍길동")
    @Size(min = 2, max = 10, message = "Minimum username length: 4 characters")
    private String username;

    @JsonProperty("userEmail")
    @Schema(title = "사용자 이메일", example = "test@test.com")
    @Email(message = "Email Should Be Valid")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(title = "사용자 비밀번호", example = "Pa@sW0rd")
    @Size(min = 3, message = "Minimum password length: 8 characters")
    private String password;

    @JsonProperty("userEnabled")
    @Schema(title = "사용자 활성화 여부", example = "true")
    private boolean enabled;

    @JsonProperty("created_at")
    @JsonFormat(pattern="yyyy-MM-dd hh:mm:ss", locale = "ko_KR", timezone = "Asia/Seoul")
    @Schema(title = "사용자 생성 시간", example = "2023-07-04 12:00:00")
    private Instant created_at;

    @DBRef
    @JsonProperty("userRole")
    @Schema(title = "사용자 권한 정보", example = "ROLE_USER")
    private RoleEntity role;

    @DBRef
    @Builder.Default
    @JsonProperty("userOrgs")
    @Schema(title = "Org 정보", example = "더모멘트")
    private Set<OrgEntity> orgs = new HashSet<>();

//////////////////////////////////////////////////////////////////////////

    @Getter
    @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class RegisterDto {

        @Schema(title = "사용자 아이디", example = "admin")
        @Size(min = 4, max = 10)
        private String userid;

        @Schema(title = "사용자 이름", example = "홍길동")
        @Size(min = 2, max = 10, message = "Minimum username length: 4 characters")
        private String username;

        @Email
        @Schema(title = "사용자 이메일", example = "admin@innogrid.com")
        @Size(min = 3, max = 40)
        private String email;

        @Schema(title = "사용자 비밀번호", example = "Pa@sW0rd")
        @Size(min = 3, message = "Minimum password length: 8 characters")
        private String password;
    }

    @Getter
    @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class LoginDto {

        @Schema(title = "사용자 아이디", example = "admin")
        @Size(min = 4, max = 10, message = "Minimum admin length: 4 characters")
        private String userid;

        @Schema(title = "사용자 비밀번호", example = "Pa@sW0rd")
        @Size(min = 3, message = "Minimum password length: 8 characters")
        private String password;
    }

    @Getter
    @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class UpdateDto {

        @Schema(title = "사용자 이름", example = "관리자")
        @Size(min = 2, max = 10)
        private String username;

        @Schema(title = "사용자 이메일", example = "admin@innogrid.com")
        @Email
        @Size(min = 3, max = 40)
        private String email;

        @Schema(title = "사용자 비밀번호", example = "Pa@sW0rd")
        @Size(min = 3, max = 40)
        private String password;

    }
}
