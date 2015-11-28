/*
 * Filename: testNetworking.cpp
 */
#include <iostream>
#include <string>
#include <thread>
#include <memory>

#include "gtest/gtest.h"

#include <boost/network/protocol/http/client.hpp>
#include <boost/network/protocol/http/server.hpp>

using namespace boost::network;
using namespace boost::network::http;

struct hello_world;
typedef http::server<hello_world> http_server;
struct hello_world {
	//@Override
	void operator()(http_server::request const &request,
			http_server::response &response) {
		std::cout << "Hello_world.operator()\n";
		std::string ip = source(request);
		response = http_server::response::stock_reply(http_server::response::ok,
				std::string("Hello, ") + ip + "!");
	}
	//@Override
	void log(http_server::string_type const &info) {
		std::cerr << "ERROR: " << info << '\n';
	}
};

class SyncServerWorker {
public:
	SyncServerWorker() :
		handler(), options(handler), server(options.address("0.0.0.0").port("8000").reuse_address(true)) {
	}
	~SyncServerWorker() {
		stop();
	}
	void runInOwnThread() {
		serverThreadPtr.reset(new std::thread(&SyncServerWorker::run, std::ref(*this)));
	}
private:
	void run() {
		std::cout << "SyncServerWorker::run ENTER...\n";
		server.run();
		std::cout << "SyncServerWorker::run LEAVE...\n";
	}
	void stop() {
		std::cout << "SyncServerWorker::stop() ENTER" <<std::endl;
		if (serverThreadPtr.get()) {
			std::cout << "SyncServerWorker::stop(), stopping server httpserver " <<std::endl;
			// calling stop can generate a lot of lines:
			// ERROR: Bad file descriptor
			server.stop();
			std::cout << "SyncServerWorker::stop(), joining / waiting for httpserver to terminate" <<std::endl;
			// join is important. without you will get a segmentation exception
			serverThreadPtr->join();
		};
		std::cout << "SyncServerWorker::stop() LEAVE" <<std::endl;
	}
	hello_world handler;
    http_server::options options;
    http_server server;
    std::unique_ptr<std::thread> serverThreadPtr;
};

TEST(NetworkingTest, clientConnectsToSyncHttpServer) {
	SyncServerWorker worker;
	client httpClient;
	try {
		worker.runInOwnThread();
		std::this_thread::sleep_for(std::chrono::seconds(1));
		//
		client::request request_("http://127.0.0.1:8000/");
		request_ << header("Connection", "close");
		client::response response_ = httpClient.get(request_);
		// Explicit read of body required for request to be completed
		int statusCode = status(response_);
		std::string body_ = body(response_);
		std::cout << "NetworkingTest::clientConnectsToServer: status="
				<< statusCode << ", body=" << body_ << std::endl;
	} catch (std::exception& e) {
		std::cout << e.what() << '\n';
		FAIL();
	}
}
