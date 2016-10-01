# Multithreaded Game of Life
This was an assignment in CS351: Design of Large Programs. It is a multithreaded implementation of Conway's game of life, designed to showcase threaded programming and the performance impact of memory access patterns.

## Notes:
* scrollable with either click-n-drag, scrollbars, or cursor/page up/page down keys.
 * 'Reset' button will re-seed the grid with random values
 The spinner next to the play button selects the number of threads.
  * Allowed values are in [1, NUM_CPU_CORES * 2] \(to allow for testing of what happens when more threads than cores are used)
      * This number is calculated at the start of the program, so you will have to restart it if you somehow manage to add more CPUs to a live system
  * The dropdown selects a preset for the load button to load
  * Contains 6 presets:
    * A 'blank' preset that loads an empty grid
    * A Turing Machine taken from http://www.conwaylife.com/wiki/Turing_machine
    * A 2-period oscillator called the 'ring of fire' from http://conwaylife.com/wiki/Ring_of_fire
    * A gun that fires ships taken from http://conwaylife.com/wiki/3-engine_Cordership_gun
    * A rake (a ship that fires ships) taken from http://conwaylife.com/wiki/3-engine_Cordership_rake
    * A calculator of Fermat primes taken from http://conwaylife.com/wiki/Fermat_prime_calculator
      * The pattern destroys a section of itself when a Fermat prime is found.
      * A number N is tested for Fermat primality around generation 120N-550, and the pattern destroys a section of itself each time a prime is found
      * the pattern is rigged to completely self-destruct if a Fermat prime larger than 65537 (the largest known) is found.
