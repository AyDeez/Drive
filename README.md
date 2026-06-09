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

---

## Core Classes Overview:
### Protocol
The `Protocol` class defines all command and response identifiers used by both client and server.

It includes:

- Client → Server commands:
    - LOGIN, LOGOUT, UPLOAD, DOWNLOAD, LIST, DELETE, MKDIR
- Server → Client responses:
    - RES_OK, RES_ERROR, RES_DATA, RES_AUTH_OK, RES_AUTH_FAIL
- System limits:
    - Maximum payload size: 10 MB
    - I/O buffer size: 8 KB

This class is immutable and only contains constants.

---

### MessageWriter
The `MessageWriter` class is responsible for sending messages over an `OutputStream`.

It provides a high-level API for:

- Sending commands without payload
- Sending binary payloads
- Sending UTF-8 string payloads
- Sending shortcut responses (`ok()` and `error()`)

Each message is serialized as:

CMD (1 byte) + LENGTH (4 bytes) + PAYLOAD

The writer ensures messages are flushed immediately after being sent.

---

### MessageReader
The `MessageReader` class handles incoming messages from an `InputStream`.

It performs the following steps:

1. Reads 1 byte → command identifier
2. Reads 4 bytes → payload length
3. Validates payload size against protocol limits
4. Reads payload bytes safely

It returns a `Message` object representing the decoded packet.

---

### Message
The `Message` class represents a received packet.

It contains:

- `cmd` → command or response type
- `payload` → raw byte data

Utility methods:

- `payloadAsString()` → converts payload to UTF-8 string
- `payloadAsParts()` → splits payload using `\0`
- `hasPayload()` → checks if payload is not empty

---

## Communication Flow:
1. Client creates a message using `MessageWriter`
2. Message is sent over TCP socket
3. Server reads message using `MessageReader`
4. Server interprets command using `Protocol`
5. Server processes request
6. Server responds using `MessageWriter`
