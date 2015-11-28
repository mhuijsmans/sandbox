Introduction
------------
This is a project using cpp-netlib for network services.

System
------
Linux

Experiences
-----------
- I could compile and run_ssl_server without the BOOST_HTTPS setttings
  test with firefox: https://127.0.0.1:8442/ works
  test with firefox: http://127.0.0.1:8442/ does not return a page.

- client_wget compiled with
  testing: ./client_wget http://127.0.0.1:8442 
    resulted in eternal wait (internal looping at: : fusion::tuple, parse_until ).

- testing: ./client_wget https://127.0.0.1:8442
    resulted in: Abnormal termination - exception: Invalid Version Part. 
     (through trace statements) I observed that buffer contained body (Hello World!!")
     instead of htpp status-line, e.g. HTTP/1.1 200 OK

- ./client_wget https://google.com
  works. no complaints 

- HTTPS Server (works when compiled with/without SSL).

- Client side: no possibility to change client tcp buffer size.

- O bserved that cpp-netlib test suite is limited with a single test case public server.  

References
----------
HTTPS server possible but only with async server
https://groups.google.com/forum/#!topic/cpp-netlib/61E9vOhyBrE

HTTP client side described here
http://cpp-netlib.org/0.11.1RC2/reference/http_client.html#http-methods 

design guide line client shared. request / response inside try catch
- https://groups.google.com/forum/#!topic/cpp-netlib/evYfu2InQwI

- synchronous client work but is deprecated
  ref: https://groups.google.com/forum/#!topic/cpp-netlib/3NS5NAHIOvI 

- ASIO based client / server example
  ref: http://julien.boucaron.free.fr/wordpress/?p=178

- server: read & write through callback
  ref: http://www.cppblog.com/toMyself/archive/2013/07/15/201819.html

- Different interesting example of cpp-netlib
  - code for async client
    https://github.com/cpp-netlib/cpp-netlib/issues/160
  - https://github.com/kaspervandenberg/https-tryout/blob/e8a918c5aa8efaaff3a37ac339bf68d132c6d2d6/httpClient.cxx
  - http://stackoverflow.com/questions/20523022/how-close-http-connection-with-cpp-netlib
  - http://stackoverflow.com/questions/20525372/https-server-with-cpp-netlib
  - netlib examples: https://github.com/cpp-netlib/cpp-netlib/tree/0.11-devel/libs/network/example
    includes SSL.
	
- CPP-netlib plans
  http://www.cplusplus-soup.com/2012/09/cpp-netlib-http-api-plans-update-part_27.html	