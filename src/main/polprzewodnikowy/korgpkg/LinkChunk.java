package polprzewodnikowy.korgpkg;

/**
 * Created by korgeaux on 19.05.2016.
 */
public class LinkChunk extends Chunk {

    public LinkChunk() {
        id = LINK;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[" + id + " LinkChunk]: ");
        str.append("???");
        return str.toString();
    }

}
