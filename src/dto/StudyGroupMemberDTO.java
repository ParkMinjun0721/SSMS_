package dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudyGroupMemberDTO {
    private int groupMemberId;
    private int groupId;
    private int studentId;
    private String memberRole;
    private int matchedPriority;
    private Date joinedAt;
}
