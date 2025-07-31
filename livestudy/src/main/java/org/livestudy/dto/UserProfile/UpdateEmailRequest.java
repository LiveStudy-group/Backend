package org.livestudy.dto.UserProfile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailRequest {

    @NotBlank
    @Size(max = 100)
    private String newEmail;

}
