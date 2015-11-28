/*
 * Filename: SrcLocation.cpp
 */

#include "SrcLocation.h"

SrcLocation::SrcLocation(const std::string& file, const std::string& function,
		int line) :
		where(file) {
	where.append(":function=").append(function).append(":line=").append(
			std::to_string(line));
}

SrcLocation::SrcLocation(const std::string& file, const std::string& className,
		const std::string& function, int line) :
		where(file) {
	where.append(":").append(className).append("::").append(function).append(
			":line=").append(std::to_string(line));
}

SrcLocation::~SrcLocation() {
}

std::ostream& operator<<(std::ostream& os, const SrcLocation& dt) {
	os << dt.where;
	return os;
}

