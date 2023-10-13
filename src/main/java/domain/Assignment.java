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
public class Assignment {
    private int assignmentID;
    private String assignmentName;
    private int courseID;
    private Date deadline;
}