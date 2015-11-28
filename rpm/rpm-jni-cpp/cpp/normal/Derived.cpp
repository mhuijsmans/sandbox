#include <iostream>
#include "Derived.h"

int baselib::impl::Derived::Method1 ()  { 
  std::cout << "Derived::Method2" << std::endl;  
  return 1;
}

