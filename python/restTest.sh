#!/usr/bin/python

import pycurl, json

# used tutorial: http://bottlepy.org/docs/dev/tutorial.html

# #################################################
from bottle.Bottle import Bottle, run, request
app = Bottle()
@app.route('/helloget', method='GET')
def helloGet():
    print "Server: helloGet"
    return "HelloGet"
  
@app.route('/hellopost', method='POST')
def hello():
    print "Server: helloPost, body: %s" % request.body.getvalue()
    return "HelloPost"

def HttpThread(host_, port_):
    run(app, host=host_, port=port_)

def StartHttpServerInOwnThread(host_, port_):
    print "StartedHttpServerInOwnThread ENTER"
    import thread
    t = thread.start_new_thread(HttpThread, (host_, port_))
    print "StartedHttpServerInOwnThread server started, going to sleep"
    import time
    time.sleep(1)  # delays for x seconds    
    print "StartedHttpServerInOwnThread LEAVE (after wakeup)"
    
def StopHttpServer():
  # Server waits for ctrl-c. I hope that closing stdin also works
  import sys
  sys.stdin.close()
  
# #################################################    

class Test:
  def __init__(self):
    self.contents = ''
  def body_callback(self, buf):
    self.contents = self.contents + buf

def RestGet(url, fp):
	curl = pycurl.Curl()
	curl.setopt(pycurl.URL, url)
	curl.setopt(pycurl.CONNECTTIMEOUT, 30)
	curl.setopt(pycurl.TIMEOUT, 30)
  # ref: http://justanyone.blogspot.nl/2008/07/using-libcurl-pycurl-on-for-xml-post.html
  # shows use of StringIO.StringIO was write function	
	curl.setopt(pycurl.WRITEFUNCTION, fp.body_callback)
	curl.perform()
	statusCode = curl.getinfo(curl.RESPONSE_CODE) 
	# Elapsed time for the transfer.
	print('GET completed in: %f' % curl.getinfo(curl.TOTAL_TIME))
	curl.close()
	return statusCode
    
def RestPost(url, data):
  curl = pycurl.Curl()
  curl.setopt(pycurl.URL, url)
  # c.setopt(pycurl.USERPWD, user_pwd)
  curl.setopt(pycurl.POST, 1)
  # curl.setopt(pycurl.HTTPHEADER, ["Content-type: text/xml"])
  curl.setopt(pycurl.POSTFIELDS, data)  
  # found next. didn't analyse yet.
  # curl.setopt(pycurl.NOSIGNAL, 1) # disable signals, curl will be using other means besides signals to timeout.
  curl.perform()
  statusCode = curl.getinfo(curl.RESPONSE_CODE) 
  # Elapsed time for the transfer.
  print('POST completed in: %f' % curl.getinfo(curl.TOTAL_TIME))
  curl.close()
  return statusCode
    
# ###################################################
# copied from: http://stackoverflow.com/questions/3605680/creating-a-simple-xml-file-using-python
# ref: http://effbot.org/zone/element-index.htm

def CreateXMLDocument():
  import xml.etree.cElementTree as ET
  from xml.etree.cElementTree import fromstring, tostring  

  root = ET.Element("root")

  doc = ET.SubElement(root, "doc")

  field1 = ET.SubElement(doc, "field1")
  field1.set("name", "blah")
  field1.text = "some value1"

  field2 = ET.SubElement(doc, "field2")
  field2.set("name", "asdfasd")
  field2.text = "some value2"

  tree = ET.ElementTree(root)
  #tree.write("filename.xml")
  return tostring(root)

# ###################################################

host = 'localhost'
port = 8080
StartHttpServerInOwnThread(host, port)
baseurl = "http://%s:%d" % (host, port)

t = Test()
statusCode = RestGet("%s/helloget" % baseurl, t)	
print('Status: %d' % statusCode)
print 'Body: %s' % t.contents

data = json.dumps({"name": "test_repo", "description": "Some test repo"})
statusCode = RestPost("%s/hellopost" % baseurl, data)  
print('Status: %d' % statusCode)

data = CreateXMLDocument()
statusCode = RestPost("%s/hellopost" % baseurl, data)  
print('Status: %d' % statusCode)

StopHttpServer()

