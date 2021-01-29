### animated lolcat combined with figlet

<img src="https://github.com/vzvz4/jfiglol/blob/master/img/g.gif"/>

### Installation
1. Install java runtime enviroment
```
ubuntu
$ sudo apt install default-jre

arch
$ sudo pacman -S openjdk-8-jre-headless
```
run java -version you should see something like this:
```
openjdk version "8.0.1" 2018-04-17
OpenJDK Runtime Environment (build 10.0.1+10-Ubuntu-3ubuntu1)
OpenJDK 64-Bit Server VM (build 10.0.1+10-Ubuntu-3ubuntu1, mixed mode)
```
2. Clone repo and cd to 'app' directory
```
$ git clone "https://github.com/vzvz4/jfiglol"
$ cd jfiglol/app
```

### Usage
 - run commands from app directory

Patten:
$java Jfiglol \[mode\] \[printer\] \[options\]

You can download figlet fonts from here http://www.figlet.org/examples.html aslo there is few in fonts folder which is in root project folder) 
```
with specific font
$ java Jfiglol --font "./fonts/3d.flf" "You text Here" -r

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
<img src="https://github.com/vzvz4/jfiglol/blob/master/img/goodby.gif"/>
