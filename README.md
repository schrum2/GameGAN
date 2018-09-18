# GameGAN

First create a file my_python_path.txt in the root directory. This file should contain a single line with the full path to the Python executable you want to use. An example for Windows might look like this: 

C:\Program Files\Python36\python.exe

This is required for the program to locate the Python executable and use the GAN.

For the GAN to work, you will also need to install PyTorch.

With these requirements in place, you should be able to launch either InteractiveMarioGAN.bat or InteractiveZeldaGAN.bat. On a Windows system, you can double-click the batch files, but on Mac/Linux systems you can simply launch them as bash files with a command like:

bash InteractiveMarioGAN.bat

These files rely on the pre-compiled jar file in the build directory. If you make changes to the code, you will need to re-export a runnable jar to this location in order to see the changes.

If you want to launch the interactive evolution from directly in an IDE, run the class edu.southwestern.tasks.interactive.mario.MarioGANLevelBreederTask for Mario or edu.southwestern.tasks.interactive.gvgai.ZeldaGANLevelBreederTask for Zelda.