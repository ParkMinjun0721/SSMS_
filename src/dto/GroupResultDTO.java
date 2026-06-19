package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GroupResultDTO {
    private int groupId;
    private String categoryName;
    private String groupName;
    private int studentId;
    private String studentName;
    private String memberRole;
    private int matchedPriority;
}
