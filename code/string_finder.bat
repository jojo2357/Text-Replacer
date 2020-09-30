javac *.java
set /p find=find:
set /p replace=replace:
java Main -r %find% -s %replace%