import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobStatus {
    private String jobId;
    private String fileName;
    private String status;
    private String message;

    public boolean isComplete() {
        return this.status.equalsIgnoreCase("FINISHED");
    }
}
