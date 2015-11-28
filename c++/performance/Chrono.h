/*
 * Chrono
 *
 *  Created on: Aug 3, 2014
 *      Author: martien
 */

#ifndef Chrono_H_
#define Chrono_H_


class Chrono
{
public:
  Chrono();
  long elapsedTimeInMs();
  long nowMS();
  void reset();
private:
  long now;
};

#endif
