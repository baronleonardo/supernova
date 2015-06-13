import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

class DownLoadManagerPreparing
{
    String download_link = "";
    String file_path = "";
    //    BufferedInputStream data_stream = null;
    ArrayList<ArrayList> segments;
    int number_of_segments = 1;
    private long stream_size = 0;

    DownLoadManagerPreparing(String download_link, String file_path, int number_of_segments) throws IOException
    {
        this.download_link = download_link;
        this.file_path = file_path;
        this.number_of_segments = number_of_segments;

        // 0: BufferedInputStream, 1: start_byte, 2: end_byte
        this.segments = new ArrayList<ArrayList>();
    }

    void start_downloading() throws IOException, InterruptedException
    {
        this.stream_size = get_stream_size(new URL(this.download_link));
        prepare_segments( this.stream_size, this.number_of_segments );


//        RandomAccessFile file = new RandomAccessFile(this.file_path, "rw");
        RandomAccessFile file = prepare_file( this.file_path );
        DownloadManagerCore core = new DownloadManagerCore(segments, file, this.stream_size, this.number_of_segments);
        core.threading_control("start");


        System.out.println("Start Downloading...");
    }

    long get_stream_size(URL url) throws IOException
    {
        URLConnection url_connection = url.openConnection();

        long stream_size = url_connection.getContentLengthLong();
        System.out.println("File Length: " + Long.toString(stream_size) + " Bytes");

        return stream_size;
    }

    RandomAccessFile prepare_file(String file_path) throws FileNotFoundException
    {
        return new RandomAccessFile(file_path, "rw");
    }

    void prepare_segments( long file_size, int segments_num ) throws InterruptedException, IOException
    {
        final short num_of_segment_properties = 3;
        long segment_size = file_size / segments_num;
        long start = 0;
        long end;

//        long[] segments_properties = new long[num_of_segment_properties];
        ArrayList segments_properties = new ArrayList(num_of_segment_properties);

        for (int iii = 1; iii <= segments_num; iii++)
        {
            if( iii == segments_num )
                end = this.stream_size;
            else
                end = start + segment_size;

            System.out.println( start + "    " + end );

            segments_properties.add( download_request(start, end) );
            segments_properties.add( start );
            segments_properties.add( end );

            segments.add( segments_properties );

//            threading_control("start");
            start = end + 1;
        }
    }

    //TODO: Type of Link - http https ftp ftps

    BufferedInputStream download_request( long start_byte, long end_bytes ) throws IOException
    {
        // URl Object
        URL url = new URL(this.download_link);

        // URL connection
        URLConnection url_connection = url.openConnection();
//        ((HttpURLConnection)url_connection).setInstanceFollowRedirects(true);

        // Request to start download a file from a certain byte ot a certain byte
        url_connection.setRequestProperty("Range", "bytes=" + start_byte + "-" + end_bytes);

        url_connection.connect();

//        System.out.println( start_byte + "    " + end_bytes );

        // TODO: get response code - must be http or https
        // TODO: try HttpClient from apache

        try
        {
            // Open a stream connection to receive data in buffers
            InputStream in = url_connection.getInputStream();

//            long file_length = url_connection.getContentLengthLong();
//            System.out.println("File Length: " + Long.toString(file_length) + " Bytes");

            return new BufferedInputStream(in);
        }

        catch ( IOException e )
        {
            System.err.println("Link is wrong!");
//            e.printStackTrace();
            return null;
        }

    }
}