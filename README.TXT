## Exercise Context Detection
# Intro
This was created for CS439 at UIUC.
The main purpose of this was to create an application that could detect context. For this purpose, we built an application to detect exercise movements. The user must attach an android phone to their upper arm via an exercise armband. Then, with the app open, press start. The application will then send information from the accelerometer and gyroscope constantly to an external server which will classify the information as belonging to one of several exercise classes:
- Pushups
- Situps
- Jumping Jacks

Main server and information parser was written in python3 and trained using the naive bayes classifier. Only around 15 data samples for each exercise were used.



——
To start -  run server.py
Default port opened is 80, change in source if required
The machine learner has already been trained.

If you wish to retrain it, place any data files in the ./data/ directory, delete and 
create blank files of activities.txt and training.csv. Then run the command

$ python parse.py all

The resulting trained machine learner is saved as a pickled numpy array.



ANDROID SOURCE:
android source code is located in WirelessProject 2.zip The only external library used 
is loopj, included in the zip file.
