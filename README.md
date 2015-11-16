
###Abstract:

>This assignment has 2 part. One is finding an agent team which can cover all necessary skillsets. The other is finding a model which satisfies all clauses defined in knowledge base (KB) using DPLL algorithms with 2 heuristics. One is “Pure Symbol” and the other is “Unit Clause”. The goal of this assignment is comparing iterations and find which heuristics is efficient.

###How to define KB for *"Multi-agent task-assignment"* problem
`agent_job.kb` should include each agent’s capabilities and other constraints to find a right team for the required jobs.

1)	define each agent’s Capabilities
Based on the table on the description, the program generates rules below. 
For example, agent `a` is chosen, `the agent’s all 4 capabilities` should be `true`


```
-a painter
-a stapler
-a recharger
-a welder
-b cutter
-b sander
-b welder
-b stapler
-c cutter
-c painter
-d sander
-d welder
-d recharger
-e painter
-e stapler
-e welder
-f stapler
-f welder
-f joiner
-f recharger
-g stapler
-g gluer
-g painter
-f recharger
-h cutter
-g gluer
```

2)	define the relationship between jobs and agents
If a job is necessary to be done by an agent, one of agents which has the capability should be true. 
For example, `painter` can be done by agent `a`, `c`, `e`, or `g`.  


```
-painter a c e g
-stapler a b e f g
-recharger a d f g
-sander b d
-welder a b d e f
-cutter b c h
-joiner f
-gluer g h
```

3)	Define at most three agents can be chosen. 
This statement is equal to more than 4 agents can be selected at the same time. 
This result in enumerations below. Without this rule, the program easily get to the conclusion, picking out all the agents.

```
-a -b -c -d
-a -b -c -e
-a -b -c -f
-a -b -c -g
-a -b -c -h
-a -b -d -e
-a -b -d -f
-a -b -d –g
...
-d -e -g -h
-d -f -g -h
-e -f -g –h
```

4)	Define a rule of avoiding not chosen any agents
In case of not being chosen any agents, add the rule below.

```
a b c d e f g h
```

5)	Define which job should be done.
By adding query at the end of the KB, the program can match required jobs and necessary agents. For example:

```
cutter
welder
painter
joiner
recharger
```

###Heuristic options:

1. with Pure Symbol and Unit clause
2. with Unit clause only
3. Baseline (with only Backtracking)
