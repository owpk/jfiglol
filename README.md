### animated lolcat combined with figlet

<p align="center">
   <img src="https://github.com/vzvz4/jfiglol/blob/master/img/g.gif"/>
</p>

## Usage
- use file named "jfiglol", copy "jfiglol" to /usr/bin/ directory if you want

```bash
$ ./jfiglol "Your input here"
# don't forget double quotes if your text contains spaces

$ ./jfiglol "Some text" -a
# if you want to animate your input
```
### Command pattern:

jfiglol \[mode\] \[printer\] \[options\]

You can download figlet fonts here http://www.figlet.org/examples.html aslo there is few in "fonts" folder.
```
with specific font
$ ./jfiglol --font "/path/to/font.flf" "Your text Here" -r

print file
$ ./jfiglol --file "path/to/file" -r

just your input
$ ./jfiglol --plain "Your input here" -r
```
 - Examples:

 animated output with 3d.flf font and rainbow colors print mode
 ```
$ ./jfiglol --font "/path/to/font.flf" "You text Here" --rainbow --animated
```
<img src="https://github.com/vzvz4/jfiglol/blob/master/img/rainbow.gif"/>

animated output with 3d.flf font and gradient colors print mode
 ```
$ ./jfiglol --font "/path/to/font.flf" "You text Here" --gradient --animated
```
<img src="https://github.com/vzvz4/jfiglol/blob/master/img/gradient.gif"/>

animated output with 3d.flf font and mono colors print mode
 ```
$ ./jfiglol --font "/path/to/font.flf" "You text Here" --mono --animated
```
<img src="https://github.com/vzvz4/jfiglol/blob/master/img/mono.gif"/>

 - Also you can use --verbose (-v), --random (-r) and --debug (-d) options
```
$ ./jfiglol --font "./fonts/3d.flf" "You text Here" --mono -d -v -r
```
<img src="https://github.com/vzvz4/jfiglol/blob/master/img/help.png"/>

<h1> GraalVM native-image </h1>

> prerequisites:
- java graalvm (install guide: https://www.graalvm.org/docs/getting-started/)
- native-image (install guide: https://www.graalvm.org/reference-manual/native-image/)

* Produce a native image

   * With gradle:

   ```bash
   $ ./gradlew nativeImage
   $ cd build/bin
   ```

   * Manually:
   1. compile java classes and create configuration file directory

   ```bash
   $ mkdir bin
   $ javac -d ./bin ./src/**/*.java && cd bin
   $ jar cfm jfiglol.jar ../Manifest.txt ./com/*
   $ java -agentlib:native-image-agent=config-output-dir=conf/ -jar jfiglol.jar test
   ```

   2. produce native image

   ```bash
   $ native-image --allow-incomplete-classpath \
     --report-unsupported-elements-at-runtime \
     --no-fallback \
     --no-server \
     -H:ConfigurationFileDirectories=conf/ -jar jfiglol.jar
   ```

* You should see "jfiglol" binary file in current directory, you can copy it to your path like "/usr/local/bin"
