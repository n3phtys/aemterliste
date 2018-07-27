#!/bin/sh

/bin/sh -c "/app/query_sewobe.sh && exec nginx -g 'daemon off;'"
