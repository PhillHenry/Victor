# Victor
Leverages Java's new(ish) APIs to efficiently handle vectors.
Also uses TornadoVM to offload work to the GPU.

# Compiling
Ensure you have Java 21 and invoke Maven with ` -Pjava21-target`, for example `mvn clean install -Pjava21-target` 

# Running with TornadoVM
Note that Maven delegates to `./bin/java` to exploit TornadoVM.
This is just a wrapper around the `tornado` executable but Maven insists that the JVM executable is called `java`. Hence this hack.
Note: you'll need to install a TornadoVM and set your `TORNADO_HOME` environment variable.

# CUDA Profiling 
Run something like this:

`mvn compile && /usr/local/NVIDIA-Nsight-Compute-2024.3/target/linux-desktop-glibc_2_11_3-x64/ncu --verbose --config-file off --export /home/henryp/Temp/MatrixExamples --force-overwrite  /home/henryp/Code/Java/TornadoVM/bin/sdk/bin/tornado --jvm "\-Xmx16g \-\-add-modules=jdk.incubator.vector \-Dtornado.logger.codegen=True"  "\-cp  ...    uk.co.odinconsultants.victor.MatrixExamples"`

This will output a file that can be loaded in via `ncu-ui` and inspected for optimizations.