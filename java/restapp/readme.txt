The project explores the implementation of a workflow engine for implementation of technical workflows.

For concurrency 2 thoughts are considered
- multi-threading
  todo: more details / decision to be taken.
- processes specific execution-queue  
  One event is executed. 
  client-code that accesses a remote resource (e.g. via REST), pauses execution, waiting for an event.
         So some threading is needed, but there is a single process thread.
         When response is available, an event is posted to the process resulting in continuation
  In case or error, all paused tasks are aborted. 
- an actor model (e.g. java akka) has been considered, but dropped, because of the ambition to use the workflow 
  concept for application level modeling.           

Concurrency also requires thoughts on the appl. execution model
- there is a president process and there are 0-n child processes.
  A child has a parent, which is a child or is the president. 
- a process can execute only one path.
  Concurrency is implemented via child processes fork & join.
  > at fork, parent data is copied to child.
  > at join, 
    a) all changed data is merged, provided there are no conflicts
    b) the defined result data set is added to the parent, provided that there are no conflicts   
    
 Todo:
 - use of thread priorities