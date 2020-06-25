public interface PdfConvertClient {
    public ConvertResult convert(ConvertRequest request);

    public JobStatus getStatus(String jobId);

    public DownloadResult download(String jobId);
}
