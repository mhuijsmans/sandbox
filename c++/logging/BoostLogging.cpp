/*
 * Filename: BoostLogging.cpp
 */

// Useful link:
// http://www.boost.org/doc/libs/1_56_0/libs/log/doc/html/sinks.html
// Configuration of BOOST to enable features.
#define BOOST_LOG_USE_NATIVE_SYSLOG

#include <iostream>
#include <iomanip>

#include <boost/smart_ptr/shared_ptr.hpp>
#include <boost/log/core.hpp>
#include <boost/log/expressions.hpp>
#include <boost/log/sinks/unlocked_frontend.hpp>
#include <boost/log/sinks/basic_sink_backend.hpp>
#include <boost/log/sinks/frontend_requirements.hpp>
#include <boost/log/sources/severity_channel_logger.hpp>
#include <boost/log/sources/record_ostream.hpp>

#include <boost/log/attributes/current_process_name.hpp>
#include <boost/log/attributes/current_thread_id.hpp>
#include <boost/log/expressions.hpp>
#include <boost/log/sinks/syslog_backend.hpp>

#include <boost/log/sinks/sync_frontend.hpp>
#include <boost/log/sources/severity_channel_logger.hpp>
#include <boost/log/sources/severity_logger.hpp>
#include <boost/log/trivial.hpp>
#include <boost/log/utility/setup/file.hpp>
#include <boost/log/utility/setup/common_attributes.hpp>
#include <boost/log/utility/setup/console.hpp>
#include <boost/log/support/date_time.hpp>

#include <boost/log/expressions/formatters/format.hpp>

#include <memory>

#include "BoostLogging.h"

// Boost guidelines: http://boost-log.sourceforge.net/libs/log/doc/html/log/how_to_read.html
namespace attrs = boost::log::attributes;
namespace expr = boost::log::expressions;
namespace keywords = boost::log::keywords;
namespace logging = boost::log;
namespace sinks = boost::log::sinks;

// Boost configuration Macro's
// http://boost-log.sourceforge.net/libs/log/doc/html/log/installation/config.html

void BoostLogging::Init() {
	InitAddCommonAttributes();
	InitSetLogLevelToDebug();
	// For trivial logging, Console is the default log. 
	// The call call, sets the console with an own formatter.
	InitLogToConsole();
}

void BoostLogging::InitSetLogLevelToDebug() {
	// Set Log Level
	boost::shared_ptr<logging::core> core = logging::core::get();
	//  Only log records with severity level not less than info
	// will pass the filter and end up on the console.
	core->set_filter(logging::trivial::severity >= logging::trivial::debug);
}

void BoostLogging::SetLogLevelToTrace() {
	// Set Log Level
	boost::shared_ptr<logging::core> core = logging::core::get();
	//  Only log records with severity level not less than info
	// will pass the filter and end up on the console.
	core->set_filter(logging::trivial::severity >= logging::trivial::trace);
}

void BoostLogging::InitAddCommonAttributes() {
	logging::add_common_attributes();
	// Ref: http://stackoverflow.com/questions/15853981/boost-log-2-0-empty-severity-level-in-logs
	// states: TimeStamp, LineID, and Message are common_attributes.
	// So severity needs to be registered.
	// Ref boost attributes: http://www.boost.org/doc/libs/1_54_0/libs/log/doc/html/log/detailed/attributes.html
	boost::log::register_simple_formatter_factory<
			boost::log::trivial::severity_level, char>("Severity");
	logging::core::get()->add_global_attribute("ProcessID",
			attrs::current_process_id());
	logging::core::get()->add_global_attribute("Process",
			attrs::current_process_name());
	logging::core::get()->add_global_attribute("ThreadID",
			attrs::current_thread_id());
}

// setup console log
void BoostLogging::InitLogToConsole() {
	// Formatter below is: Boost.Format-style" formatter.
	// options: http://www.boost.org/doc/libs/1_56_0/libs/log/doc/html/log/tutorial/formatters.html
	logging::add_console_log(
			//
			std::cout, //
			keywords::filter = logging::trivial::severity
					>= logging::trivial::info, //
			keywords::format =
					"<time=\\x22%TimeStamp%\\x22 level=\\x22%Severity%\\x22 process=\\x22%Process%\\x22 thread=\\x22%ThreadID%\\x22 msg=\\x22%Message%\\x22>" //
					);
}

void BoostLogging::InitLogToFile(std::string &fileName) {
	// http://www.boost.org/doc/libs/1_56_0/libs/log/doc/html/log/tutorial/sinks.html
	// For trivial logging, console is the default sink.
	// Once another sinks is added to the logging core, the default sink will no longer be used. 
	// The trivial logging macros can be used though.
	// ref: http://www.boost.org/doc/libs/1_56_0/libs/log/doc/html/log/tutorial/sinks.html
	logging::add_file_log( //
			keywords::file_name = fileName,
			// original code (next 3 lines)
//        keywords::file_name = "sample_%N.log",
//        keywords::rotation_size = 10 * 1024 * 1024,
//        keywords::time_based_rotation = sinks::file::rotation_at_time_point(0, 0, 0),
			keywords::format = "[%TimeStamp%]: %Message%");
}

// As this is a file, it has no synchronisation.
// With trivial logging, synchronisation is included.
// http://www.boost.org/doc/libs/1_56_0/libs/log/doc/html/log/tutorial.html#log.tutorial.trivial
void BoostLogging::InitLogToDevNull() {
	logging::add_file_log(
			keywords::file_name = "/dev/null",
			keywords::format =
					"[%TimeStamp%] [%Severity%] [process=%Process%-%ProcessId%] [thread=%ThreadID%]: %Message%");
}


// A trivial sink backend that requires no thread synchronization
class DevNullBackend :
    public sinks::basic_sink_backend< sinks::concurrent_feeding >
{
public:
    // The function is called for every log record to be written to log
    void consume(logging::record_view const& rec)
    {
    }
};
void BoostLogging::InitLogToDevNullUnsynchronized() {
	typedef sinks::unlocked_sink< DevNullBackend > sink_t;

    boost::shared_ptr< logging::core > core = logging::core::get();

    // One can construct backend separately and pass it to the frontend
    boost::shared_ptr< DevNullBackend > backend(new DevNullBackend());
    boost::shared_ptr< sink_t > sink(new sink_t(backend));
	
	// no formatter set, because
	// 'class boost::log::v2_mt_posix::sinks::unlocked_sink<DevNullBackend>' has no member named 'set_formatter'
		
    core->add_sink(sink);
}

void BoostLogging::InitLogToDevNullSynchronized() {
  boost::shared_ptr< logging::core > core = logging::core::get();

    boost::shared_ptr< sinks::text_file_backend > backend =
        boost::make_shared< sinks::text_file_backend >(
            keywords::file_name = "/dev/null"
        );

    // Wrap it into a synchronized frontend and register in the core.
    typedef sinks::synchronous_sink< sinks::text_file_backend > sink_t;
    boost::shared_ptr< sink_t > sink(new sink_t(backend));
	
	sink->set_formatter(
			expr::stream // line id will be written in hex, 8-digits, zero-filled
			 << expr::format_date_time< boost::posix_time::ptime >("TimeStamp", "%Y-%m-%d %H:%M:%S")
			 << ": " << expr::smessage);

    core->add_sink(sink);
}

// ref: http://linux.die.net/man/3/syslog
// states that syslog impl. stores the ident pointer as-is.
const char *BoostLogging::syslogIdentityPosix = "BoostLoggingPosix";
// Found typedef at: http://www.boost.org/doc/libs/1_56_0/libs/log/doc/html/log/detailed/sink_backends.html
typedef sinks::synchronous_sink<sinks::syslog_backend> sink_syslog;
void BoostLogging::InitLogToSysLogViaPosix() {
	// copied from: http://www.boost.org/doc/libs/1_56_0/libs/log/doc/html/log/detailed/sink_backends.html
	boost::shared_ptr<logging::core> core = logging::core::get();

	// Create a backend
	boost::shared_ptr<sinks::syslog_backend> backend(
			new sinks::syslog_backend(keywords::facility = sinks::syslog::user,
					keywords::ident = syslogIdentityPosix, keywords::use_impl =
							sinks::syslog::native));

	// Enable auto-flushing (for testing) after each log record written
	// I think this is default mode, because there is no buffer.
	// backend->auto_flush(true);

	// Set the straightforward level translator for the "Severity" attribute of type int
	backend->set_severity_mapper(
			sinks::syslog::direct_severity_mapping<int>("Severity"));

	// Wrap it into the frontend and register in the core.
	// The backend requires synchronization in the frontend.
	core->add_sink(boost::make_shared<sink_syslog>(backend));
}

void BoostLogging::InitLogToSysLogViaPosix(int formatter) {
	boost::shared_ptr<logging::core> core = logging::core::get();

	typedef sinks::synchronous_sink<sinks::syslog_backend> sink_t;

	// One can construct backend separately and pass it to the frontend
	boost::shared_ptr<sinks::syslog_backend> backend(
			new sinks::syslog_backend(keywords::facility = sinks::syslog::user,
					keywords::ident = syslogIdentityPosix, keywords::use_impl =
							sinks::syslog::native));
	boost::shared_ptr<sink_t> sink(new sink_t(backend));
	core->add_sink(sink);

	sink->set_filter(logging::trivial::severity >= logging::trivial::trace);
	if (formatter==0) {
	sink->set_formatter(
			expr::stream // line id will be written in hex, 8-digits, zero-filled
			 << expr::format_date_time< boost::posix_time::ptime >("TimeStamp", "%Y-%m-%d %H:%M:%S")
			 << ": " << expr::smessage);
	} else {
		// found via:: http://www.boost.org/doc/libs/1_56_0/libs/log/doc/html/log/detailed/expressions.html#log.detailed.expressions.formatters
		//sink->set_formatter( keywords::format("[%TimeStamp%]: %Message%") );
	}
}

const char *BoostLogging::syslogIdentityUdp = "BoostLoggingUdp";
void BoostLogging::InitLogToSysLogViaUDP() {
	boost::shared_ptr<logging::core> core = logging::core::get();

	// Create a new backend
	boost::shared_ptr<sinks::syslog_backend> backend(
			new sinks::syslog_backend(keywords::facility = sinks::syslog::user,
					keywords::ident = syslogIdentityUdp, keywords::use_impl =
							sinks::syslog::udp_socket_based));

	// Setup the target address and port to send syslog messages to
	backend->set_target_address("localhost", 514);

	// Create and fill in another level translator for "MyLevel" attribute of type string
	sinks::syslog::custom_severity_mapping<std::string> mapping("MyLevel");
	mapping["debug"] = sinks::syslog::debug;
	mapping["normal"] = sinks::syslog::info;
	mapping["warning"] = sinks::syslog::warning;
	mapping["failure"] = sinks::syslog::critical;
	backend->set_severity_mapper(mapping);

	// Wrap it into the frontend and register in the core.
	core->add_sink(boost::make_shared<sink_syslog>(backend));
}

void BoostLogging::LogAMessageForAllLevels() {
	LogAMessageForAllLevels(std::string());
}

void BoostLogging::LogAMessageForAllLevels(const std::string &msg) {
	int i = 0;
	// copied from: http://www.boost.org/doc/libs/1_56_0/libs/log/doc/html/log/tutorial/trivial_filtering.html
	BOOST_LOG_TRIVIAL(trace)<< "BoostLogging: a trace severity message; " << msg;
	BOOST_LOG_TRIVIAL(debug)<< "BoostLogging: a debug severity message; "<< msg;
	BOOST_LOG_TRIVIAL(info)<< "BoostLogging: an informational severity message; "<< msg;
	BOOST_LOG_TRIVIAL(warning)<< "BoostLogging: a warning severity message; "<< msg;
	BOOST_LOG_TRIVIAL(error)<< "BoostLogging: an error severity message; "<< msg;
	BOOST_LOG_TRIVIAL(fatal)<< "BoostLogging: a fatal severity message, with value: " << i << "; "<< msg;
}

void BoostLogging::LogTraceMessageManyTimes(const int max, bool complexMsg) {
	for (int i = 0; i < max; i++) {
		int v1 = 10 + i, v2 = 50 + i;
		double d = 10.89632109 + i;
		if (complexMsg) {
			BOOST_LOG_TRIVIAL(trace)<< "BoostLogging: a trace severity message " << v1 << "-" << v2 << " and double " << d;
		} else {
			BOOST_LOG_TRIVIAL(trace)<< "BoostLogging: a trace severity message ";
		}
	}
}

void BoostLogging::LogDebugMessageManyTimes(const int max) {
	for (int i = 0; i < max; i++) {
		int v1 = 10 + i, v2 = 50 + i;
		double d = 10.89632109 + i;
		BOOST_LOG_TRIVIAL(debug)<< "BoostLogging: a trace severity message " << v1 << "-" << v2 << " and double " << d;
	}
}

void BoostLogging::LogTraceMessage(const std::string &msg) {
	BOOST_LOG_TRIVIAL(trace)<< msg;
}

BoostLogging::BoostLogging() {
}

BoostLogging::~BoostLogging() {
}

