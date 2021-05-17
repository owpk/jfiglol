### animated lolcat combined with figlet

<img src="https://github.com/vzvz4/jfiglol/blob/master/img/g.gif"/>

### Usage
- use compiled native image binary named "jfiglol", copy "jfiglol" to /usr/bin/ directory   
Command patten:
jfiglol \[mode\] \[printer\] \[options\]

You can download figlet fonts here http://www.figlet.org/examples.html aslo there is few in fonts folder which is in root project folder) 
```
with specific font
$ ./jfiglol --font "/path/to/font.flf" "You text Here" -r

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

- Produce a native image of the application:

1. go to "src" directory, run commands below to compile all java classes  

```bash
$ javac *.java
```

2. produce native image  

```bash
$ native-image --no-server --no-fallback --static \
-H:ReflectionConfigurationFiles="reflect-config.json" Jfiglol
```
3. you should see "jfiglol" file in current directory
