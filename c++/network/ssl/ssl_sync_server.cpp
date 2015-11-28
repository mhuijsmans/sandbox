#include <boost/network/protocol/http/server.hpp>

#include <boost/asio.hpp>
#include <boost/asio/ssl.hpp>
#include <boost/shared_ptr.hpp>

#include <string>
#include <iostream>
#include <signal.h>

// ref: http://cpp-netlib.org/0.11.0/index.html#hello-world
// Updated with code from ssl_server.cpp

// https://groups.google.com/forum/#!topic/cpp-netlib/61E9vOhyBrE "think this deson't work"
// That is correct. I get (when testing with libcurl)
// == Info: Initializing NSS with certpath: sql:/etc/pki/nssdb
// == Info: NSS error -12263 (SSL_ERROR_RX_RECORD_TOO_LONG)
// == Info: SSL received a record that exceeded the maximum permissible length.
// With Firefox I also get the error: SSL_ERROR_RX_RECORD_TOO_LONG

namespace http = boost::network::http;

struct handler;
typedef http::server<handler> http_server;

struct handler {
    void operator() (http_server::request const &request,
                     http_server::response &response) {
        response = http_server::response::stock_reply(
            http_server::response::ok, "ssl-sync-server: Hello, world!");
    }

    void log(http_server::string_type const &info) {
        std::cerr << "ERROR: " << info << '\n';
    }
};

std::string password_callback(
    std::size_t max_length,
    boost::asio::ssl::context_base::password_purpose purpose) {
  return std::string("test");
}

/**
 * Clean shutdown signal handler
 *
 * @param error
 * @param signal
 * @param p_server_instance
 */
void shut_me_down(const boost::system::error_code& error, int signal,
                  boost::shared_ptr<http_server> p_server_instance) {
  if (!error) p_server_instance->stop();
}


int main(int argc, char * argv[]) {

try {

    if (argc != 3) {
        std::cerr << "Usage: " << argv[0] << " address port" << std::endl;
        return 1;
    }

    // ==================
      // setup asio::io_service
  boost::shared_ptr<boost::asio::io_service> p_io_service(
      boost::make_shared<boost::asio::io_service>());

  // Initialize SSL context
  boost::shared_ptr<boost::asio::ssl::context> ctx =
      boost::make_shared<boost::asio::ssl::context>(
          boost::asio::ssl::context::sslv23);
  ctx->set_options(boost::asio::ssl::context::default_workarounds |
                   boost::asio::ssl::context::no_sslv2 |
                   boost::asio::ssl::context::single_dh_use);

  // Set keys
  ctx->set_password_callback(password_callback);
  ctx->use_certificate_chain_file("server.pem");
  ctx->use_private_key_file("server.pem", boost::asio::ssl::context::pem);
  ctx->use_tmp_dh_file("dh512.pem");
    // ==================


  std::string host(argv[1]);
  std::string port(argv[2] );
  std::cout << "Listening on " << host << ":" << port << std::endl;

    handler handler_;
    http_server::options options(handler_);
    boost::shared_ptr<http_server> p_server_instance(boost::make_shared<http_server>(
        options.address(host)
               .port(port)
               .io_service(p_io_service)
               .reuse_address(true)
               .context(ctx)
          ));

    // setup clean shutdown
    boost::asio::signal_set signals(*p_io_service, SIGINT, SIGTERM);
    signals.async_wait(boost::bind(shut_me_down, _1, _2, p_server_instance));

    p_server_instance->run();

    // we are stopped - shutting down
    p_io_service->stop();

    std::cout << "Terminated normally" << std::endl;
    exit(EXIT_SUCCESS);
}
catch (const std::exception& e) {
    std::cout << "Abnormal termination - exception: " << e.what() << std::endl;
    exit(EXIT_FAILURE);
}

}


