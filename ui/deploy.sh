#!/bin/bash
ps -ef | grep tomcat | grep -v grep | tr -s ' ' ':' | cut -d':' -f3 | xargs kill
rm -rf /usr/local/apache-tomcat-7.0.37/webapps/pinetree-0.0.1-SNAPSHOT
cp ./target/pinetree-0.0.1-SNAPSHOT.war /usr/local/apache-tomcat-7.0.37/webapps
/usr/local/apache-tomcat-7.0.37/bin/startup.sh
