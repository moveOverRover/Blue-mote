# V3

This version adds 2 analog inputs because of the digital joystick.

This implementation of the joystick was not perfect.
I had trouble offsetting the sprite of the joystick from the position of the finger because the coordinates of the sprite start at the top left corner of the sprite not the middle.
Some strange looking offsets had to made because of this and it interfered with the way I would map the position of the joystick to its actual value.

Other than that, pretty similar to V2.
I did clean up the UI and switched it to a relative layout. I Also implement some new features like toasts and an option to set the origin of the joystick anywhere on the screen.
Because of these changes this version of the app should* fit on any phone screen unlike the last one.
We are still using the miniJeep3 message frame as well.

<p float="left">
  <img src="https://user-images.githubusercontent.com/77077715/131576468-0c97a087-06a2-420c-bc0c-42121d4ef9e2.jpg" alt="Screenshot_20210810-084741_BluetoothShenanegans2" width="250" height="500">
  <img src="https://user-images.githubusercontent.com/77077715/131576480-4bcd2dec-e274-40a8-bfd9-8f6e507c0b82.jpg" alt="Screenshot_20210810-084846_BluetoothShenanegans3" width="250" height="500">
</p>


