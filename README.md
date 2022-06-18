# LiWE
LiWE stands for Lightweight Workflow Engine. A lot of applications needs to execute series of actions with some
if-else branching. Sometimes these flows are big enough, sometimes they are small. Bigger ones typically treated as
workflows and BPM engines like Camunda are used to design and execute them. But what to do with smaller cases? What if
You consider BPM engine too heavyweight and complicated?  
So, here is LiWE.  
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

