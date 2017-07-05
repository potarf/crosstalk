javac -d ./build/classes -cp source ./source/Main.java
jar cvfe dist/crosstalk.jar Main -C build/classes .
