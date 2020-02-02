set style data lines
set xlabel "Generation"

set title "InteractiveMarioGAN-Explore11_parents User Preference"
plot \
"InteractiveMarioGAN-Explore11_parents_log.txt" u 1:2 t "MIN", \
"InteractiveMarioGAN-Explore11_parents_log.txt" u 1:3 t "AVG", \
"InteractiveMarioGAN-Explore11_parents_log.txt" u 1:4 t "MAX"

pause -1

