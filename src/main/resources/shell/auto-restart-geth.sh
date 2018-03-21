#!/bin/bash

kill -9 `ps -ef | grep "geth --rpc"|egrep -v "grep"|awk '{print $2}'`
nohup