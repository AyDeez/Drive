package protocol;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MessageWriter {

    private final DataOutputStream out;

    public MessageWriter(OutputStream os) {
        this.out = new DataOutputStream(new BufferedOutputStream(os));
    }

    // Message with payload (eg. LOGIN)
    public void write(byte cmd, byte[] payload) throws IOException {
        out.writeByte(cmd);
        out.writeInt(payload.length);
        if (payload.length > 0) out.write(payload);
        out.flush();
    }

    // Message without payload (eg. LOGOUT)
    public void write(byte cmd) throws IOException {
        write(cmd, new byte[0]);
    }

    // Message with text payload (UTF-8)
    public void write(byte cmd, String text) throws IOException {
        write(cmd, text.getBytes(StandardCharsets.UTF_8));
    }

    // Shortcut for OK response
    public void ok(String msg) throws IOException {
        write(Protocol.RES_OK, msg);
    }

    // Shortcut for ERROR response
    public void error(String msg) throws IOException {
        write(Protocol.RES_ERROR, msg);
    }

    // Returns DataOutputStream object
    public DataOutputStream raw() { return out; }

}
