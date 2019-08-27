#!/usr/bin/env bash

#!/bin/sh

##### RUN it on the production queue
nohup /nfs/pride/work/java/jdk1.8.0_144/bin/java -jar redis-scheduled-listener-0.0.1-SNAPSHOT.jar &
echo $! > start-redis-scheduled-listener.pid