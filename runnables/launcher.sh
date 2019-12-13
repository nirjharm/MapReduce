#!/bin/bash
set -x
echo $1
v="./mprinstance.sh &"
x=""
for ((i=1; i<=$1;i++));
do
	x="$v $x"	
done
eval $x
