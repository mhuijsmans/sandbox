#include <string>
#include <fstream>
#include <iostream>
int main()
{
	{
		std::ofstream os("test1.txt",  std::fstream::out | std::fstream::binary);
		os << "hi";
		os.flush();
		os.close();
	}

	{
		std::ofstream os("/dev/null",  std::fstream::out | std::fstream::binary);
		os << "hi";
		os.flush();
		os.close();
	}


	//std::cout << "now               : " << now() << std::endl;
}
