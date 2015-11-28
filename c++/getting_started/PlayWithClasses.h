/*
 * PlayWithClasses.h
 *
 *  Created on: Mar 13, 2014
 *      Author: martien
 */

#ifndef PLAYWITHCLASSES_H_
#define PLAYWITHCLASSES_H_

#include <string>

class PlayWithClasses {
public:
	PlayWithClasses();
	virtual ~PlayWithClasses();

	void itIsTimeToPlay();
private:
	std::string getId();
	int id;
};

#endif /* PLAYWITHCLASSES_H_ */
