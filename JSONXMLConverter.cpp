#include "JSON2XMLConverter.h"
#include <iostream>
 
 
JSON2XMLConverter::JSON2XMLConverter(unsigned indent):
	_out(std::cout),
	_object(false),
	_array(false),
	_index(0)
{
}
 
 
JSON2XMLConverter::JSON2XMLConverter(std::ostream& out, unsigned indent):
	_out(out),
	_object(false),
	_array(false),
	_index(0)
{
	_out << "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>";
}
 
 
JSON2XMLConverter::~JSON2XMLConverter()
{
	_out << "</root>";
}
 
 
void JSON2XMLConverter::reset()
{
	_out.flush();
	_array = false;
	_index = 0;
	_object = false;
	_key = "";
}
 
 
void JSON2XMLConverter::startObject()
{
	_object = true;
	if (!_key.empty())
		_out << '<' << _key << '>';
}
 
 
void JSON2XMLConverter::endObject()
{
 
	_object = false;
	_out << "</" << _key << '>';
}
 
 
void JSON2XMLConverter::startArray()
{
	_index = 0;
	_array = true;
}
 
 
void JSON2XMLConverter::endArray()
{
 
	_array = false;
	_index = 0;
}
 
 
void JSON2XMLConverter::key(const std::string& k)
{
	_key = k;
	_out << '<' << _key << '>';
}
 
 
void JSON2XMLConverter::null()
{
	doValue("null");
}
 
 
void JSON2XMLConverter::value(int v)
{
	doValue(v);
}
 
 
void JSON2XMLConverter::value(unsigned v)
{
	doValue(v);
}
 
 
void JSON2XMLConverter::value(Poco::Int64 v)
{
	doValue(v);
}
 
 
void JSON2XMLConverter::value(Poco::UInt64 v)
{
	doValue(v);
}
 
 
void JSON2XMLConverter::value(const std::string& value)
{
	doValue(value);
}
 
 
 
void JSON2XMLConverter::value(double d)
{
	doValue(d);
}
 
 
void JSON2XMLConverter::value(bool b)
{
	b ? doValue("true") : doValue("false");
}


/*

#include "JSON2XMLConverter.h"
#include "Poco/JSON/Parser.h" 
using Poco::JSON::Parser; 
using Poco::JSON::Handler; 
int main() 
{ 
	std::string json = "{ \"name\" : \"Homer\", \"age\" : 38, \"wife\" : \"Marge\", \"age\" : 36"
				", \"children\" : [ \"Bart\", \"Lisa\", \"Maggie\" ] }"; 
	Handler::Ptr pJ2XConv = new JSON2XMLConverter(std::cout);
	Parser(pJ2XConv).parse(json); 
	return 0; 
}
*/
