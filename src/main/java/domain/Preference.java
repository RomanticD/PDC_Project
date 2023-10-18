package domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Preference {
    private int userId;
    private boolean rememberMe;
    private String storedUsername;
    private String storedPassword;
}
