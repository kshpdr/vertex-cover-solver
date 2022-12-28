#!/bin/bash

cd "${0%/*}"

java Graph | python3 sat.py