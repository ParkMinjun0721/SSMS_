package dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudyRequestDTO {
    private int requestId;
    private int studentId;
    private int firstCategoryId;
    private Integer secondCategoryId;
    private Integer thirdCategoryId;
    private String matchedYn;
    private Date createdAt;
    private Date updatedAt;
}
