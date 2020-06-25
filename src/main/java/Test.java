import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
public class Test {
    public static void main(String... args) throws InterruptedException, IOException {


        PdfConvertClient client = new PdfConvertClientImpl("localhost", "8083");

        String downloadDir = "/data/1/pdfConvertedDir/";
        String filePath = "/data/1/testFiles/TestDoc.docx";

		// the first arg, if present, is the file path (i.e. "/data/1/testFiles/TestDoc.docx")		
        if (args.length == 1) {
            filePath = args[0];
        }

        File file = new File(filePath);

        log.info("Exists? " + file.exists());
        ConvertResult result = client.convert(ConvertRequest.builder().file(file).build());

        log.info("jobId is {}", result.getJobId());

        String jobId = result.getJobId();

        // Get Status
        boolean complete = false;
        JobStatus status = null;
        while(!complete) {
            status = client.getStatus(jobId);
            log.info("Status: " + status.getStatus());

            complete = status.isComplete();
            Thread.sleep(5000);
        }

        log.info("Status: " + status.getStatus());

        DownloadResult dr = client.download(jobId);
        File targetFile = new File(downloadDir + dr.getFilename());

        // TODO - Create a File Processor in DownloadRequest to process inputstream such as copying it to a destination.
        FileUtils.copyInputStreamToFile(dr.getInputStream(), targetFile);
    }
}
