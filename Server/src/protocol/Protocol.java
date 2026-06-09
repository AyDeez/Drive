package protocol;

public final class Protocol {

    // ── Client -> Server command ──────────────────────────────────────────────
    public static final byte CMD_LOGIN    = 0x01; // payload: user\password
    public static final byte CMD_LOGOUT   = 0x02; // payload: -
    public static final byte CMD_UPLOAD   = 0x03; // payload: filename\0filesize(8B)\0data
    public static final byte CMD_DOWNLOAD = 0x04; // payload: filename
    public static final byte CMD_LIST     = 0x05; // payload: path (opzionale)
    public static final byte CMD_DELETE   = 0x06; // payload: filename
    public static final byte CMD_MKDIR    = 0x07; // payload: dirname

    // ── Server -> Client response ─────────────────────────────────────────────
    public static final byte RES_OK       = 0x10; // payload: messaggio opzionale
    public static final byte RES_ERROR    = 0x11; // payload: messaggio errore
    public static final byte RES_DATA     = 0x12; // payload: dati richiesti
    public static final byte RES_AUTH_OK  = 0x13; // payload: -
    public static final byte RES_AUTH_FAIL= 0x14; // payload: motivo

    // ── Limits ────────────────────────────────────────────────────────────────
    public static final int  MAX_PAYLOAD  = 10 * 1024 * 1024; // 10 MB (per messaggi non-file)
    public static final int  BUFFER_SIZE  = 8 * 1024;         // 8 KB buffer I/O

}
