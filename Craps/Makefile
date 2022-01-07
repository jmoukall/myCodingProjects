# A simple Makefile
# Jafar Moukalled	G01110547
# CS262, Lab Section 223
# Craps

CC = gcc

CFLAGS=-g -Wall -std=c89 -pedantic-errors
TARGET=Craps
LIBS=-lm
all: $(TARGET)
	$(CC) $(CFLAGS) -o $(TARGET) $(TARGET).c $(LIBS)

clean:
	rm $(TARGET)
