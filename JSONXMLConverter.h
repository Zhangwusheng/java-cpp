#ifndef JSON_JSON2XMLConverter_INCLUDED
#define JSON_JSON2XMLConverter_INCLUDED
 
 
#include "Poco/JSON/JSON.h"
#include "Poco/JSON/Handler.h"
 
 
class JSON2XMLConverter : public Poco::JSON::Handler
	/// JSON2XMLConverter converts JSON to XML.
{
public:
	JSON2XMLConverter(unsigned indent = 0);
		/// Creates the JSON2XMLConverter.
 
	JSON2XMLConverter(std::ostream& out, unsigned indent = 0);
		/// Creates the JSON2XMLConverter.
 
	~JSON2XMLConverter();
		/// Destroys the JSON2XMLConverter.
 
	void reset();
		/// Resets the handler state.
 
	void startObject();
		/// The parser has read a '{'; a new object is started.
 
	void endObject();
		/// The parser has read a '}'; the object is closed.
 
	void startArray();
		/// The parser has read a [; a new array will be started.
 
	void endArray();
		/// The parser has read a ]; the array is closed.
 
	void key(const std::string& k);
		/// A key of an object is read; it will be written to the output.
 
	void null();
		/// A null value is read; "null" will be written to the output.
 
	void value(int v);
		/// An integer value is read.
 
	void value(unsigned v);
		/// An unsigned value is read. This will only be triggered if the
		/// value cannot fit into a signed int.
		
	void value(Poco::Int64 v);
		/// A 64-bit integer value is read; it will be written to the output.
 
	void value(Poco::UInt64 v);
		/// An unsigned 64-bit integer value is read; it will be written to the output.
 
	void value(const std::string& value);
		/// A string value is read; it will be fromatted and written to the output.
 
	void value(double d);
		/// A double value is read; it will be written to the output.
 
	void value(bool b);
		/// A boolean value is read; it will be written to the output.
 
	void setIndent(unsigned indent);
		/// Sets indentation.
 
	template <typename T>
	void doValue(T val)
	{
		if (_array)
		{
			++_index;
			_out << '<' << _key << _index << ">" << val << "</" << _key << _index << '>';
		}
		else
			_out << val << "</" << _key << '>';
	}
 
private:
	std::ostream& _out;
	bool          _object;
	std::string   _key;
	bool          _array;
	int           _index;
};
 
 
#endif // JSON_JSON2XMLConverter_INCLUDED
