import lombok.Builder;
import lombok.Getter;

import java.io.File;

@Builder
@Getter
public class ConvertRequest {
    private File file;
}
