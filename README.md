# SensorAppforAccessibleCampus
This app collects the data from Various sensors of Android mobile.
GPS, Accelerometer, Orientation, Magnetometer, Audio recorder sensors are used to collect data.

The data is collected by a Timer service whose interval is 1second and can be changed
according to requirements.

The data is sent to a python server connected to the network of the mobile.
We ran the python server on the laptop, which is connected from the mobile 
via Wifi hotspot of the mobile.
When we start the app, we need to give the IP address and port number of the server.

On pressing Start Recording button, the app start sending the sensors data to the server
and Save the Audio file in the Home folder of the Mobile.

On pressing stop button, it stops recording Audio and also stops send data to the server.
Restart app to record again.
