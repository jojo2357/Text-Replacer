javac -d out -cp src/com/github/jojo2357/textreplacer/ *.java
set /p find=Find:
set /p replace=Replace:
java -cp out/ Main -r %find% -s %replace%