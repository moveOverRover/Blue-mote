# Blue-mote
This contains the stl files, code, and media for my Bluetooth remote project. But has more recently been taken over by my adventures of creating an android application to replace the remote (please do have a look at that, its pretty cool).
  
I started this project well over a year ago and it taught me a lot, certainly more than I want to write in this read me. Visiting the code of each project version will provide a deeper dive into the challenges and victories I found.

A lot of this repo is my first creating a controller, and then spending a year trying to figure out why half the time the messages I receive are not the ones I sent even though Bluetooth has a pretty bullet proof protocol.

I dig into parity, checksums, message frames, and end up making something like my own protocol even though I have never run it on hardware that isn’t already running its own error correction (although I might buy some cheep transmitters/receivers that have nothing else going on and give it a shot later).

In the end I believe the problem was my placing long wires in noisy environments between the simpler UART interfaces that was giving me the problem (1 parity bit might not be enough with a 2kw motor next to it). It was either that or a misunderstanding of how Arduino’s/HC-05 input buffers work because sometimes they would give me unreasonable values.
<p float="left">
  <img src="https://user-images.githubusercontent.com/77077715/131572858-0042a2dc-f131-4f49-9988-6e2b3c36643f.jpg" alt="Screenshot_20210810-084741_BluetoothShenanegans2" width="350" height="350">
  <img src="https://user-images.githubusercontent.com/77077715/131572917-9a2d0284-0e52-46be-9a64-12a8adf8330e.jpg" alt="Screenshot_20210810-084846_BluetoothShenanegans3" width="350" height="350">
</p>

