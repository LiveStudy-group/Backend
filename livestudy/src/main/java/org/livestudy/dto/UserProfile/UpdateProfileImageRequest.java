package org.livestudy.dto.UserProfile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileImageRequest {

    @NotBlank
    @Size(max = 1024)
    private String newProfileImage;

}
