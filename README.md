# GameGAN

First create a file ``my_python_path.txt`` in the root directory. This file should contain a single line with the full path to the Python executable you want to use. An example for Windows might look like this: 
```
C:\Program Files\Python36\python.exe
```
This is required for the program to locate the Python executable and use the GAN. For the GAN to work, you will also need to install PyTorch.

With these requirements in place, you should be able to launch either ``InteractiveMarioGAN.bat`` or ``InteractiveZeldaGAN.bat``. On a Windows system, you can double-click the batch files, but on Mac/Linux systems you can simply launch them as bash files with a command like:
```
bash InteractiveMarioGAN.bat
```
These files rely on the pre-compiled jar file in the build directory. If you make changes to the code, you will need to re-export a runnable jar to this location in order to see the changes.

Alternatively, if you want to launch the interactive evolution directly from an IDE, run the class ``edu.southwestern.tasks.interactive.mario.MarioGANLevelBreederTask`` for Mario or ``edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask`` for Zelda.


## Docker
Requires docker. Only tested on Ubuntu. (Other OS might not have the $PWD environment variable for the current directory)

```
git clone git@github.com:schrum2/GameGAN.git
cd GameGAN
git checkout CPPNtoGAN
docker build -t gamegan .
docker run -ti --name GameGAN --rm -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix -v $PWD:/app gamegan /bin/bash -c "cd /app; bash ObjectiveMarioCPPNtoGAN.bat"
```

## Known issues

### java.lang.NoClassDefFoundError: sun/reflect/ReflectionFactory$GetReflectionFactoryAction
This software uses the sun.reflect package, which was discontinued. Use Java 8 to fix.

### PILLOW_VERSION undefined
pillow 7.0.0 has removed "PILLOW_VERSION", but torchvision 0.4.2 uses it. So downgrade to Pillow 6.1
```
pip3 uninstall Pillow
pip3 install Pillow==6.1
```

### ImportError: No module named models.dcgan
Use python3

### [Docker] No X11 DISPLAY variable was set, but this program performed an operation which requires it.
??

### [Docker] Directory /etc/sudoers.d nonexistent
Install package sudo
```
apt-get install sudo
```
