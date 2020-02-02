set style data lines
set xlabel "Generation"

set title "InteractiveMarioGAN-Both12_parents User Preference"
plot \
"InteractiveMarioGAN-Both12_parents_log.txt" u 1:2 t "MIN", \
"InteractiveMarioGAN-Both12_parents_log.txt" u 1:3 t "AVG", \
"InteractiveMarioGAN-Both12_parents_log.txt" u 1:4 t "MAX"

pause -1

