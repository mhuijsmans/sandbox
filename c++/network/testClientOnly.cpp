/*
 * Filename: testClientOnly.cpp
 */
#include <iostream>

#include "gtest/gtest.h"

#include <boost/network/protocol/http/client.hpp>

using namespace boost::network;
using namespace boost::network::http;

TEST(NetworkingTest, errorClientErrorCanNotConnect) {
	// According to: https://groups.google.com/forum/#!topic/cpp-netlib/evYfu2InQwI
	// client is threadsafe and request/response shall be used inside try/catch
	client httpClient;
	try {
		client::request request_("http://127.0.0.1:8000/");
		client::response response_ = httpClient.get(request_);
		int statusCode = status(response_);
		std::cout << "NetworkingTest::errorClientErrorCanNotConnect: status="
				<< statusCode << std::endl;
		FAIL();
	} catch (std::exception& e) {
		// Broken pipe
		std::cout << e.what() << '\n';
		SUCCEED();
	}
}
