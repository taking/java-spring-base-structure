package kr.taking.backend.util.Security;

import kr.taking.backend.model.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessToken {

    private String accessToken;

    //////////////////////////////////////////////////////////////////////////

    @Getter
    @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class Get {

        private String userSeq;
        private String userId;
        private String userName;
        private RoleEntity userRole;
        private String accessToken;
        private String refreshToken;

    }

}