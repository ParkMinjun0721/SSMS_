package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudyCategoryDTO {
    private int categoryId;
    private String categoryName;
    private String description;
}
