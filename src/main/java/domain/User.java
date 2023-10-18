package domain;

import domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private int userId;

    private String name;

    private String username;

    private String password;

    private String email;

    private Role role;

    public boolean isAdmin(){
        return role.toString().equals("ADMIN");
    }
}
