#!/bin/bash

cd /c/temp
isdiag $*
isdtrace trace.log trace.txt
rm trace.log
less trace.txt

