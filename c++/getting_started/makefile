CC=g++
CFLAGS=-c -Wall
LDFLAGS= -L/usr/local/lib -llog4cplus
SOURCES=main.cpp PlayWithClasses.cpp PlayWithCollections.cpp PlayWithStrings.cpp Test1.cpp ToBeTestedClass.cpp
OBJECTS=$(SOURCES:.cpp=.o)
EXECUTABLE=hello

all: $(SOURCES) $(EXECUTABLE)
	
$(EXECUTABLE): $(OBJECTS) 
	$(CC) $(LDFLAGS) $(OBJECTS) -o $@

.cpp.o:
	$(CC) $(CFLAGS) $< -o $@

clean:
	rm -rf *o $(EXECUTABLE)

