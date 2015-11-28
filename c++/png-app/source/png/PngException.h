#ifndef PngException_H_
#define PngException_H_

#include <stdexcept>

class PngException: public std::exception
{
public:
	// explicit: c++ keyword to indicate that implicit conversion is allowed.
	// Example: see http://stackoverflow.com/questions/121162/what-does-the-explicit-keyword-in-c-mean
    explicit PngException(const char* message);

    explicit PngException(const std::string& message);

    virtual ~PngException() throw ();

    virtual const char* what() const throw () {
       return msg.c_str();
    }

protected:
    std::string msg;
};

#endif
