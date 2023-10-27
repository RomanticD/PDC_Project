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
public class Submission {
    private int submissionID;
    private int studentID;
    private int assignmentID;
    private Date submissionTime;
    private String submissionContent;
    private int submissionOrder;
    private String submissionStatus;
    private String evaluation;
}
