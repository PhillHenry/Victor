# Victor
Leverages Java's new(ish) APIs to efficiently handle vectors.
Also uses TornadoVM to offload work to the GPU.

# Compiling
Ensure you have Java 21 and invoke Maven with ` -Pjava21-target`, for example `mvn clean install -Pjava21-target` 

# Running with TornadoVM
Note that Maven delegates to `./bin/java` to exploit TornadoVM.
This is just a wrapper around the `tornado` executable but Maven insists that the JVM executable is called `java`. Hence this hack.
Note: you'll need to install a TornadoVM and set your `TORNADO_HOME` environment variable.