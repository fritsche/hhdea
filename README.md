# HHdEA
HHdEA - Hyper-heuristics for distributed Evolutionary Algorithms

To create jar files:

```sh
cd HHdEA
make 
```
It will generate `HHdEA-1.0-SNAPSHOT-jar-with-dependencies.jar` inside `target` folder.

Usage:
```
usage: java -cp <jar> br.ufpr.inf.cbio.hhdea.runner.Main [-a <algorithm>]
       [-h] [-id <id>] [-M <methodology>] [-m <objectives>] [-o <fir>] [-P
       <path>] [-p <problem>] [-s <seed>]

Execute a single independent run of the <algorithm> on a given <problem>.
 -a,--algorithm <algorithm>       set the algorithm to be executed:
                                  NSGAII, MOEAD, MOEADD, ThetaDEA,
                                  NSGAIII, SPEA2, SPEA2SDE, HypE, MOMBI2,
                                  Traditional, HHdEA (default), <other>.If
                                  <other> name is given, HHdEA will be
                                  executed and the algorithm output name
                                  will be <other>.
 -h,--help                        print this message and exits.
 -id <id>                         set the independent run id, default 0.
 -M,--methodology <methodology>   set the methodology to be used: NSGAIII
                                  (default), MaF, Arion.
 -m,--objectives <objectives>     set the number of objectives to
                                  <objectives> (default value is 3).
                                  <problem> and <methodology> must be set
                                  acordingly.
 -o,--output <fir>                enable some info output for
                                  hyper-heuristics, space separated,
                                  options include: fir firbin count.
 -P,--output-path <path>          directory path for output (if no path is
                                  given experiment/ will be used.)
 -p,--problem <problem>           set the problem instance: DTLZ[1-7],
                                  WFG[1-9], MinusDTLZ[1-7], MinusWFG[1-9],
                                  MaF[1-15]; default is WFG1.<methodology>
                                  must be set accordingly.
 -s,--seed <seed>                 set the seed for JMetalRandom, default
                                  System.currentTimeMillis()

Please report issues at https://github.com/fritsche/hhdea/issues
```
