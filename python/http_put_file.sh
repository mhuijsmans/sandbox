#!/usr/bin/python

import requests
# the source: http://docs.python-requests.org/en/latest/

the_url = 'http://127.0.0.1:8003/path'
config_filename = 'config.xml'

# read the configfile from disk
with open (config_filename, "r") as myfile:
    xmldoc=myfile.read().replace('\n', '')

# put the config file
put_http_headers = {'Content-Type': 'application/xml'}
print requests.put(the_url, data=xmldoc, headers=put_http_headers).text

# get the configfile to double check what has been put
print requests.get(the_url).text
