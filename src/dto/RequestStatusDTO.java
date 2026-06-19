package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestStatusDTO {
    private int requestId;
    private int studentId;
    private String studentName;
    private String firstCategoryName;
    private String secondCategoryName;
    private String thirdCategoryName;
    private String matchedYn;
}
