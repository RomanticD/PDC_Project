package domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {
    private int projectID;
    private String projectName;
    private String projectDescription;
    private int teamSize;
}
