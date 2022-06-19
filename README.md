# MAGE
MAGE stands for Minimalistic Actions Graph Executor. Some applications needs to execute a sequence of actions with 
some conditional branching. Sometimes these flows are big enough, sometimes they are small. Bigger ones typically
treated as workflows and BPM engines like Camunda are used to design and execute them. But what to do with smaller
cases? What if You consider BPM engine too heavyweight and complicated?  
So, here is MAGE.  
Note that project is in preview stage, first release is expected till the end of July 2022.

## Features:
- Represents a graph of actions to execute, applied to workflow data
- Workflow data can be any type: a Map, a POJO
- Actions represented as Consumer of data object
- Has routing on exceptions
- Has a fluent builders so flow can be defined just in code. Those builders may use objects for actions and predicates 
but also may look up beans by name.
- Offers interface to integrate with DI
- Supports subflows. Unfortunately, current implementation uses the same data as for main flow
- Offers interface for persistence of data and execution state that is called once implementation is supplied
- Supports resume from given execution state. If You run multiple instances of application and one of them dies, other
may pick up data and execution state and continue from the point it was at.
- May be executed synchronously in the same thread or submitted to executor

## Disadvantages
- DI integration implementation for Spring is planned but not implemented yet. Micronaut integration may
  be provided later.
- Still executed in a single thread. Simultaneous execution of 20 flows in just 4 threads for a moment isn't possible. 
Though refactoring to achieve this is planned
- Workflow definition in POJO and JSON with persistence for POJO representation is planned but not implemented yet
- Visual designer is missing and isn't planned yet
- Doesn't offer persistence implementation out of the box
- Whatever else You expected from such engine but didn't found here

## Examples

## How it works
There are 3 node types: action, router and subflow.  
Action node executes a single action. For convenience You may supply also a predicate making action execution
conditional. Action node has at most one successor node.  
Router node is an if - else if - else node, allowing several paths of execution. So, it has many successors and selects
one according to first predicate returned true.  
Subflow node controls a subflow execution treated as a single action. Subflow node has at most one successor node.  
Each node has an exception handling that allows to continue execution on distinct path in case of exception.  
Nodes a referenced indirectly, by node id. Workflow completes when reaches a node without successor (i. e. next node id
is null). On router node it means that matched predicate corresponds to a null node id.  

## Usage hints

* For router nodes it is recommended to use new flow builder for each path instead of just node name unless referenced
node is defined earlier in the flow and You want to make a cycle.
* It is safe use the same extra FlowBuilder several times, it will reference the same part of graph.
* Use beans for consumers and predicates. If You find it boring to declare some of them as beans, provide a cache to
bean resolver. This will allow You to use flow definitions once they get implemented.
