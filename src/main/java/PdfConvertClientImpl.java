import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.InputStream;

@Slf4j
public class PdfConvertClientImpl extends AbstractClientSDK implements PdfConvertClient {
    private static final String SERVICE_NAME = "PDF-CONVERSION-SERVICE";

    public PdfConvertClientImpl(String host, String port) {
        this(host, port, false);
    }

    public PdfConvertClientImpl(String host, String port, boolean isSecure) {
        super(SERVICE_NAME, host, port, isSecure);
    }

    @Override
    public ConvertResult convert(ConvertRequest request) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(request.getFile()));

        return getRestClient().post(getUri(), "pdfConversion/uploadFile", MediaType.MULTIPART_FORM_DATA_VALUE, body,
            ConvertResult.class);
    }

    @Override
    public JobStatus getStatus(String jobId) {
        if(jobId.isEmpty()) {
            throw new IllegalArgumentException("jobID may not be null/empty.");
        }

        try {
            log.info("Running GetStatus at {}...", getUri());
            JobStatus status = getRestClient().get(getUri(), String.format("pdfConversion/getStatus/%s", jobId),
                MediaType.APPLICATION_JSON_VALUE, JobStatus.class);

            return status;
        } catch (Exception e) {
            log.error("Error processing getStatus.", e);
            return null;
        }
    }

    @Override
    public DownloadResult download(String jobId) {
        try {
            log.info("Running download at {}...", getUri());

            Resource in = getRestClient().get(getUri(), String.format("pdfConversion/downloadFile/%s", jobId),
                MediaType.MULTIPART_FORM_DATA_VALUE, Resource.class);

			// remove the jobId from the filename
            String fileName = in.getFilename().substring(6);
            log.info("Filename: " + fileName);

            InputStream is = in.getInputStream();

            return DownloadResult.builder().filename(fileName).inputStream(is).build();
        } catch (Exception e) {
            log.error("Error processing download.", e);

            //TODO - throw exception
            return null;
        }
    }
}
