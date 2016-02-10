
# Overview

This is a P2P wrapper for the axolotl library by TextSecure which adds extra, rich security functionality
The protocol overview is available [here](https://github.com/trevp/axolotl/wiki),
and the details of the wire format are available [here](https://github.com/WhisperSystems/TextSecure/wiki/ProtocolV2).

## Demo

This library is accompanied by a demonstration suit supported by java swing for GUI and openfire XMPP server
for message transport.

## Running the demo

### Build the project
  - Install java (sudo apt-get install default-jre)
  - Install [gradle 2.5](https://services.gradle.org/distributions/gradle-2.5-all.zip)
  - From the project directory, build the project and create an executable JAR (gradle intallDist)
  - Dependencies are also inside the lib folder, just in case

### Get and install the openfire XMPP server
 - wget -O openfire.deb http://www.igniterealtime.org/downloadServlet?filename=openfire/openfire_4.0.1_all.deb
 - sudo dpkg --install openfire.deb

### Create some artificial users
 - Visit local server management [interface](http://localhost:9090)
 - Follow the wizard instructions for openfire server setup
 - Login as (admin, password_you_chose), username is "admin", not your email!
 - More detailed instructions [here](https://www.digitalocean.com/community/tutorials/how-to-install-openfire-xmpp-server-on-a-debian-or-ubuntu-vps)
 - Add at least 3 users (enough for group chat)
    - "Users/Groups" tab
    - Left side menu - "Create new user"
 - Add friends to each other's roster
    - "Users/Groups" tab
    - Click on the user's name
    - Left side menu - "Roster"
    - Add new item written in green, on the right side of the screen
        - JID=friendname@servername
        - Nickname=friendname
        - Left side menu - "Roster"
        - Hit "edit" on each roster menu item
        - Change the "Subscription" field to "both"
 

### Run the program 
 - Three instances (one to simulate each user) with "ClientGUI" as the main class 
    - (for i in 1 2 3; do java -jar java/build/install/java/lib/secure-chat-p2p-java.jar & done) 

### Login
 - Type the server address (by default should be the computer name)
 - Keep the default port number (5222)
 - Type the user name and password (different for each instance)
 - Type the desired path for a key store (without file name). If the key store already exists it will be used, otherwise a new one will be generated.
 - Hit Login
  







