/*
 * Filename: SrcLocation.h
 */

#ifndef SRCLOCATION_H_
#define SRCLOCATION_H_

#include <ostream>
#include <typeinfo>

struct SrcLocation {
	SrcLocation(const std::string& file, const std::string& function, int line);
	SrcLocation(const std::string& file, const std::string& className, const std::string& function, int line);
	~SrcLocation();
  std::string where;

  friend std::ostream& operator<<(std::ostream& os, const SrcLocation& dt);
};

#define SRC_LOCATION SrcLocation(__FILE__, __func__, __LINE__)
#define CLS_SRC_LOCATION SrcLocation(__FILE__, typeid(*this).name(), __func__, __LINE__)

#endif /* SRCLOCATION_H_ */
