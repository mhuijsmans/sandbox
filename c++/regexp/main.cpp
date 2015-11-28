#include <iostream>
#include <regex>
#include <string>

class Test2 {
public:

	void Test1() {
		Tester(std::string("/a/b/c"));
		Tester(std::string("/a/b/c/{"));
		Tester(std::string("/a/b/c/{a}a"));
		Tester(std::string("/a/b/c/{}"));
		Tester(std::string("/a/b/c/{d}"));
	}

	void Tester(std::string s) {
		std::cout << "String : " << s << ", size: " << s.size() << std::endl;
		std::size_t foundOpen = s.find("/{");
		if (foundOpen == std::string::npos) {
			std::cout << "/{ Not found" << std::endl;
			return;
		}
		std::size_t foundClose = s.find('}', foundOpen);
		if (foundClose == std::string::npos) {
			std::cout << "/{ found, but not }" << std::endl;
			return;
		}
		if (foundClose + 1 != s.size()) {
			std::cout << "} shall be last char: " << foundClose << std::endl;
			return;
		}
		if ((foundClose - foundOpen) == 2) {

			std::cout << "text required between { and }" << std::endl;
			return;
		}
		std::cout << "MATCH, found at [" << foundOpen << "," << foundClose
				<< "]" << std::endl;
		std::string firstPart = s.substr(0, foundOpen + 1);
		std::cout << ". first part: " << firstPart << std::endl;
	}
};

// Regexp examples below do not really work on fedora. I get std::regex_error.
// Fedora 2.0 has gcc 4.8.3. That seem to have problems with double backslash
// see: http://stackoverflow.com/questions/8060025/is-this-c11-regex-error-me-or-the-compiler

// Impression is that gcc 4.9 is works well (not fully supported).
// ref: https://gcc.gnu.org/gcc-4.9/changes.html

class Test {
public:
	void Test1() {
		Set("(\\+|-)?[[:digit:]]+");
		matches("1");
	}

	void Test2() {
		Set("[a-z");
		matches("a");
	}

	void matches(std::string value) {
		std::cout << "value: " << value << " matches: " << expr << " : "
				<< std::regex_match(value, regex) << std::endl;
	}

	void Set(const char * strExpr) {
		std::cout << "ENTER" << std::endl;
		expr = strExpr;
		std::regex tmp(strExpr, std::regex_constants::basic);
		regex = tmp;
		std::cout << "LEAVE" << std::endl;
	}

	std::string expr;
	std::regex regex;
};

int main() {
	try {

		Test2 t;
		t.Test1();
	} catch (const std::regex_error e) {
		std::cout << e.what() << std::endl;
	}
	return 0;

	std::cout << "***********" << std::endl;
	std::cout << "Regexp demo" << std::endl;

	{
		std::string regex_str = "[a-z_][a-z_0-9]*\\.[a-z0-9]+";
		std::regex reg1(regex_str, std::regex_constants::icase);

		const int MAX = 7;
		const std::string text[MAX] = // initialized here (not in the constructor)
				{ "Sunday", "aon.day", "Tuesday", "Wedensday", "Thursday",
						"Friday", "Saturday" };
		for (int i = 0; i < MAX; i++) {
			if (std::regex_match(text[i], reg1)) {
				std::cout << "Match   : " << text[i] << std::endl;
			} else {
				std::cout << "No-match: " << text[i] << std::endl;
			}
		}
	}

	{
		// Next is copied from: http://en.cppreference.com/w/cpp/regex/regex_match
		// AT that site yoy can selecrt multuiple compilers.
		// Wth 4.8 (C+11) i get a:
		//     terminate called after throwing an instance of 'std::regex_error'
		//     what():  regex_error
		// With 4.9 everything is fine.

//		std::string fnames[] = { "foo.txt", "bar.txt", "baz.dat", "zoidberg" };
//
//	    std::regex txt_regex("[a-z]+\\.txt");
//	    for (const auto &fname : fnames) {
//	        std::cout << fname << ": " << std::regex_match(fname, txt_regex) << '\n';
//	    }

//		std::regex pieces_regex("([a-z]+)\\.([a-z]+)");
//		std::smatch pieces_match;
//
//	    for (const auto &fname : fnames) {
//	        if (std::regex_match(fname, pieces_match, pieces_regex)) {
//	            std::cout << fname << '\n';
//	            for (size_t i = 0; i < pieces_match.size(); ++i) {
//	                std::ssub_match sub_match = pieces_match[i];
//	                std::string piece = sub_match.str();
//	                std::cout << "  submatch " << i << ": " << piece << '\n';
//	            }
//	        }
//	    }

	}

}
