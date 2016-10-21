import pprint
import time
import BaseHTTPServer
import parse
import urlparse
import sqlite3
import os

#http://canvasjs.com/docs/charts/chart-types/html5-stacked-bar-100-chart/

HOST_NAME = '0.0.0.0' # !!!REMEMBER TO CHANGE THIS!!!
PORT_NUMBER = 80 # Maybe set this to 9000.
resp = {}
prevresp = {}
displayresp = {}
firstI = 1


head = """<title>Think of a witty title</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css" integrity="sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r" crossorigin="anonymous">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js" integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS" crossorigin="anonymous"></script>
        <style>h1 {text-align:center}</style>
"""


conn = sqlite3.connect('feed.db')
c = conn.cursor()
#SQLITE STUFF
class feed():
    def get(self):
        topfragment = """
<html>
    <head>
        """

        titlefragment = """
    </head>
    <body>
        <center><h1>Latest tracked workouts..</h1></center>
"""
        c.execute("SELECT MIN(time),activity FROM feed GROUP BY activity ORDER BY time ASC;")
        middlefrag = ""
        for i in c.fetchall():
            middlefrag = middlefrag + str(i) + "<br>"

        tailfragment = """
    </body>
</html>
        """
        return topfragment + head + titlefragment + middlefrag+ tailfragment

htmlserve = feed()



class MyHandler(BaseHTTPServer.BaseHTTPRequestHandler):


    def do_HEAD(s):
        s.send_response(200)
        s.send_header("Content-type", "text/html")
        s.end_headers()
    def do_GET(s):
        """Respond to a GET request."""
        reqUser = s.path.find("/api/user/")
        reqFeed = s.path.find("/feed.html")
        if reqUser != -1 :
            if s.path[10:] in resp.keys():
                s.send_response(200)
                s.send_header("Content-type", "text/xml")
                s.end_headers()
                s.wfile.write(displayresp[s.path[10:]])
            else:
                s.send_response(404)
                s.send_header("Content-type", "text/xml")
                s.end_headers()
                s.wfile.write("ERROR")
                s.wfile.write(resp.keys())

        elif reqFeed != -1:
            s.send_response(200)
            s.send_header("Content-type", "text/html")
            s.end_headers()
            s.wfile.write(htmlserve.get())
        else:
            s.send_response(404)
            s.send_header("Content-type", "text/html")
            s.end_headers()
            s.wfile.write("<html><head><title>THERES NOTHING HERE!!!!</title></head>")
            s.wfile.write("<body>u goofy mofo theres nothing here</body></html>")

    def do_POST(s):
        global firstI
        global resp
        global prevresp
        global displayresp

        """respond to POST"""
        s.send_response(200)
        s.send_header("Content-type", "text/html")
        s.end_headers()
        s.wfile.write("Recieved")
        length = int(s.headers.getheader('content-length'))
        field_data = s.rfile.read(length)
        fields = urlparse.parse_qs(field_data)
        try:
            username = fields.get(' charset')[1].split('\n')[2].strip()
            sessionID =  fields.get(' charset')[2].split('\n')[2].strip()
        except:
            print fields.get(' charset')[0]
            print fields.get(' charset')[1]
        

        # save file to disk and parse
        name = str(time.time())
        f = open(name,'w')
        fname = fields.get(' filename')[0][fields.get(' filename')[0].index('[TYPE'):]
        f.write(fname)
        f.close() 
        answer = parse.parse(f.name,'predict')
        print answer

        if(firstI == 1):
            resp[username] = answer
            displayresp[username] = resp[username]
            c.execute("INSERT INTO feed VALUES(\""+sessionID+"\","+name+",\""+username+"\",\""+answer[:-1]+"\")")
            firstI = 0

        else:
            prevresp[username] = resp[username]
            resp[username] = answer
            if prevresp[username] == resp[username]:
                displayresp[username] = resp[username]
                c.execute("INSERT INTO feed VALUES(\""+sessionID+"\","+name+",\""+username+"\",\""+answer[:-1]+"\")")

                # ithink this is it!!
                #do feed stuff
                #sqlite requests... etc

        os.system('rm '+name)


       

if __name__ == '__main__':
    server_class = BaseHTTPServer.HTTPServer
    httpd = server_class((HOST_NAME, PORT_NUMBER), MyHandler)
    print time.asctime(), "Server Starts - %s:%s" % (HOST_NAME, PORT_NUMBER)
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    conn.commit()
    conn.close()
    httpd.server_close()
    print time.asctime(), "Server Stops - %s:%s" % (HOST_NAME, PORT_NUMBER)
