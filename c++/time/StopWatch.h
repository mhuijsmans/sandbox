#ifndef STOPWATCH_H__
#define STOPWATCH_H__

#include <chrono>

// copied from: http://codereview.stackexchange.com/questions/48872/measuring-execution-time-in-c
template<typename TimeT = std::chrono::microseconds,
    typename ClockT=std::chrono::high_resolution_clock,
    typename DurationT=double>
class Stopwatch
{
private:
    std::chrono::time_point<ClockT> _start, _end;
public:
    Stopwatch() { Start(); }
    void Start() { _start = _end = ClockT::now(); }
    DurationT Stop() { _end = ClockT::now(); return Elapsed();}
    DurationT Elapsed() {
        auto delta = std::chrono::duration_cast<TimeT>(_end-_start);
        return delta.count();
    }
};

#endif
