package protocol;

import java.nio.charset.StandardCharsets;

public class Message {

    private final byte cmd;
    private final byte[] payload;

    public Message(byte cmd, byte[] payload) {
        this.cmd = cmd;
        this.payload = payload;
    }

    public byte getCmd() { return cmd; }
    public byte[] getPayload() { return payload; }

    // Payload as UTF-8 String
    public String payloadAsString() {
        return new String(payload, StandardCharsets.UTF_8);
    }

    // Payload as String split on \0 (null byte separator)
    public String[] payloadAsParts() {
        return payloadAsString().split("\0");
    }

    public boolean hasPayload() { return payload.length > 0; }

}
