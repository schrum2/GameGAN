set style data lines
set xlabel "Generation"

set title "InteractiveMarioGAN-Explore3_parents User Preference"
plot \
"InteractiveMarioGAN-Explore3_parents_log.txt" u 1:2 t "MIN", \
"InteractiveMarioGAN-Explore3_parents_log.txt" u 1:3 t "AVG", \
"InteractiveMarioGAN-Explore3_parents_log.txt" u 1:4 t "MAX"

pause -1

