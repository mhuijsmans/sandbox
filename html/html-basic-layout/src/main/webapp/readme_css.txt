Difference
.navigation
  Classes are used where the style is repeated, e.g. say you head a special form of header
  for error messages, you could create a style h1.error {} which would only apply to <h1 class="error">
  .foo {} will style all elements with an attribute class="foo"
#navigation
  Generally speaking, you use # for styling something you know is only going to appear once, 
  for example, things like high level layout divs such sidebars, banner areas etc.
  #foo {} will style the single element declared with an attribute id="foo"
  
  