#This version Is a game changer!

All the problems I have been having with unrealiable comminications have been solved with the version.
The way I did this is by implementing my most complicatied message frame yet, I call it Butter-Scotch.
And this is what it looks like:

Butter Scotch message frame:

 each capital letter is a nibble, lowercase is a bit, * is multiplication
 
 by Bytes:                            AA AA Bxxxc DD 1-250*EE FF FF
 
                AA                AA     B             xxxc              DD           1-250* EE        FF FF
                
   message length^ | message length^ | ID^ | Received bit?^ | message type^ | message contents^ | check sum^
   
 Overview:
 
 I was inspired after learnign about the CAN bus protocol and I implmented a few key parts of it. When the device recives data to its imput buffer it will wait until
 at least 2 bytes have been recived. then the message length can be decoded by reading the first 2 bytes as binary numbers and makign sure they are the same (if their different we,
 have corruption and we can decide what to do with that). Now that we know the message length we can take in the right amount of bytes and check that the data is good using the
 checksum at the end of the data. The ID is usful if multiple devices are hooked up to a single bluetooth modual on the arduino side, this allows the arduino to determine if the
 message is for it. The ID byte also contains a 3 dont care bits and a acknowledge bit that so the device can determine if the previous message was recived and decide what to do.
 The message type byte is a a binary number that is a code in a shared array or hashmap that contains the intructions on what to do with the message content bytes.
 finaly the checksum ensures good data.
 
 Message contents:
 
 The message contents are a series of binary numbers that can be interperted in different ways based on the message type's value.
 For example: if the message type is 1 then interperate the binary contents as 3 integers 1 byte long each. If you can map whaterver data you want to send to a single byte then
 the frame becomes very convenient. I have functions and classes that do this but what if you want to send somthing more complicated than a short short int? Well i have functions
 and classes for that too. A float for example whould be be sent with a different message type to indicate how it should be read. Floats are not stored in the message traditionally
 with a sign bit, an exponent and a fraction. Instead I chose to seperate the float into 2 parts: the first is a variable length integer that represetns the numbers before the decmial
 and a 2 byte integer represetneing the numbers after the decimal. Once received I just splice them back together. I choose to do it this way becuase it was easier for me since I already had
 all the functions to do this. The pit falls of doing it this way are you would need a whole byte to show its a negative number (but ive never need to send a negative float yet) and its not
 space effienect (which I dont care about since the message can be 250 bytes long). finally strings are just sent as ascii with an appropriate type. There are better ways to do this
 but this is my way, and it works!
