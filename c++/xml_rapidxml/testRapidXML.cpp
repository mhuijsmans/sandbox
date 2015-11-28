/*
 * Filename: testXYZ.cpp
 */

#include <iostream>

#include "gtest/gtest.h"
#include "rapidxml.hpp"
#include "rapidxml_print.hpp"

#include "IOUtils.h"

using namespace rapidxml;

TEST(XyzTest, processNote) {

	std::string text = IOUtils::ReadFileToString("resources/note.xml");
	char buf[text.size() + 1];
	strcpy(buf, text.c_str());
	buf[text.size()] = 0;

	std::cout << "xml-doc[\n" << buf << "\n]" << std::endl;

	xml_document<> doc;    // character type defaults to char
	doc.parse<0>(buf);    // 0 means default parse flags

	std::cout << "Name of my first node is: " << doc.first_node()->name()
			<< "\n";
	xml_node<> *node = doc.first_node("note");
	for (xml_attribute<> *attr = node->first_attribute(); attr;
			attr = attr->next_attribute()) {
		std::cout << "Node \'note\' has attribute " << attr->name() << " ";
		std::cout << "with value " << attr->value() << "\n";
	}

	xml_node<>* to_node = node->first_node("to");
	std::cout << "Node \'to\' has value: " << to_node->value() << "\n";

	xml_node<>* from_node = node->first_node("from");
	std::cout << "Node \'from\' has value: " << from_node->value() << "\n";
}

TEST(XyzTest, processBook) {

	std::string text = IOUtils::ReadFileToString("resources/book.xml");
	char buf[text.size() + 1];
	strcpy(buf, text.c_str());
	buf[text.size()] = 0;

	std::cout << "xml-doc[\n" << buf << "\n]" << std::endl;

	xml_document<> doc;    // character type defaults to char
	doc.parse<0>(buf);    // 0 means default parse flags

	std::cout << "Name of my first node is: " << doc.first_node()->name()
			<< "\n";
	xml_node<> *book_node = doc.first_node("book");

	xml_node<>* authors_node = book_node->first_node("authors");

	xml_node<>* author = authors_node->first_node("author");
	std::cout << "Node \'author\' has value: " << author->value() << "\n";

	// Note that next_sibling operates on author level.
	author = author->next_sibling("author");
	std::cout << "Node \'author\' has value: " << author->value() << "\n";

	author = author->next_sibling("author");
	ASSERT_EQ(0, author);
}

// test copied from: http://www.setnode.com/blog/quick-notes-on-how-to-use-rapidxml/
TEST(XyzTest, createXmlDoc) {
	xml_document<> doc;

// xml declaration
	xml_node<>* decl = doc.allocate_node(node_declaration);
	decl->append_attribute(doc.allocate_attribute("version", "1.0"));
	decl->append_attribute(doc.allocate_attribute("encoding", "utf-8"));
	doc.append_node(decl);

// root node
	xml_node<>* root = doc.allocate_node(node_element, "rootnode");
	root->append_attribute(doc.allocate_attribute("version", "1.0"));
	root->append_attribute(doc.allocate_attribute("type", "example"));
	doc.append_node(root);

// child node
	xml_node<>* child = doc.allocate_node(node_element, "childnode");
	root->append_node(child);

	std::cout << doc;

	std::string xml_as_string;
	// watch for name collisions here, print() is a very common function name!
	print(std::back_inserter(xml_as_string), doc);
	// xml_as_string now contains the XML in string form, indented
	// (in all its angle bracket glory)
	std::cout << "### Indented xml doc[\n" << xml_as_string << "\n]" << std::endl;

	std::string xml_no_indent;
	// print_no_indenting is the only flag that print() knows about
	print(std::back_inserter(xml_no_indent), doc, print_no_indenting);
	// xml_no_indent now contains non-indented XML
	std::cout << "### Non-indented xml doc[\n" << xml_no_indent << "\n]" << std::endl;
}
