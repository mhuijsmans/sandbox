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

struct AsyncHandler;
typedef boost::network::http::async_server<AsyncHandler> AsyncServer;

struct AsyncHandler {
	void operator()(AsyncServer::request const &request,
			AsyncServer::connection_ptr connection) {
		static AsyncServer::response_header headers[] = { { "Connection",
				"close" }, { "Content-Type", "text/plain" } };
		connection->set_status(AsyncServer::connection::ok);
		connection->set_headers(
				boost::make_iterator_range(headers, headers + 2));
		// Write needs to go after set_status and set_headers!
		connection->write("Hello world!");
	}
};

class AsyncServerWorker {
public:
	AsyncServerWorker() :
		handler(), options(handler), server(options.address("0.0.0.0").port("8000").reuse_address(true)) {
		std::cout << "AsyncServerWorker::AsyncServerWorker" <<std::endl;
	}
	~AsyncServerWorker() {
		std::cout << "AsyncServerWorker::~AsyncServerWorker" <<std::endl;
		stop();
	}
	void runInOwnThread() {
		serverThreadPtr.reset(new std::thread(&AsyncServerWorker::run, std::ref(*this)));
	}
private:
	void run() {
		std::cout << "AsyncServerWorker::run ENTER...\n";
		server.run();
		std::cout << "AsyncServerWorker::run LEAVE...\n";
	}
	void stop() {
	std::cout << "AsyncServerWorker::stop() ENTER" <<std::endl;
	if (serverThreadPtr.get()) {
		std::cout << "AsyncServerWorker::stop(), stopping server httpserver " <<std::endl;
		server.stop();
		std::cout << "AsyncServerWorker::stop(), joining / waiting for httpserver to terminate" <<std::endl;
		// join is important. without you will get a segmentation exception
		serverThreadPtr->join();
	};
	std::cout << "AsyncServerWorker::stop() LEAVE" <<std::endl;
	}
    AsyncHandler handler;
    AsyncServer::options options;
    AsyncServer server;
    std::unique_ptr<std::thread> serverThreadPtr;
};


TEST(NetworkingTest, clientConnectsToAsyncHttpServer) {
	AsyncServerWorker worker;
	client httpClient;
	try {
		worker.runInOwnThread();
		std::this_thread::sleep_for(std::chrono::seconds(1));
		//
		client::request request_("http://127.0.0.1:8000/");
		request_ << header("Connection", "close");
		client::response response_ = httpClient.get(request_);
		int statusCode = status(response_);
		std::string body_ = body(response_);
		std::cout << "NetworkingTest::clientConnectsToAsyncServer: status="
				<< statusCode << ", body=" << body_ << std::endl;
	} catch (std::exception& e) {
		std::cout << e.what() << '\n';
		FAIL();
	}
}
