package dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentDTO {
    private int studentId;
    private String loginId;
    private String password;
    private String name;
    private int cohort;
    private String email;
    private String role;
    private Date createdAt;
}
