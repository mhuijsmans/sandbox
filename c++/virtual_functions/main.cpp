#include <iostream>
#include "Derived.h"

//int main() {
int main(int argc, char **argv) {

  std::cout << "!!!Hello World!!!" << std::endl;

  baselib::impl::Derived derived;
  derived.Method1();

  return 0;
}

