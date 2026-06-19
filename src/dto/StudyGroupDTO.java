package dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudyGroupDTO {
    private int groupId;
    private int categoryId;
    private String groupName;
    private String groupStatus;
    private Date createdAt;
}
