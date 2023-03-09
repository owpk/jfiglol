### animated lolcat combined with figlet

<p align="center">
   <img src="https://github.com/vzvz4/jfiglol/blob/master/img/g.gif"/>
</p>

## Prerequisites
 - jdk 17  
 you can use [sdk man](https://sdkman.io/install) if you are on linux machine
 - also check [GraalVM native-image](#gvm) section if you want to build native 'jfiglol' application

## Build
```bash
$ ./gradlew shadowJar
$ sudo cp build/libs/jfiglol-*-all.jar /bin/jfiglol.jar
$ echo "alias jfiglol='java -jar /bin/jfiglol.jar' $@" >> ~/.bashrc #<- or change to '.your_shellrc' file name
```
## Usage
- use file named "jfiglol", copy "jfiglol" to /usr/bin/ directory if you want

```bash
$ ./jfiglol "Your input here"
# don't forget double quotes if your text contains spaces

$ ./jfiglol "Some text" -a
# if you want to animate your input

$ ./jfiglol "Hello world!" -a -t /path/to/font.flf
# if you want to specify font file
```
 - [flf fonts](https://github.com/xero/figlet-fonts)

## <a name="gvm"></a><h1> GraalVM native-image </h1>

> prerequisites:
- java graalvm 17 (**yes only 17 java version required!**) [install guide](https://www.graalvm.org/docs/getting-started/)  
  also you can use [sdk man](https://sdkman.io/install) if you are on linux machine
- native-image [install guide](https://www.graalvm.org/reference-manual/native-image/)

* Produce a native image

   * With gradle:

   ```bash
   $ ./gradlew nativeBuild
   $ cd build/native/nativeCompile
   $ ./jfiglol "Hello world!" -a
   ```
---

## Micronaut 3.8.6 Documentation

- [User Guide](https://docs.micronaut.io/3.8.6/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.8.6/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.8.6/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)


- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)
## Feature lombok documentation

- [Micronaut Project Lombok documentation](https://docs.micronaut.io/latest/guide/index.html#lombok)

- [https://projectlombok.org/features/all](https://projectlombok.org/features/all)


