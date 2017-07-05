javac -d ./build/classes ./source/*.java
jar cvfe dist/crosstalk.jar Main -C build/classes .
