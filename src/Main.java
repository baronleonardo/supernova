import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        String download_link = "LINK";
        String file_path = "PATH/file";
        int number_of_segments = 3;

        DownLoadManagerPreparing downloader =
                new DownLoadManagerPreparing(download_link, file_path, number_of_segments);
        downloader.start_downloading();
    }
}
