
# project Overview

This is a software library for secure group conversation over an unsafe media.
Our motivation for the project was to give people the ability to communicate as a group,
by a secure chat via easy to use/modify open library which any user can remodel for his needs.

## Core – ENGINEERING
Open source Axolotl library

    - Created as a part of TextSecure
    - Assumes a central server architecture	
    - Used “As-Is”
Peer-To-Peer wrapper library

    -No need for central server!
    -Preserves all security features
    -Provides extra, rich functionality
Global compatibility

    -Generates / consumes Base64 encoded payloads
    -Doesn’t assume specific transport
    -Built for easy integration with existing chat technologies

## Core - features
Performing a secure conversation with a peer

    - Secrecy (Forward / Future)
    - Authenticity
    - Deniability
Peer authentication

    - Fingerprint
    - Usability
    - Secure Storage
Group conversations

    - Based on multiple P2P sessions
    - Conversation history
    - Conversation consistency

## Message exchange layer
A wrapper for the smack library

    - Server communication
    - Peer / group communication
Message exchange
    
    - Provides simultaneous send / receive functionalities
    - Defines message types for key and data exchange
Server data access
    
    - Collects account data from server to clients
    - Collects user status

## Demonstration layer
Single client GUI

    - Uses the java swing library
Provides evaluation capabilities

    - Login with server
    - Create / access existing key store
    - Export / consume Out-Of-Band authentication data
    - Start secure sessions with peers and / or groups
    - Authentication status
    - Intentional session tamper – hide a message from a peer in a group
    - Single message /range retransmission


### Design Considerations:
Our library was designed work on top of any type of media and can be easily integrated into any
existing chat / IM application. Key exchange messages, metadata and user payload are all
serialized and encoded in Base64 and can be transported as simple character strings.
The user does not need to worry about securing any of the messages as the library secures
anything of value. It is the user’s responsibility to make sure the library generated key exchange
message is exchanged between every pair of peers before the actual conversation begins.




