## SensorAppforAccessibleCampus
App part

This app collects the data from Various sensors of Android mobile.
GPS, Accelerometer, Orientation, Magnetometer, Audio recorder sensors are used to collect data.

The data is collected by a Timer service whose interval is 1second and can be changed
according to requirements.

The data is sent to a python server connected to the network of the mobile.
We ran the python server on the laptop, which is connected from the mobile 
via Wifi hotspot of the mobile.

# Working of the Android Application
1. On starting the app, we need to give the IP address and port number of the server.

2. On pressing Start Recording button, the app start sending the sensors data to the server
and Save the Audio file in the Home folder of the Mobile.

3. On pressing stop button, it stops recording Audio and also stops send data to the server.
4. Restart app to record again.


## README (for software):
The software reads the tagged data, builds a kdtree for the tuples of longitude and latitude
values and finally speaks out the corresponding landmarks. All of this code is inside kdtree
folder and has been written in python and the tagged data has been prepared manually. All of
this is done by combinedscript.py. The structure of the code is as follows:
# 1. Reading the data:
The data is written in .txt files as:
Img timestamp, longitude, latitude, <tagged landmarks>
The data is read and all the tuples of longitudes and latitudes are stored in a kdtree. The
kdtree implementation has been borrowed from the following project:
https://github.com/stefankoegl/kdtree
  
  A dictionary of landmarks is maintained, which is indexed by 0 if the user is moving
towards the main gate and 1 if the user is moving towards the boys hostel gate. This is
determined at the runtime by the value of orientation received by the application. The
application will send 0 and 1 accordingly. For each GPS coordinate, an array of
landmarks are stored corresponding to both 0 and 1. The software then builds a kdtree
from the tagged data.
  
# 2. Establishing connection:
This is done by project.py. The code tries to establish connection with the application by
waiting to receive some packet at the common port. As soon as it receives the
handshake data, it begins a loop that receives the real-time GPS location of the user and
the corresponding value of orientation denoted by 0 and 1.

# 3. Finding out the nearest neighbor:
The code then performs a 2-NN query with the real-time GPS location on the kdtree. It
then receives 2 neighbours nearest to the current location. It stores the previous
location of the user and checks by the distance between the two neighbor points and
the current and the previous GPS location. Comparison is done in order to determine
which of the 2 NN to choose next.

# 4. Speaking out landmarks:
After it has determined which coordinate is the nearest, it picks up the landmarks
corresponding to the orientation corresponding to the orientation value from the
landmarks dictionary. It finally speaks out all the landmarks using pyttsx3 library, which
is an offline tts for python. The implementation and installation of pyttsx3 is borrowed
from the following link:
https://github.com/nateshmbhat/pyttsx3

### Application file : app-debug.apk
Apk file to install app without building the project.
Install from unsecured sources option from Security and preferences has to be turned on to install apk. 

