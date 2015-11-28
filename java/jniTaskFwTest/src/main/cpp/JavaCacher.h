/*
 * JavaCacher.h
 *
 *  Created on: Aug 17, 2014
 *      Author: martien
 */

#ifndef JAVACACHER_H_
#define JAVACACHER_H_

typedef struct
{
    int X;
    double Y;
} Coordinate_t;

typedef struct
{
	int Status;
    Coordinate_t Coordinate1;
    Coordinate_t Coordinate2;
    Coordinate_t Coordinate3;
    Coordinate_t Coordinate4;
} Coordinates_t;

typedef struct
{
		Coordinates_t Set1;
		Coordinates_t Set2;
} BigSet_t;

class JavaCacher {
public:
	JavaCacher();
	virtual ~JavaCacher();
	BigSet_t bigset;
};

#endif /* JAVACACHER_H_ */
