# Blue-mote
  This contains my the stl files, code, and media for my bluetooth remote project. But has more recentyl been taken over by my adventures of creatign an android application to replace the remote.
  
I started this project well over a year ago and it taught me alot, certainly more than I want to write in this read me. Visting the code of each project version will provide a more deeper dive into the challanges and victories I found.

Alot of this repo is my first creating a controller, and then spending a year trying to figure out why half the time the messages I recive are not the ones I sent even though bluetooth has a pretty bullet proof protocol.

I dig into parity, checksums, message frames, and end up making something like my own protocol even though I have never run it on hardware that isnt already running its own error correction (although I might buy some cheep transmitters/recivers that have nothing else going on and give it a shot later).

In the end I belive the problem was my placing long wires in noisy enviroments between the simpler UART interfaces that was giving me the probelem (1 parity bit might not be enough with a 2kw motor next to it). It was either that or a missunderstanding of how arduino's/HC-05 input buffers work becuase sometimes thye would give me unresonable values.
