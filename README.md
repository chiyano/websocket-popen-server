websocket-popen-server
======================

websocket-popen-server can provide your programs or scripts  as a web service 
in easy way. When you accessed a URI, corresponding program will be started.

Input stream comes into the standard input of the started program via Web-
Socket like a UNIX pipe. STDOUT and STDERR will be streamed out too.

If your web browser supports WebSocket, it is possible to call your programs
or scripts using JavaScript.

The goal of this project is to achieve a remote execution of our local 
programs or scripts via URI from any web applications such as a wiki, 
Redmine, Twitter and Facebook.

## Requirement

Java 1.6 later

## Getting Started

Below is tested with OS X Mountain Lion and probably works in Linux too. 

Download jar file here

https://github.com/chiyano/websocket-popen-server/raw/master/bin/websocket-popen-server-0.0.4-SNAPSHOT-jar-with-dependencies.jar

Then run it.

    java -jar websocket-popen-server-0.0.4-SNAPSHOT-jar-with-dependencies.jar

Create a script and save as ~/bin/hello. (Don't forget chmod 755)

    #!/bin/bash
    cat | sed -l 's/^/hello /'

NOTE: Above script is for OSX. Use "sed -u" for Linux.

In the JavaScript Console of your Google Chrome, try this.

    ws = new WebSocket("ws://localhost:9999/ws/hello")
    ws.onmessage = function (e) { console.log(e.data) }
    ws.send("test\n")
    ws.send("bye\n")
    ws.send("\0")

## License

Copyright (c) 2013 Chiyano

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
