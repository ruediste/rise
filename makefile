.PHONY: all images

all: images

images: $(patsubst %.uml,%.png, $(shell find -type f -name '*.uml'))

%.png: %.uml
	java -jar plantuml.jar $<