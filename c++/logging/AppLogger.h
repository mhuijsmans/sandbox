#ifndef APP_LOGGER_H_
#define APP_LOGGER_H_

#include <boost/log/trivial.hpp>

#define APP_LOG_TRACE BOOST_LOG_TRIVIAL(trace)
#define APP_LOG_DEBUG BOOST_LOG_TRIVIAL(debug)
#define APP_LOG_INFO BOOST_LOG_TRIVIAL(info)
#define APP_LOG_WARNING BOOST_LOG_TRIVIAL(warning)
#define APP_LOG_ERROR BOOST_LOG_TRIVIAL(error)
#define APP_LOG_FATAL BOOST_LOG_TRIVIAL(fatal)

#endif
