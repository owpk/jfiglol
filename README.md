### Translate selected text and send it to windows notification center

<img src="https://github.com/vzvz4/dotfiles/blob/master/transl.gif"/>

### Installation Guide
1. Make sure you have PowerShell installed
    - You can check it, go to C:/Windows/System32/WindowsPowerShell/v1.0 and search for powershell.exe

2. Make sure you have windows sub system linux (wsl) installed
	- You can check it, go to C:/Windows/System32/ and search for wsl.exe,
    see [installation guide](https://docs.microsoft.com/en-en/windows/wsl/install-win10) if needed.

3. To send translated text to notify you have to install [BurntToast](https://github.com/Windos/BurntToast), should work fine just follow instructions. If some issues occures try to do this: 
	 - Dont forget "unblock" the zip file before extracting the contents.
	 - Open powershell and run this command "Import-Module BurntToast"  

4. Download and unzip https://github.com/vzvz4/select-and-translate/releases/tag/0.0.2 or 
```
$ git clone https://github.com/vzvz4/select-and-translate 
```
- run wsl.exe, go to the folder you just downloaded and copy "gclip", "pclip", "translator" and "notif" to /usr/local/bin/ directory
- Step by step:
```bash
$ cd /mnt/c/'YOUR_PATH_TO_DOWNLOADS'/select-and-translate/tools/
$ cp * /usr/local/bin/
```
### Usage Guide
Copy any text you want to translate and double click to run-en.vbs, notification with translated text should pop up.

### Additions
 - Copy "run-\*.vbs" file (for example run-en.vbs), instead of "en" in "lang = en" paste any language you want to translate the selected text.

 - Also you can bind execution of the tranlsation script to specific keys, i found this solution https://www.youtube.com/watch?v=tPcw-gDDVwo but it works a bit slow

<img src="https://github.com/vzvz4/dotfiles/blob/master/transl-hotkey.gif"/>

 - Note that translated text copy to system clipboard so you can paste translation to where ever you want
