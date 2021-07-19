REM Usage  : launchMultiple.bat <batch file> <starting run> <ending run> 
FOR /L %%A IN (%2,1,%3) DO (
  cd ToG-CPPNThenDirect2GAN
  cd Experiments-2021-MoreCPPN2GAN
  %1 %%A
)
ECHO "All done!"