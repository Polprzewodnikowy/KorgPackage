import com.polprzewodnikowy.korgpkg.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by korgeaux on 08.05.2016.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length >= 1) {
            String path;
            if(args.length == 1)
                path = args[0].substring(0, args[0].lastIndexOf('.'));
            else
                path = args[1];
            PackageReader packageReader = new PackageReader(args[0]);
            List<Chunk> chunks = packageReader.load();
            for (Chunk chunk: chunks) {
                try {
                    chunk.export(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(chunk);
            }
        } else {
            System.out.println("\r\nUsage:\r\n\tKorgPackage package [export dir]");
        }
    }

}
