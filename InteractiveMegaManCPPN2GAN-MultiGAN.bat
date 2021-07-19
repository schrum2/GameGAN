java -jar build/MMNEAT.jar runNumber:$1 randomSeed:$1 trials:1 mu:16 maxGens:500 io:true netio:true mating:true task:edu.southwestern.tasks.interactive.megaman.MegaManCPPNtoGANLevelBreederTask watch:true cleanFrequency:-1  simplifiedInteractiveInterface:false saveAllChampions:true ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA imageSize:200 GANInputSize:5 showInteractiveGANModelLoader:false base:interactivemegamancppn2gan saveTo:MultiGAN log:InteractiveMegaManCPPN2GAN-MultiGAN cleanOldNetworks:false megaManGANLevelChunks:10 megaManUsesUniqueEnemies:false useMultipleGANsMegaMan:true MegaManGANHorizontalModel:MegaManSevenGANHorizontalWith12TileTypes_5_Epoch5000.pth MegaManGANVerticalModel:MegaManSevenGANUpWith12TileTypes_5_Epoch5000.pth MegaManGANUpModel:MegaManSevenGANUpWith12TileTypes_5_Epoch5000.pth MegaManGANDownModel:MegaManSevenGANDownWith12TileTypes_5_Epoch5000.pth MegaManGANUpperLeftModel:MegaManSevenGANUpperLeftCornerWith12TileTypes_5_Epoch5000.pth MegaManGANUpperRightModel:MegaManSevenGANUpperRightCornerWith12TileTypes_5_Epoch5000.pth MegaManGANLowerLeftModel:MegaManSevenGANLowerLeftCornerWith12TileTypes_5_Epoch5000.pth MegaManGANLowerRightModel:MegaManSevenGANLowerRightCornerWith12TileTypes_5_Epoch5000.pth cleanOldNetworks:false allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 recurrency:false includeFullSigmoidFunction:true includeFullGaussFunction:true includeCosineFunction:true includeGaussFunction:false includeIdFunction:true includeTriangleWaveFunction:true includeSquareWaveFunction:true includeFullSawtoothFunction:true includeSigmoidFunction:false includeAbsValFunction:false includeSawtoothFunction:false mating:true fs:false