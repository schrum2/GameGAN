set style data lines
set xlabel "Generation"

set title "InteractiveMarioGAN-Both2_parents User Preference"
plot \
"InteractiveMarioGAN-Both2_parents_log.txt" u 1:2 t "MIN", \
"InteractiveMarioGAN-Both2_parents_log.txt" u 1:3 t "AVG", \
"InteractiveMarioGAN-Both2_parents_log.txt" u 1:4 t "MAX"

pause -1

