import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

class DownloadManagerCore implements Runnable
{
    // Read stream
//    private BufferedInputStream read_data = null;
    // Write stream
    private RandomAccessFile write_data = null;

    long segment_size = 0;
    int number_of_segments = 1;
    final int max_buffer_byte = 2048;
    final int max_available_threads = 1;
    // Number of started threads
    private short threads_started = 0;

    ArrayList<ArrayList> segments;

    Thread thread = null;
    Semaphore semaphore;

    DownloadManagerCore(ArrayList<ArrayList> segments, RandomAccessFile write_data,
                        long segment_size, int number_of_segments) throws InterruptedException
    {
        this.write_data = write_data;
        this.segment_size = segment_size;
        this.number_of_segments = number_of_segments;

        this.segments = segments;
        this.semaphore = new Semaphore(max_available_threads);

        // Write data as a buffer of data
//        RandomAccessFile writeData = new RandomAccessFile(file, "w");

//        calculate_segments(file_size, segments_num);
    }

    void threading_control(String operations) throws InterruptedException
    {
        if( operations.equals( "start" ) )
        {
            for (int iii = 0; iii < number_of_segments; iii++)
            {
                this.thread = new Thread(this);
                this.thread.start();
            }
        }
    }

    @Override
    public void run()
    {
        System.out.println("Thread id=" + Long.toString(Thread.currentThread().getId()) + " Started!");

        try {
//            semaphore._wait(Thread.currentThread());
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            download();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void download() throws IOException
    {
        InputStream read_data = (BufferedInputStream)this.segments.get(threads_started).get(0);
        long start = (Long)this.segments.get(threads_started).get(1);
        long end = (Long)this.segments.get(threads_started).get(2);

//        semaphore._signal();
        this.threads_started++;
        semaphore.release();

        write_data.seek(start);

        byte buffer[] = new byte[max_buffer_byte];
        int offset = 0;
        int data;
        long data_written = 0;

        // Read and Write
        while( ( data = read_data.read(buffer) ) > 0 && data_written <= this.segment_size )
        {
            data_written += data;
            write_data.write(buffer, offset, data);
            System.out.println(data_written);
        }

        // Close the connection
        read_data.close();
        // Close the stream writing
        write_data.close();

        System.out.println("Segment id=" + Thread.currentThread().getId() + "downloading finished!");
    }
}