
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
  - Install gradle 2.5 (https://services.gradle.org/distributions/gradle-2.5-all.zip)
  - From the project directory, build the project and create an executable JAR (gradle intallDist)
  - Dependencies are also inside the lib folder, just in case

### Get and install the openfire XMPP server
 - wget -O openfire.deb http://www.igniterealtime.org/downloadServlet?filename=openfire/openfire_3.8.2_all.deb
 - sudo dpkg --install openfire.deb

### Create some artificial users
 - Visit local server management [interface](http://localhost:9090)
 - Follow the wizard instructions for openfire server setup
 - Login as (admin, password_you_chose), username is "admin", not your email!
 - Add at least 3 users (enough for group chat)
 - More detailed instructions [here](https://www.digitalocean.com/community/tutorials/how-to-install-openfire-xmpp-server-on-a-debian-or-ubuntu-vps)

### Run the program 
 - Three instances (one to simulate each user) with "ClientGUI" as the main class (java -jar java/build/install/java/lib/secure-chat-p2p-java.jar)
 - Each instance should login as a different user by typing the user name and password in the textboxes
 and clicking "Login"

### Generate an empty key store for each user
 - After user login, type the desired path for a key store (without file name)
 - Click "Create KS".






