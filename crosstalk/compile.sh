javac -d ./build/classes ./source/*.java
jar cvfe crosstalk.jar Main -C build/classes .
