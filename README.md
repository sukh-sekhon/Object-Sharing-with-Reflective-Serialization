# Object Sharing with Reflective Serialization
[![forthebadge](https://forthebadge.com/images/badges/made-with-Java.svg)](Controller.java)

## Dependencies
__JDOM2__ is used for serializaing/deserializing objects and can be downloaded [here](http://www.jdom.org/downloads/index.html).

## Usage
1. Clone the repo `git clone https://github.com/sukhjot-sekhon/Object-Sharing-with-Reflective-Serialization.git`
2. Compile all files `javac *.java`
3. Run the Controller class `java Controller`
4. Follow instructions displayed on the command line

## Features
* __Sending Mode__
	* __Object Creator__
		* Create instances of objects defined by the user
		* Types of objects:
			* Primitive
			* Reference
			* Primitive Array
			* Reference Array
			* ArrayList
	* __Serializer__
		* Serializes objects by generating an XML document using JDOM2
	* __Socket Sender__
		* Sends the serialized XML document via a socket
* __Receiving Mode__
	* __Socket Receiver__
		* Retrieves the serialized XML document via a socket
	* __Deserializer__
		* Deserializes a serialized XML document by instantiating and setting objects
  	* __Visualizer__
  		*   Inspects objects and produces a complete visualization
  		*   Displays
  			* __Declaring class__, __superclass__, and __interface__(s)
  			* __Method__(s) with their __exception__(s), __parameter__(s), and __modifier__(s)
  			* __Constructor__(s) with their __parameter__(s) and __modifier__(s)
  			* __Field__(s) with their __type__, __modifier__(s), and __value__
       
