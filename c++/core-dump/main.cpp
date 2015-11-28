#include <iostream>


// baz() generates a segv
void baz() {
 int *foo = (int*)-1; // make a bad pointer
  printf("%d\n", *foo);       // causes segfault
}

int main()
{
    std::cout << "Going to crash" << std::endl;
	baz();
}
