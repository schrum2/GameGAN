set style data lines
set xlabel "Generation"

set title "InteractiveMarioGAN-Explore2_parents User Preference"
plot \
"InteractiveMarioGAN-Explore2_parents_log.txt" u 1:2 t "MIN", \
"InteractiveMarioGAN-Explore2_parents_log.txt" u 1:3 t "AVG", \
"InteractiveMarioGAN-Explore2_parents_log.txt" u 1:4 t "MAX"

pause -1

