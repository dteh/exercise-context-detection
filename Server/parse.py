import sys
import os
import numpy as np
from sklearn.externals import joblib
from sklearn.naive_bayes import GaussianNB
from math import sqrt


#usage: python parse.py <file/all> <dtype>
# <file/all> either file name of file to parse, or 'all' to parse all files in dir
# <dtype> either training or predict

def parse(filename,dtype):
	dataf = open(filename,'rb')
	data = dataf.readlines()

	activitiesf = open('activities.txt','rb')
	activities = activitiesf.readlines()
	numActivities = len(activities)

	trainingfile = open('training.csv','a')

	# parse activity name, find if activity is a known activity
	if dtype == "training":
		activity = data[0][18:-3]
		found = False
		for item in activities:
			if item.find(activity) != -1:
				found = True
				activityID = item.split(',')[0]
		if not found:
			addActivitiesf = open('activities.txt','a')
			addActivitiesf.write(str(numActivities+1)+","+activity+"\n")
			activityID = str(numActivities+1)

	# parse data
	accel = False
	gyro = False
	gyroData = []
	accelData = []

	for lines in data:
		#check if line is parseable
		if lines[0] != "[":
			pass
		#check if parsing accel data	
		elif lines.find('[ACCELERATION]') != -1:
			accel = True
			gyro = False
		#check if parsing gyro data
		elif lines.find('[GYRO]') != -1:
			accel = False
			gyro = True
		elif not gyro and accel:
			#do something with accel data
			split = lines.split()
			accelData.append( (split[1],split[2]) )
		elif not accel and gyro:
			#do something with gyro data 	
			split = lines.split()
			gyroData.append( (split[1],split[2]) )

	# do some calculations and then write to a file
	# FMT = '%H:%M:%S.%f'
	# timeAstart = None
	# timeAstop = None
	# timeGstart = None
	# timeGstop = None
	maxA = None
	minA = 100000
	totA = 0
	maxG = None
	minG = 100000
	totG = 0
	avgG = None
	avgA = None

	#total sum of accelerations
	#probably unnecessary
	gTotals = [0,0,0]
	aTotals = [0,0,0]

	#get start and end times
	# timeAstart = accelData[0][0][:-1]
	# timeAstop = accelData[len(accelData)-1][0][:-1]
	# timeGstart = gyroData[0][0][:-1]
	# timeGstop = gyroData[len(gyroData)-1][0][:-1]

	#calc out max/min values
	for i in accelData:
		j = i[1].split(',')
		k = float(j[0])**2 + float(j[1])**2 + float(j[2])**2


		aTotals[0] += float(j[0])
		aTotals[1] += float(j[1])
		aTotals[2] += float(j[2])

		vector = sqrt(k)
		if vector > maxA:
			maxA = vector
		if vector < minA:
			minA = vector
		totA += vector

	for i in gyroData:
		j = i[1].split(',')
		k = float(j[0])**2 + float(j[1])**2 + float(j[2])**2

		gTotals[0] += float(j[0])
		gTotals[1] += float(j[1])
		gTotals[2] += float(j[2])

		vector = sqrt(k)
		if vector > maxG:
			maxG = vector
		if vector < minG:
			minG = vector
		totG += vector

	#calc averages
	avgA = float(totA)/(len(accelData))
	avgG = float(totG)/(len(gyroData))

	#print "\nValues for:[\"" + sys.argv[1] + "\"]"
	# print "Activity name: " + activity
	# print "[Acceleration]"
	# print "Min:" +str(minA)+ " Max:" + str(maxA)+ " Avg accel:" + str(avgA)
	# print "[Gyro]"
	# print "Min:" +str(minG)+ " Max:" + str(maxG)+ " Avg rad:" + str(avgG) + "\n"

	# predval = str(minA)+","+str(maxA)+","+str(avgA)+","+str(minG)+","+str(maxG)+","+str(avgG)
	# print "test: ["+predval+"]"

	#probably unnecessary
	#print "ATotal = " + str(aTotals) + "\nGTotal = " + str(gTotals) + "\n"

	# either add to training set, or predict
	if dtype == "training":
		trainingfile.write("\n"+str(activityID)+","+str(minA)+","+str(maxA)+","+str(avgA)+
			","+str(minG)+","+str(maxG)+","+str(avgG))
		trainingfile.close()

	elif dtype == "predict":
		clf = joblib.load("model.pkl")
		vec = np.array([minA,maxA,avgA,minG,maxG,avgG])
		x = int(clf.predict(vec.reshape(1,-1))[0])
		for lines in activities:
			if lines.split(',')[0] == str(x):
				return lines.split(',')[1]


	dataf.close()
	activitiesf.close()


if __name__ == "__main__":
	if len(sys.argv) == 1:
		print "Need file argument (all/file)"
		quit()
	elif sys.argv[1] == "all":
		for file in os.listdir("./data/"):
			print file
			if file != ".DS_Store":
				parse("./data/"+file, "training")

		csv = np.genfromtxt('training.csv',delimiter=",")
		X = csv[1:,1:]
		Y = csv[1:,0]
		clf = GaussianNB()
		clf.fit(X,Y)
		joblib.dump(clf, "model.pkl")

	else:
		print parse(sys.argv[1], "predict")




