The project explores the configuration to get core dump if program crashes

Experience
----------- 
- running app generates
  Going to crash
  Segmentation fault (core dumped)
  Initially no core dump. However after
  $ ulimit -c unlimited
  in the used shell, I got core dump when running the program.
- started new shell and observed that no core dump is generated.
  After above command, I again got core dump.
- core dump can be opened with kdbg: http://www.kdbg.org/manual/
  2 small steps
  1) open executable
  2) open core dump
  
From ref: http://stackoverflow.com/questions/866721/how-to-generate-gcc-debug-symbol-outside-the-build-target   
- after build main is: 26393
- copy debug symbols from executable main to file main.debug
  $ objcopy --only-keep-debug main main.debug
  Note: main is not affected.
- remove debug symbols from executable main
  $ objcopy --strip-debug main 
  Now main is: 9189
      main.debug is 22865  
- run, generating core dump  
- objcopy --add-gnu-debuglink main.debug main
  Now main is: 9309 (smaller ?)
- kdbg can open this main after which a core dump can be opened.  

Reference
---------
