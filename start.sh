#!/bin/sh

/bin/sh -c "/app/query_sewobe.sh && nginx && /usr/sbin/crond -f -l 8"
