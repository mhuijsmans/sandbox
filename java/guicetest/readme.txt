private modules
https://stackoverflow.com/questions/16987815/guice-binding-several-objects-with-different-dependencies

guice-jersey example
https://github.com/aluedeke/jersey2-guice-example
Unclear is that works for latest jersey release

Interesting statements.
- from https://stackoverflow.com/questions/18599871/do-guice-singletons-honor-thread-confinement
  "As you can see from the Scope interface, a scope is just a decorator for a Provider .."
- Modules are evaluated once per injector.
  Guice will provide the same singleton across threads for the same injector,
  but Guice can only provide the same singleton across threads if you use toInstance.