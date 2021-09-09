# Blue-mote
This contains my the stl files, code, and media for my bluetooth remote project. But has more recently been taken over by my adventures of creating an android application to replace the remote.
  
I started this project well over a year ago and it taught me alot, certainly more than I want to write in this read me. Visting the code of each project version will provide a more deeper dive into the challanges and victories I found.

Alot of this repo is my first creating a controller, and then spending a year trying to figure out why half the time the messages I recive are not the ones I sent even though bluetooth has a pretty bullet proof protocol.

I dig into parity, checksums, message frames, and end up making something like my own protocol even though I have never run it on hardware that isnt already running its own error correction (although I might buy some cheep transmitters/recivers that have nothing else going on and give it a shot later).

In the end I belive the problem was my placing long wires in noisy enviroments between the simpler UART interfaces that was giving me the probelem (1 parity bit might not be enough with a 2kw motor next to it). It was either that or a missunderstanding of how arduino's/HC-05 input buffers work becuase sometimes they would give me unresonable values.
<p float="left">
  <img src="https://user-images.githubusercontent.com/77077715/131572858-0042a2dc-f131-4f49-9988-6e2b3c36643f.jpg" alt="Screenshot_20210810-084741_BluetoothShenanegans2" width="400" height="400">
  <img src="https://user-images.githubusercontent.com/77077715/131572917-9a2d0284-0e52-46be-9a64-12a8adf8330e.jpg" alt="Screenshot_20210810-084846_BluetoothShenanegans3" width="400" height="400">
</p>

