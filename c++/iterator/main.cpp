#include <iostream>

class ModelIterator1Result {
public:
  int id;
};

class ModelIterator1 {
private:
  ModelIterator1Result resultBegin;
  ModelIterator1Result middel;  
  ModelIterator1Result resultEnd;
  int max;
public:
  ModelIterator1(int max_) : resultBegin(), middel(), resultEnd(), max(max_)  {}
  typedef ModelIterator1Result * iterator;
  typedef const ModelIterator1Result * const_iterator;
  iterator begin() { 
     return &resultBegin; 
  }
  iterator end() { 
    return &resultEnd; 
  }
};

class ModelIterator2 {
private:
  int max;
public:
  ModelIterator2(int max_) : max(max_)  {}
  typedef int iterator;
  typedef const int const_iterator;
  iterator begin() { 
     return 0; 
  }
  iterator end() { 
    return max; 
  }
};

class ModelIterator3Helper {
public:
  ModelIterator3Helper(int max_) : max(max_) , cntr(0) {
    SetMsg();
  }

  bool operator !=(ModelIterator3Helper const& b) {
    return cntr != max;
  }

  ModelIterator3Helper& operator*() {
    return *this;
  }  

  ModelIterator3Helper& operator ++() {
    cntr++;
    SetMsg();
    return *this;
  }

  std::string msg;

private:  
  int max;
  int cntr=0;

  void SetMsg() {
    msg = "hi-" + std::to_string (cntr);
  }
};

class ModelIterator3 {
private:
  ModelIterator3Helper helper;  
public:
  ModelIterator3(int max) : helper(max)  {}
  typedef ModelIterator3Helper &iterator;
  typedef const ModelIterator3Helper& const_iterator;
  iterator begin() { 
     return helper; 
  }
  iterator end() { 
    return helper; 
  }
};

int main(int argc, char **argv) {

	std::cout << "*****************************************************************" << std::endl;

	std::cout << "main ENTER" << std::endl;

{
  ModelIterator1 mi(2);
  for (auto it = std::begin(mi); it!=std::end(mi); ++it) {
    std::cout << "it1" << std::endl;
  }  
}
{
  ModelIterator2 mi(4);
  for (auto it = std::begin(mi); it!=std::end(mi); ++it) {
    std::cout << "it2" << std::endl;
  }
}
{
  ModelIterator3 mi(6);
  for (auto it = std::begin(mi); it!=std::end(mi); ++it) {
    std::cout << "it3 " << it.msg << std::endl;
  }
}
{
  ModelIterator3 mi(6);
  for (auto it: mi) {
    std::cout << "it4 " << it.msg << std::endl;
  }
}

	std::cout << "main LEAVE" << std::endl;
	return 0;
}


