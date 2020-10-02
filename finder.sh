#!/bin/bash

# compile to out
javac -d out -cp src/com/github/jojo2357/textreplacer/ ./*.java
# read user input
read -pr "Find: " FIND
read -pr "Replace: " REPLACE
# run
java -cp out/ Main -r "$FIND" -s "$REPLACE"
