package domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Course {
    private int courseID;
    private String courseName;
    private String courseDescription;
    private String instructor;
    private Date deadLine;
}
