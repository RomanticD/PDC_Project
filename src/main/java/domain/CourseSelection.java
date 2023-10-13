package domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseSelection {
    private int id;
    private int userId;
    private int courseId;
    private String selectionStatus;
    private Date selectionTime;
}
