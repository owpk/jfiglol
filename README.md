### animated lolcat combined with figlet

<img src="https://github.com/vzvz4/jfiglol/blob/master/img/g.gif"/>

### Usage
- Go to "app" folder.
- Option 1: use compiled native image binary named "jfiglol", copy "jfiglol" to /usr/bin/ directory 
- Option 2: use jar file. From directory root run java -jar Jfiglol.jar [commads]

Command patten:
$java Jfiglol \[mode\] \[printer\] \[options\]

You can download figlet fonts here http://www.figlet.org/examples.html aslo there is few in fonts folder which is in root project folder) 
```
with specific font
$ java Jfiglol --font "/path/to/font.flf" "You text Here" -r

print file
$ java Jfiglol --file "path/to/file" -r

just your input
$ java Jfiglol --plain "Your input here" -r
```
 - Examples:

 animated output with 3d.flf font and rainbow colors print mode
 ```
$ java Jfiglol --font "/path/to/font.flf" "You text Here" --rainbow --animated
```
<img src="https://github.com/vzvz4/jfiglol/blob/master/img/rainbow.gif"/>

animated output with 3d.flf font and gradient colors print mode
 ```
$ java Jfiglol --font "/path/to/font.flf" "You text Here" --gradient --animated  
```
<img src="https://github.com/vzvz4/jfiglol/blob/master/img/gradient.gif"/>

animated output with 3d.flf font and mono colors print mode
 ```
$ java Jfiglol --font "/path/to/font.flf" "You text Here" --mono --animated 
```
<img src="https://github.com/vzvz4/jfiglol/blob/master/img/mono.gif"/>

 - Also you can use --verbose (-v), --random (-r) and --debug (-d) options
```
$ java Jfiglol --font "./fonts/3d.flf" "You text Here" --mono -d -v -r
```
<img src="https://github.com/vzvz4/jfiglol/blob/master/img/help.png"/>

<h2> GraalVM Native-image </h2>
- produce a native image of the application:
go to "src" directory, run commands below to compile all java classes  
```bash
$ javac *.java
```
produce natice image
```bash
$ native-image --no-server --no-fallback --static \
-H:ReflectionConfigurationFiles="reflect-config.json" Jfiglol
```

