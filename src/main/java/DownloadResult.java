import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

@Getter
@Builder
public class DownloadResult {
    String filename;
    InputStream inputStream;
}
