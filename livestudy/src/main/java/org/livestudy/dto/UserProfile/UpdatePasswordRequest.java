package org.livestudy.dto.UserProfile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {

    @NotBlank
    @Size(max = 70)
    private String currentPassword;

    @NotBlank
    @Size(max = 70)
    private String newPassword;

    @NotBlank
    @Size(max = 70)
    private String confirmNewPassword;

}
