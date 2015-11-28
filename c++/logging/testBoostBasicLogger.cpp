/*
 *          Copyright Andrey Semashev 2007 - 2014.
 * Distributed under the Boost Software License, Version 1.0.
 *    (See accompanying file LICENSE_1_0.txt or copy at
 *          http://www.boost.org/LICENSE_1_0.txt)
 */

// ref: https://raw.githubusercontent.com/boostorg/log/master/example/doc/tutorial_logging.cpp

#include <iostream>

#include <boost/move/utility.hpp>
#include <boost/log/sources/logger.hpp>
#include <boost/log/sources/record_ostream.hpp>
#include <boost/log/sources/global_logger_storage.hpp>
#include <boost/log/utility/setup/file.hpp>
#include <boost/log/utility/setup/common_attributes.hpp>

namespace logging = boost::log;
namespace src = boost::log::sources;
namespace keywords = boost::log::keywords;

BOOST_LOG_INLINE_GLOBAL_LOGGER_DEFAULT(my_logger2, src::logger_mt)
BOOST_LOG_INLINE_GLOBAL_LOGGER_DEFAULT(my_logger3, src::logger)

void logging_function1()
{
    src::logger lg;

//[ example_tutorial_logging_manual_logging
    logging::record rec = lg.open_record();
    if (rec)
    {
        logging::record_ostream strm(rec);
        strm << "Hello, World!";
        strm.flush();
        lg.push_record(boost::move(rec));
    }
//]
}

void logging_function2()
{
    src::logger_mt& lg = my_logger2::get();
    BOOST_LOG(lg) << "Greetings from the global MT logger!";
}

void logging_function3()
{
    src::logger& lg = my_logger3::get();
    BOOST_LOG(lg) << "Greetings from the global non-MT logger!";
}

int BoostBasicLoggerTest() {

    // logging::add_file_log("sample.log");
    logging::add_common_attributes();

    logging_function1();
    logging_function2();
	logging_function3();
	
	std::cerr << "Success" << std::endl;
	exit(0);
	return 0;
}

#include "gtest/gtest.h"
TEST(boostBasicLoggerTest, testUsingDefaults) {
	EXPECT_EXIT(BoostBasicLoggerTest(), ::testing::ExitedWithCode(0),
			"Success");
}
