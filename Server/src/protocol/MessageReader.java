package protocol;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MessageReader {

    private final DataInputStream in;

    public MessageReader(InputStream is) {
        this.in = new DataInputStream(new BufferedInputStream(is));
    }

    // Reads
    public Message read() throws IOException {
        byte cmd = in.readByte();
        int length = in.readInt();

        if (length<0 || length>Protocol.MAX_PAYLOAD) {
            throw new IOException("Invalid Payload: " + length + " bytes");
        }
        byte[] payload = new byte[length];
        if (length > 0) in.readFully(payload);

        return new Message(cmd, payload);
    }

    // Returns DataInputStream object
    public DataInputStream raw() { return in; }

}
