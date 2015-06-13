import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        String download_link = "https://github.com/aptana/studio3/releases/download/v3.6.1/Aptana_Studio_3_Setup_Linux_x86_64_3.6.1.zip";
        String file_path = "/tmp/GWorkspace.png";
        int number_of_segments = 3;

        DownLoadManagerPreparing downloader =
                new DownLoadManagerPreparing(download_link, file_path, number_of_segments);
        downloader.start_downloading();
    }
}
