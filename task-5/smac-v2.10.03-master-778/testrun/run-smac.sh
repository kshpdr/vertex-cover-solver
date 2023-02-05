#!/bin/bash

f="scenario.txt"
if [ ! -z "$1" ] ; then
	f="scenario-$1.txt"
fi

../smac --scenario-file scenarios/$f --seed 1
