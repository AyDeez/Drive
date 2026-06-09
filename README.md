# Client - Server Drive System
A simple Java client-server application for managing files across multiple clients.

The system simulates a lightweight cloud drive where clients can authenticate, upload, download, and manage files on a remote server using a custom binary protocol over TCP sockets.

---

## 1) Phase 1 - Communication Protocol Design:
The 'Protocol' class defines how messages sent over a Socket are structured.
Each message follows this format:

CMD (1 byte) -> identifies the type of command or response

LENGTH (4 bytes) -> size of the payload in bytes (0 if absent)

PAYLOAD (n bytes) -> optional data depending on the command

### Payload Structure:

| Command  | Payload | Expected Response |
|----------|---------|------------------|
| LOGIN    | `username\0password` | `RES_AUTH_OK` or `RES_AUTH_FAIL` |
| LOGOUT   | — | `RES_OK` |
| LIST     | path (string, "" = root) | `RES_DATA` with list `name\0type\0size\n...` |
| UPLOAD   | `filename\0filesize` then raw file stream | `RES_OK` or `RES_ERROR` |
| DOWNLOAD | filename | `RES_DATA` with filesize then raw stream, or `RES_ERROR` |
| DELETE   | filename | `RES_OK` or `RES_ERROR` |
| MKDIR    | dirname | `RES_OK` or `RES_ERROR` |

---

### File transfer note (UPLOAD / DOWNLOAD):
File transfers are not fully embedded inside the message payload.

Instead, a **two-step mechanism** is used:

1. A control message is sent containing metadata (e.g., filename, filesize)
2. The actual file bytes are streamed directly through the socket

This design avoids loading entire files into memory and improves scalability for large transfers.

---

### Design notes:
- Binary protocol for efficiency and low overhead
- Strict separation between:
    - command header (CMD + LENGTH)
    - payload data
- String fields use `\0` as internal delimiter
- File transfers are handled outside standard message payloads for performance
