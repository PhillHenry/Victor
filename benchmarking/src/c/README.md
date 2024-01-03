Build the trivial C code with:

`
gcc -shared -o ../../target/classes/libtrivia.so -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" trivialJNI.c
`
