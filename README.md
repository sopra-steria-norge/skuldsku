
Purpose
=======
using a recording of a set of web requests, apply some regexps on the requests and store the responses. This will (separately) allow you to make two runs of the program against different backends and compare the result.

Required software
=================
Burp Suite Free Edition http://portswigger.net/index.html - provides a clientside proxy and allows for recording of requests to an xml file

Procedure
=========
prepare
* launch BurpSuite
* go to the Proxy/Intercept tab and ensure that intercepts are disabled (the tool will later block otherwise)
* go to the Proxy/Options tab and add a proxy "127.0.0.1:8080" in the top "Proxy Listeners"
* Launch your browser and configure a the proxy to 127.0.0.1:8080

record
* Run your tests in the browser, you should see the Proxy/History tab being populated as you go
* in the Proxy/History tab select the rows of requests that you'd like to replay later
* rightclick->Save items, say into requests.xml

replay
* create a file (say) regexps.txt where you will store a list of regexps for massaging the urls (to switch server to dev etc)
* it is recommended to include the line "Accept-Encoding: gzip, ===Accept-Encoding: "
* add patterns to the file in the format "vg.no===google.com" which will transform any mention of vg.no to google.com (where === is the separator)
* launch UrlFetcher by: 
	java UrlFetcher requests.xml regexps.txt
* The output will be a list of files in the current directory 
    the name will be n-xxx where n is the request number and xxx is the http return code
    the content of the files will be the data that is retrieved